package org.innovateuk.ifs.project.documents.transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competitionsetup.repository.ProjectDocumentConfigRepository;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_ALREADY_COMPLETE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_ACCEPTED_OR_REJECTED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_DELETED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DOCUMENT_INVALID_DECISION;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DOCUMENT_NOT_YET_UPLOADED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.APPROVED;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.REJECTED;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.SUBMITTED;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.UPLOADED;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
/**
 * Transactional and secure service for Project Documents processing work
 */
public class DocumentsServiceImpl extends AbstractProjectServiceImpl implements DocumentsService {

    @Autowired
    private ProjectDocumentConfigRepository projectDocumentConfigRepository;

    @Autowired
    private ProjectDocumentRepository projectDocumentRepository;

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private GrantOfferLetterService grantOfferLetterService;

    private static final String PDF_FILE_TYPE = "PDF";
    private static final String SPREADSHEET_FILE_TYPE = "Spreadsheet";
    private static final String PDF_MEDIA_TYPE = "application/pdf";
    private static final List<String> SPREADSHEET_MEDIA_TYPE_LIST = Arrays.asList("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.oasis.opendocument.spreadsheet");
    @Override
    public ServiceResult<List<String>> getValidMediaTypesForDocument(long documentConfigId) {
        return getProjectDocumentConfig(documentConfigId)
                .andOnSuccessReturn(projectDocumentConfig -> getMediaTypes(projectDocumentConfig.getFileTypes()));
    }

    private ServiceResult<org.innovateuk.ifs.competitionsetup.domain.ProjectDocument> getProjectDocumentConfig(final long documentConfigId) {
        return find(projectDocumentConfigRepository.findOne(documentConfigId), notFoundError(org.innovateuk.ifs.competitionsetup.domain.ProjectDocument.class, documentConfigId));
    }

