package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResourceAssembler;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_ALREADY_UPLOADED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.state.ApplicationStateVerificationFunctions.verifyApplicationIsOpen;

/**
 * Service provides CRUD operation functions for {@FileEntry}s linked to {@FormInputReponse}s.
 */
@Service
public class ApplicationFormInputUploadServiceImpl extends BaseTransactionalService implements ApplicationFormInputUploadService {
    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private FileService fileService;

    private static final Log LOG = LogFactory.getLog(ApplicationServiceImpl.class);

    @Override
    @Transactional
    public ServiceResult<FormInputResponseFileEntryResource> createFormInputResponseFileUpload(FormInputResponseFileEntryResource formInputResponseFile,
                                                                                               Supplier<InputStream> inputStreamSupplier) {

        long applicationId = formInputResponseFile.getCompoundId().getApplicationId();
        long processRoleId = formInputResponseFile.getCompoundId().getProcessRoleId();
        long formInputId = formInputResponseFile.getCompoundId().getFormInputId();

        return findApplicationById(applicationId).andOnSuccess(
                foundApplication -> verifyApplicationIsOpen(foundApplication).andOnSuccess(
                        openApplication -> {
                            LOG.info("[FileLogging] Creating a new file for application id " + openApplication +
                                    " processRoleId " + processRoleId +
                                    " formInputId " + formInputId);

                            FormInputResponse existingResponse = formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(
                                    applicationId,
                                    processRoleId,
                                    formInputId);

                            // Removing and replacing if file already exists here
                            if (existingResponse != null && existingResponse.getFileEntries() != null
                              && existingResponse.getFileEntries().size() >= existingResponse.getFormInput().getWordCount()) {
                                LOG.info("[FileLogging] FormInputResponse for upload exceeds configured maximum of " + existingResponse.getFormInput().getWordCount() +
                                        " for application id " + openApplication +
                                        " processRoleId " + processRoleId +
                                        " formInputId " + formInputId +
                                        " , so returning error...");
                                return serviceFailure(new Error(FILES_ALREADY_UPLOADED));
                            }

                            return fileService.createFile(formInputResponseFile.getFileEntryResource(), inputStreamSupplier)
                                    .andOnSuccess(successfulFile ->
                                        createFormInputResponseFileUpload(
                                                successfulFile,
                                                existingResponse,
                                                processRoleId,
                                                applicationId,
                                                formInputId,
                                                formInputResponseFile
                                        )
                            );
                        }));
    }

    private ServiceResult<FormInputResponseFileEntryResource> createFormInputResponseFileUpload(Pair<File, FileEntry> successfulFile,
                                                                                                FormInputResponse existingResponse,
                                                                                                long processRoleId,
                                                                                                long applicationId,
                                                                                                long formInputId,
                                                                                                FormInputResponseFileEntryResource formInputResponseFile) {
        FileEntry fileEntry = successfulFile.getValue();

        if (existingResponse != null) {
            existingResponse.addFileEntry(fileEntry);
            formInputResponseRepository.save(existingResponse);
            FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(
                    FileEntryResourceAssembler.valueOf(fileEntry),
                    formInputResponseFile.getCompoundId()
            );
            return serviceSuccess(fileEntryResource);
        }

        return find(() -> findProcessRoleById(processRoleId),
                () -> findFormInputById(formInputId),
                () -> findApplicationById(applicationId))
                .andOnSuccess((processRole, formInput, application) -> {
                    FormInputResponse newFormInputResponse = new FormInputResponse(
                            ZonedDateTime.now(),
                            singletonList(fileEntry),
                            processRole,
                            formInput,
                            application
                    );
                    formInputResponseRepository.save(newFormInputResponse);
                    FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(
                            FileEntryResourceAssembler.valueOf(fileEntry),
                            formInputId,
                            applicationId,
                            processRoleId,
                            Optional.of(fileEntry.getId())
                    );
                    return serviceSuccess(fileEntryResource);
                });
    }

