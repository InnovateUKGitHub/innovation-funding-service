package com.worth.ifs.application.transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.FileEntryResourceAssembler;
import com.worth.ifs.file.transactional.FileService;
import com.worth.ifs.finance.handler.ApplicationFinanceHandler;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.stream.Collectors.toList;

/**
 * Transactional and secured service focused around the processing of Applications
 */
@Service
public class ApplicationServiceImpl extends BaseTransactionalService implements ApplicationService {

    private static final Log LOG = LogFactory.getLog(ApplicationServiceImpl.class);

    // TODO DW - INFUND-1555 - put into a DTO
    public static final String READY_FOR_SUBMIT = "readyForSubmit";
    public static final String PROGRESS = "progress";
    public static final String RESEARCH_PARTICIPATION = "researchParticipation";
    public static final String RESEARCH_PARTICIPATION_VALID = "researchParticipationValid";
    public static final String ALL_SECTION_COMPLETE = "allSectionComplete";

    @Autowired
    private FileService fileService;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    private SectionService sectionService;

    @Override
    public ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, Long competitionId, Long userId) {

        return handlingErrors(() -> {

            return find(user(userId), competition(competitionId)).andOnSuccess((user, competition) -> {

               Application application = new Application();
               application.setName(applicationName);
               LocalDate currentDate = LocalDate.now();
               application.setStartDate(currentDate);

               String name = ApplicationStatusConstants.CREATED.getName();

               List<ApplicationStatus> applicationStatusList = applicationStatusRepository.findByName(name);
               ApplicationStatus applicationStatus = applicationStatusList.get(0);

               application.setApplicationStatus(applicationStatus);
               application.setDurationInMonths(3L);

               List<Role> roles = roleRepository.findByName(UserRoleType.LEADAPPLICANT.getName());
               Role role = roles.get(0);

               Organisation userOrganisation = user.getProcessRoles().get(0).getOrganisation();

               ProcessRole processRole = new ProcessRole(user, application, role, userOrganisation);

               List<ProcessRole> processRoles = new ArrayList<>();
               processRoles.add(processRole);

               application.setProcessRoles(processRoles);
               application.setCompetition(competition);

               applicationRepository.save(application);
               processRoleRepository.save(processRole);

               return serviceSuccess(applicationMapper.mapApplicationToResource(application));
           });
        });
    }

    @Override
    public ServiceResult<Pair<File, FormInputResponseFileEntryResource>> createFormInputResponseFileUpload(FormInputResponseFileEntryResource formInputResponseFile, Supplier<InputStream> inputStreamSupplier) {

        return handlingErrors(FILES_UNABLE_TO_CREATE_FILE, () -> {

            long applicationId = formInputResponseFile.getCompoundId().getApplicationId();
            long processRoleId = formInputResponseFile.getCompoundId().getProcessRoleId();
            long formInputId = formInputResponseFile.getCompoundId().getFormInputId();

            FormInputResponse existingResponse = formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(applicationId, processRoleId, formInputId);

            if (existingResponse != null && existingResponse.getFileEntry() != null) {
                return serviceFailure(new Error(FILES_FILE_ALREADY_LINKED_TO_FORM_INPUT_RESPONSE, existingResponse.getFileEntry().getId()));
            } else {

                return fileService.createFile(formInputResponseFile.getFileEntryResource(), inputStreamSupplier).andOnSuccess(successfulFile -> {

                    FileEntry fileEntry = successfulFile.getValue();

                    if (existingResponse != null) {

                        existingResponse.setFileEntry(fileEntry);
                        formInputResponseRepository.save(existingResponse);
                        FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(FileEntryResourceAssembler.valueOf(fileEntry), formInputResponseFile.getCompoundId());
                        return serviceSuccess(Pair.of(successfulFile.getKey(), fileEntryResource));

                    } else {

                        return getProcessRole(processRoleId).
                                andOnSuccess(processRole -> getFormInput(formInputId).
                                andOnSuccess(formInput -> getApplication(applicationId).
                                andOnSuccess(application -> {

                                    FormInputResponse newFormInputResponse = new FormInputResponse(LocalDateTime.now(), fileEntry, processRole, formInput, application);
                                    formInputResponseRepository.save(newFormInputResponse);
                                    FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(FileEntryResourceAssembler.valueOf(fileEntry), formInputId, applicationId, processRoleId);
                                    return serviceSuccess(Pair.of(successfulFile.getKey(), fileEntryResource));
                        })));
                    }
                });
            }
        });
    }

    @Override
    public ServiceResult<Pair<File, FormInputResponseFileEntryResource>> updateFormInputResponseFileUpload(FormInputResponseFileEntryResource formInputResponseFile, Supplier<InputStream> inputStreamSupplier) {

        return handlingErrors(FILES_UNABLE_TO_UPDATE_FILE, () -> {

            ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> existingFileResult =
                    getFormInputResponseFileUpload(formInputResponseFile.getCompoundId());

            return existingFileResult.andOnSuccess(existingFile -> {

                FormInputResponseFileEntryResource existingFormInputResource = existingFile.getKey();

                FileEntryResource existingFileResource = existingFormInputResource.getFileEntryResource();
                FileEntryResource updatedFileDetails = formInputResponseFile.getFileEntryResource();
                FileEntryResource updatedFileDetailsWithId = new FileEntryResource(existingFileResource.getId(), updatedFileDetails.getName(), updatedFileDetails.getMediaType(), updatedFileDetails.getFilesizeBytes());

                return fileService.updateFile(updatedFileDetailsWithId, inputStreamSupplier).andOnSuccess(updatedFile ->
                        serviceSuccess(Pair.of(updatedFile.getKey(), existingFormInputResource))
                );
            });
        });
    }

    @Override
    public ServiceResult<FormInputResponse> deleteFormInputResponseFileUpload(FormInputResponseFileEntryId formInputResponseFileId) {

        return handlingErrors(FILES_UNABLE_TO_DELETE_FILE, () -> {

            ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> existingFileResult =
                    getFormInputResponseFileUpload(formInputResponseFileId);

            return existingFileResult.andOnSuccess(existingFile -> {

                FormInputResponseFileEntryResource formInputFileEntryResource = existingFile.getKey();
                Long fileEntryId = formInputFileEntryResource.getFileEntryResource().getId();

                return fileService.deleteFile(fileEntryId).
                        andOnSuccess(deletedFile -> getFormInputResponse(formInputFileEntryResource.getCompoundId()).
                        andOnSuccess(this::unlinkFileEntryFromFormInputResponse).
                        andOnSuccess(ServiceResult::serviceSuccess)
                );
            });
        });
    }

    @Override
    public ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> getFormInputResponseFileUpload(FormInputResponseFileEntryId fileEntryId) {

        return handlingErrors(notFoundError(FileEntry.class, fileEntryId.getFormInputId(), fileEntryId.getApplicationId(), fileEntryId.getProcessRoleId()), () ->

                getFormInputResponse(fileEntryId).
                        andOnSuccess(formInputResponse -> fileService.getFileByFileEntryId(formInputResponse.getFileEntry().getId()).
                        andOnSuccess(inputStreamSupplier -> serviceSuccess(Pair.of(formInputResponseFileEntryResource(formInputResponse.getFileEntry(), fileEntryId), inputStreamSupplier))
        )));
    }

    private ServiceResult<FormInputResponse> unlinkFileEntryFromFormInputResponse(FormInputResponse formInputResponse) {
        formInputResponse.setFileEntry(null);
        FormInputResponse unlinkedResponse = formInputResponseRepository.save(formInputResponse);
        return serviceSuccess(unlinkedResponse);
    }

    private ServiceResult<FormInput> getFormInput(long formInputId) {
        return find(() -> formInputRepository.findOne(formInputId), notFoundError(FormInput.class, formInputId));
    }

    private FormInputResponseFileEntryResource formInputResponseFileEntryResource(FileEntry fileEntry, FormInputResponseFileEntryId fileEntryId) {
        FileEntryResource fileEntryResource = FileEntryResourceAssembler.valueOf(fileEntry);
        return new FormInputResponseFileEntryResource(fileEntryResource, fileEntryId.getFormInputId(), fileEntryId.getApplicationId(), fileEntryId.getProcessRoleId());
    }

    private ServiceResult<FormInputResponse> getFormInputResponse(FormInputResponseFileEntryId fileEntry) {
        Error formInputResponseNotFoundError = notFoundError(FormInputResponse.class, fileEntry.getApplicationId(), fileEntry.getProcessRoleId(), fileEntry.getFormInputId());
        return find(() -> formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(fileEntry.getApplicationId(), fileEntry.getProcessRoleId(), fileEntry.getFormInputId()), formInputResponseNotFoundError);
    }

    @Override
    public ServiceResult<ApplicationResource> getApplicationById(final Long id) {
        return super.getApplication(id).andOnSuccess(application ->
            serviceSuccess(applicationMapper.mapApplicationToResource(application))
        );
    }

    @Override
    public ServiceResult<List<ApplicationResource>> findAll() {
        return serviceSuccess(applicationsToResources(applicationRepository.findAll()));
    }

    @Override
    public ServiceResult<List<ApplicationResource>> findByUserId(final Long userId) {
        return getUser(userId).andOnSuccess(user -> {
            List<ProcessRole> roles = processRoleRepository.findByUser(user);
            List<Application> applications = simpleMap(roles, ProcessRole::getApplication);
            return serviceSuccess(applicationsToResources(applications));
        });
    }

    @Override
    public ServiceResult<ApplicationResource> saveApplicationDetails(final Long id, ApplicationResource application) {

        return handlingErrors(() -> {

            return getApplication(id).andOnSuccess(existingApplication -> {

                existingApplication.setName(application.getName());
                existingApplication.setDurationInMonths(application.getDurationInMonths());
                existingApplication.setStartDate(application.getStartDate());
                Application savedApplication = applicationRepository.save(existingApplication);
                return serviceSuccess(applicationMapper.mapApplicationToResource(savedApplication));
            });
        });
    }

    // TODO DW - INFUND-1555 - try to remove ObjectNode usage
    @Override
    public ServiceResult<ObjectNode> getProgressPercentageNodeByApplicationId(final Long applicationId) {

        return getProgressPercentageByApplicationId(applicationId).andOnSuccess(percentage -> {

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("completedPercentage", percentage);
            return serviceSuccess(node);
        });
    }

    @Override
    public ServiceResult<ApplicationResource> updateApplicationStatus(final Long id,
                                                         final Long statusId) {
        return handlingErrors(() -> {

            return find(application(id), applicationStatus(statusId)).andOnSuccess((application, applicationStatus) -> {

                application.setApplicationStatus(applicationStatus);
                applicationRepository.save(application);
                return serviceSuccess(applicationMapper.mapApplicationToResource(application));
            });
        });
    }


    @Override
    public ServiceResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(final Long competitionId,
                                                                             final Long userId,
                                                                             final UserRoleType role) {

        List<Application> allApps = applicationRepository.findAll();
        List<Application> filtered = simpleFilter(allApps, app -> app.getCompetition().getId().equals(competitionId) &&
                applicationContainsUserRole(app.getProcessRoles(), userId, role));
        List<ApplicationResource> filteredResource = applicationsToResources(filtered);
        return serviceSuccess(filteredResource);
    }

    private static boolean applicationContainsUserRole(List<ProcessRole> roles, final Long userId, UserRoleType role) {
        boolean contains = false;
        int i = 0;
        while (!contains && i < roles.size()) {
            contains = roles.get(i).getUser().getId().equals(userId) && roles.get(i).getRole().getName().equals(role.getName());
            i++;
        }

        return contains;
    }

    @Override
    public ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(
            final Long competitionId,
            final Long userId,
            final String applicationName) {

        return handlingErrors(() -> {

            return find(user(userId), competition(competitionId)).andOnSuccess((user, competition) -> {

               Application application = new Application();
               application.setName(applicationName);
               LocalDate currentDate = LocalDate.now();
               application.setStartDate(currentDate);

               String name = ApplicationStatusConstants.CREATED.getName();

               List<ApplicationStatus> applicationStatusList = applicationStatusRepository.findByName(name);
               ApplicationStatus applicationStatus = applicationStatusList.get(0);

               application.setApplicationStatus(applicationStatus);
               application.setDurationInMonths(3L);

               List<Role> roles = roleRepository.findByName("leadapplicant");
               Role role = roles.get(0);

               Organisation userOrganisation = user.getOrganisations().get(0);

               ProcessRole processRole = new ProcessRole(user, application, role, userOrganisation);

               List<ProcessRole> processRoles = new ArrayList<>();
               processRoles.add(processRole);

               application.setProcessRoles(processRoles);
               application.setCompetition(competition);

               Application createdApplication = applicationRepository.save(application);
               processRoleRepository.save(processRole);

               return serviceSuccess(applicationMapper.mapApplicationToResource(createdApplication));
            });
        });
    }

    @Override
    public ServiceResult<ApplicationResource> findByProcessRole(final Long id){

        return getProcessRole(id).andOnSuccess(processRole ->
            serviceSuccess(applicationMapper.mapApplicationToResource(processRole.getApplication()))
        );
    }

    // TODO DW - INFUND-1555 - try to remove the usage of ObjectNode
    @Override
    public ServiceResult<ObjectNode> applicationReadyForSubmit(Long id) {

        return find(application(id), () -> getProgressPercentageByApplicationId(id)).andOnSuccess((application, progressPercentage) -> {

            return sectionService.childSectionsAreCompleteForAllOrganisations(null, id, null).andOnSuccess(allSectionsComplete -> {

                Competition competition = application.getCompetition();
                double researchParticipation = applicationFinanceHandler.getResearchParticipationPercentage(id).doubleValue();

                boolean readyForSubmit = false;
                if (allSectionsComplete &&
                        progressPercentage == 100 &&
                        researchParticipation <= competition.getMaxResearchRatio()) {
                    readyForSubmit = true;
                }

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();
                node.put(READY_FOR_SUBMIT, readyForSubmit);
                node.put(PROGRESS, progressPercentage);
                node.put(RESEARCH_PARTICIPATION, researchParticipation);
                node.put(RESEARCH_PARTICIPATION_VALID, (researchParticipation <= competition.getMaxResearchRatio()));
                node.put(ALL_SECTION_COMPLETE, allSectionsComplete);
                return serviceSuccess(node);

            });
        });
    }

    // TODO DW - INFUND-1555 - deal with rest results
    private ServiceResult<Double> getProgressPercentageByApplicationId(final Long applicationId) {

        return getApplication(applicationId).andOnSuccess(application -> {

            List<Section> sections = application.getCompetition().getSections();

            List<Question> questions = sections.stream()
                    .flatMap(section -> section.getQuestions().stream())
                    .filter(Question::isMarkAsCompletedEnabled)
                    .collect(toList());

            List<ProcessRole> processRoles = application.getProcessRoles();

            Set<Organisation> organisations = processRoles.stream()
                    .filter(p -> p.getRole().getName().equals(UserRoleType.LEADAPPLICANT.getName()) || p.getRole().getName().equals(UserRoleType.APPLICANT.getName()) || p.getRole().getName().equals(UserRoleType.COLLABORATOR.getName()))
                    .map(ProcessRole::getOrganisation).collect(Collectors.toSet());

            Long countMultipleStatusQuestionsCompleted = organisations.stream()
                    .mapToLong(org -> questions.stream()
                            .filter(Question::getMarkAsCompletedEnabled)
                            .filter(q -> q.hasMultipleStatuses() && questionService.isMarkedAsComplete(q, applicationId, org.getId()).getSuccessObject()).count())
                    .sum();

            Long countSingleStatusQuestionsCompleted = questions.stream()
                    .filter(Question::getMarkAsCompletedEnabled)
                    .filter(q -> !q.hasMultipleStatuses() && questionService.isMarkedAsComplete(q, applicationId, 0L).getSuccessObject()).count();

            Long countCompleted = countMultipleStatusQuestionsCompleted + countSingleStatusQuestionsCompleted;

            Long totalMultipleStatusQuestions = questions.stream().filter(Question::hasMultipleStatuses).count() * organisations.size();
            Long totalSingleStatusQuestions = questions.stream().filter(q -> !q.hasMultipleStatuses()).count();

            Long totalQuestions = totalMultipleStatusQuestions + totalSingleStatusQuestions;
            LOG.info("Total questions" + totalQuestions);
            LOG.info("Total completed questions" + countCompleted);

            if (questions.isEmpty()) {
                return serviceSuccess(Double.valueOf(0));
            } else {
                return serviceSuccess((100.0 / totalQuestions) * countCompleted);
            }
        });
    }

    private List<ApplicationResource> applicationsToResources(List<Application> filtered) {
        return simpleMap(filtered, application -> applicationMapper.mapApplicationToResource(application));
    }
}