    private List<String> getMediaTypes(List<FileType> fileTypes) {
        List<String> validMediaTypes = new ArrayList<>();

        for (FileType fileType : fileTypes) {
            switch (fileType.getName()) {
                case PDF_FILE_TYPE:
                    validMediaTypes.add(PDF_MEDIA_TYPE);
                    break;
                case SPREADSHEET_FILE_TYPE:
                    validMediaTypes.addAll(SPREADSHEET_MEDIA_TYPE_LIST);
                    break;
            }
        }
        return validMediaTypes;
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createDocumentFileEntry(long projectId, long documentConfigId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return find(getProject(projectId), getProjectDocumentConfig(documentConfigId)).
                andOnSuccess((project, projectDocumentConfig) -> validateProjectIsInSetup(project)
                        .andOnSuccess(() -> fileService.createFile(fileEntryResource, inputStreamSupplier))
                        .andOnSuccessReturn(fileDetails -> createProjectDocument(project, projectDocumentConfig, fileDetails)));
    }

    private ServiceResult<Void> validateProjectIsInSetup(Project project) {
        if (!ProjectState.SETUP.equals(projectWorkflowHandler.getState(project))) {
            return serviceFailure(PROJECT_SETUP_ALREADY_COMPLETE);
        }

        return serviceSuccess();
    }

    private FileEntryResource createProjectDocument(Project project, org.innovateuk.ifs.competitionsetup.domain.ProjectDocument projectDocumentConfig, Pair<File, FileEntry> fileDetails) {

        FileEntry fileEntry = fileDetails.getValue();
        ProjectDocument projectDocument = new ProjectDocument(project, projectDocumentConfig, fileEntry, UPLOADED);
        projectDocumentRepository.save(projectDocument);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    @Override
    public ServiceResult<FileAndContents> getFileContents(long projectId, long documentConfigId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = getFileEntry(project, documentConfigId);

            return fileService.getFileByFileEntryId(fileEntry.getId())
                .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), inputStream));
        });
    }

    private FileEntry getFileEntry(Project project, long documentConfigId) {
        return getProjectDocument(project, documentConfigId)
                .getFileEntry();
    }

    private ProjectDocument getProjectDocument(Project project, long documentConfigId) {
        return simpleFindAny(project.getProjectDocuments(), projectDocument -> projectDocument.getProjectDocument().getId().equals(documentConfigId))
                .get();
    }

    @Override
    public ServiceResult<FileEntryResource> getFileEntryDetails(long projectId, long documentConfigId) {
        return getProject(projectId)
                .andOnSuccessReturn(project -> fileEntryMapper.mapToResource(getFileEntry(project, documentConfigId)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteDocument(long projectId, long documentConfigId) {
        return find(getProject(projectId), getProjectDocumentConfig(documentConfigId)).
                andOnSuccess((project, projectDocumentConfig) -> validateProjectIsInSetup(project)
                                .andOnSuccess(() -> deleteProjectDocument(project, documentConfigId))
                                .andOnSuccess(() -> deleteFile(project, documentConfigId))
                            );
    }

    private ServiceResult<Void> deleteProjectDocument(Project project, long documentConfigId) {
        ProjectDocument projectDocumentToDelete = getProjectDocument(project, documentConfigId);
        if (Arrays.asList(APPROVED, SUBMITTED).contains(projectDocumentToDelete.getStatus())) {
            return serviceFailure(PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_DELETED);
        } else {
            projectDocumentRepository.delete(projectDocumentToDelete);
            return serviceSuccess();
        }
    }

    private void deleteFile(Project project, long documentConfigId) {
        FileEntry fileEntry = getFileEntry(project, documentConfigId);
        fileService.deleteFileIgnoreNotFound(fileEntry.getId());
    }

    @Override
    @Transactional
    public ServiceResult<Void> submitDocument(long projectId, long documentConfigId) {
        return find(getProject(projectId), getProjectDocumentConfig(documentConfigId)).
                andOnSuccess((project, projectDocumentConfig) -> validateProjectIsInSetup(project)
                        .andOnSuccess(() -> submitDocument(project, documentConfigId))
                );
    }

    private ServiceResult<Void> submitDocument(Project project, long documentConfigId) {
        ProjectDocument projectDocumentToBeSubmitted = getProjectDocument(project, documentConfigId);

        if (UPLOADED.equals(projectDocumentToBeSubmitted.getStatus())) {
            projectDocumentToBeSubmitted.setStatus(SUBMITTED);
            projectDocumentRepository.save(projectDocumentToBeSubmitted);
            return serviceSuccess();
        } else {
            return serviceFailure(PROJECT_SETUP_PROJECT_DOCUMENT_NOT_YET_UPLOADED);
        }
    }

    @Override
    @Transactional
    public ServiceResult<Void> documentDecision(long projectId, long documentConfigId, ProjectDocumentDecision decision) {

        return validateProjectDocumentDecision(decision)
                .andOnSuccess(() -> find(getProject(projectId), getProjectDocumentConfig(documentConfigId)).
                andOnSuccess((project, projectDocumentConfig) -> validateProjectIsInSetup(project)
                        .andOnSuccess(() -> applyDocumentDecision(project, documentConfigId, decision))
                        .andOnSuccess(() -> generateGrantOfferLetterIfReady(projectId))
                ));
    }

    private ServiceResult<Void> validateProjectDocumentDecision(ProjectDocumentDecision decision) {

        if (null == decision.getApproved()) {
            return serviceFailure(PROJECT_SETUP_PROJECT_DOCUMENT_INVALID_DECISION);
        } else if (!decision.getApproved()) {
            return StringUtils.isBlank(decision.getRejectionReason()) ? serviceFailure(PROJECT_SETUP_PROJECT_DOCUMENT_INVALID_DECISION) : serviceSuccess();
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> applyDocumentDecision(Project project, long documentConfigId, ProjectDocumentDecision decision) {
        ProjectDocument projectDocument = getProjectDocument(project, documentConfigId);

        if (SUBMITTED.equals(projectDocument.getStatus())) {
            projectDocument.setStatus(decision.getApproved()? APPROVED : REJECTED);
            projectDocument.setStatusComments(!decision.getApproved()? decision.getRejectionReason() : null);
            projectDocumentRepository.save(projectDocument);
            return serviceSuccess();
        } else {
            return serviceFailure(PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_ACCEPTED_OR_REJECTED);
        }
    }

    private ServiceResult<Void> generateGrantOfferLetterIfReady(Long projectId) {
        return grantOfferLetterService.generateGrantOfferLetterIfReady(projectId)
                .andOnFailure(() -> serviceFailure(GRANT_OFFER_LETTER_GENERATION_FAILURE));
    }
}