    @Override
    @Transactional
    public ServiceResult<FormInputResponse> deleteFormInputResponseFileUpload(FormInputResponseFileEntryId id) {
        return findApplicationById(id.getApplicationId())
                .andOnSuccess(foundApplication -> verifyApplicationIsOpen(foundApplication)
                    .andOnSuccess(openApplication -> deleteFormInputResponseFileUploadOnGetApplicationAndSuccess(id)));
    }

    private ServiceResult<FormInputResponse> deleteFormInputResponseFileUploadOnGetApplicationAndSuccess(FormInputResponseFileEntryId id) {
        return find(formInputRepository.findById(id.getFormInputId()), notFoundError(FormInput.class, id.getFormInputId())).andOnSuccess(
                formInput -> getFormInputResponseFileEntryResource(id, formInput)
                    .andOnSuccess(formInputResponseFileEntryResource -> {
                        LOG.info("[FileLogging] Deleting already existing FileEntryResource with id " +
                                formInputResponseFileEntryResource.getFileEntryResource().getId() +
                                " for application id " + formInputResponseFileEntryResource.getCompoundId().getApplicationId() +
                                " processRoleId " + formInputResponseFileEntryResource.getCompoundId().getProcessRoleId() +
                                " formInputId " + formInputResponseFileEntryResource.getCompoundId().getFormInputId() +
                                " deleted successfully");

                        boolean questionHasMultipleStatuses = questionHasMultipleStatuses(formInput);
                        return fileService.deleteFileIgnoreNotFound(formInputResponseFileEntryResource.getFileEntryResource().getId()).
                            andOnSuccess(deletedFile -> {
                                if (questionHasMultipleStatuses) {
                                    return getFormInputResponse(formInputResponseFileEntryResource.getCompoundId());
                                } else {
                                    return getFormInputResponseForQuestionAssignee(formInputResponseFileEntryResource.getCompoundId());
                                }
                            }).andOnSuccess(resp -> unlinkFileEntryFromFormInputResponse(resp, id));
                    })
        );
    }

    @Override
    public ServiceResult<FormInputResponseFileAndContents> getFormInputResponseFileUpload(FormInputResponseFileEntryId id) {
        return find(formInputRepository.findById(id.getFormInputId()), notFoundError(FormInput.class, id.getFormInputId())).
                andOnSuccess(formInput -> getAppropriateFormInputResponse(id, formInput).
                    andOnSuccess(formInputResponse -> {
                                FileEntry fileEntry = getFileEntry(formInputResponse, id);
                                return fileService.getFileByFileEntryId(fileEntry.getId()).
                                    andOnSuccessReturn(inputStreamSupplier -> {
                                        FormInputResponseFileEntryResource formInputResponseFileEntry =
                                                formInputResponseFileEntryResource(fileEntry, id);
                                        return new FormInputResponseFileAndContents(formInputResponseFileEntry, inputStreamSupplier);
                                    });
                            }
                    ));
    }

    private FileEntry getFileEntry(FormInputResponse response, FormInputResponseFileEntryId id) {
        if (id.getFileEntryId().isPresent()) {
            return response.getFileEntries().stream()
                    .filter(file -> file.getId().equals(id.getFileEntryId().get()))
                    .findFirst()
                    .orElseThrow(() -> new ObjectNotFoundException(singletonList("Unknown file entry " + id.getFileEntryId().get())));
        } else {
            return response.getFileEntries().get(0);
        }
    }

    private ServiceResult<FormInputResponse> unlinkFileEntryFromFormInputResponse(FormInputResponse formInputResponse, FormInputResponseFileEntryId id) {
        if (id.getFileEntryId().isPresent()) {
            formInputResponse.getFileEntries().removeIf(file -> file.getId().equals(id.getFileEntryId().get()));
        } else {
            formInputResponse.getFileEntries().clear();
        }
        FormInputResponse unlinkedResponse = formInputResponseRepository.save(formInputResponse);
        LOG.info("[FileLogging] Deleting FormInputResponse with id " + unlinkedResponse.getId() +
                " and application " + formInputResponse.getApplication());
        if (formInputResponse.getFileEntries().isEmpty()) {
            formInputResponseRepository.delete(formInputResponse);
            LOG.info("[FileLogging] FormInputResponse with id " + unlinkedResponse.getId() + " deleted");
        }
        return serviceSuccess(unlinkedResponse);
    }

    private boolean questionHasMultipleStatuses(@NotNull FormInput formInput) {
        Question question = formInput.getQuestion();
        return question.hasMultipleStatuses();
    }

    private ServiceResult<FormInputResponseFileEntryResource> getFormInputResponseFileEntryResource(FormInputResponseFileEntryId id,
                                                                                                    FormInput formInput) {
        return getAppropriateFormInputResponse(id, formInput)
                .andOnSuccess(formInputResponse -> serviceSuccess(
                        formInputResponseFileEntryResource(getFileEntry(formInputResponse, id), id))
                );
    }

    private ServiceResult<FormInputResponse> getAppropriateFormInputResponse(FormInputResponseFileEntryId fileEntry, FormInput formInput){
        boolean hasMultipleStatuses = questionHasMultipleStatuses(formInput);

        if (hasMultipleStatuses) {
            return getFormInputResponse(fileEntry);
        } else {
            return getFormInputResponseForQuestionAssignee(fileEntry);
        }
    }

    private FormInputResponseFileEntryResource formInputResponseFileEntryResource(FileEntry fileEntry,
                                                                                  FormInputResponseFileEntryId fileEntryId) {
        FileEntryResource fileEntryResource = FileEntryResourceAssembler.valueOf(fileEntry);
        return new FormInputResponseFileEntryResource(fileEntryResource, fileEntryId);
    }

    private ServiceResult<FormInputResponse> getFormInputResponse(FormInputResponseFileEntryId fileEntry) {
        Error formInputResponseNotFoundError = notFoundError(
                FormInputResponse.class,
                fileEntry.getApplicationId(),
                fileEntry.getProcessRoleId(),
                fileEntry.getFormInputId());
        return find(formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(
                fileEntry.getApplicationId(),
                fileEntry.getProcessRoleId(),
                fileEntry.getFormInputId()),
                formInputResponseNotFoundError);
    }

    /**
     * Use this method for finding a form input response when a question has single status (shared across application)
     *
     * @param fileEntry - in this case the FormInputResponseFileEntryId will contain the id of person to whom the question is assigned.
     */
    private ServiceResult<FormInputResponse> getFormInputResponseForQuestionAssignee(FormInputResponseFileEntryId fileEntry) {
        Error formInputResponseNotFoundError = notFoundError(
                FormInputResponse.class,
                fileEntry.getApplicationId(),
                fileEntry.getProcessRoleId(),
                fileEntry.getFormInputId()
        );
        List<FormInputResponse> formInputResponses = formInputResponseRepository.findByApplicationIdAndFormInputId(
                fileEntry.getApplicationId(),
                fileEntry.getFormInputId()
        );
        if (formInputResponses != null && !formInputResponses.isEmpty()) {
            return serviceSuccess(formInputResponses.get(0));
        }
        return serviceFailure(formInputResponseNotFoundError);
    }

    private ServiceResult<Application> findApplicationById(Long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId));
    }

    private ServiceResult<ProcessRole> findProcessRoleById(Long processRoleId) {
        return find(processRoleRepository.findById(processRoleId), notFoundError(ProcessRole.class, processRoleId));
    }

    private ServiceResult<FormInput> findFormInputById(long formInputId) {
        return find(formInputRepository.findById(formInputId), notFoundError(FormInput.class, formInputId));
    }
}
