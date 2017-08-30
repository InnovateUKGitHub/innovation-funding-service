package org.innovateuk.ifs.project.otherdocuments.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.workflow.configuration.ProjectWorkflowHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_ALREADY_COMPLETE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_OTHER_DOCUMENTS_APPROVAL_DECISION_MUST_BE_PROVIDED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_OTHER_DOCUMENTS_HAVE_ALREADY_BEEN_APPROVED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_OTHER_DOCUMENTS_MUST_BE_UPLOADED_BEFORE_SUBMIT;
import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElementOrEmpty;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Service
/**
 * Transactional and secure service for Project Other Documents processing work
 */
public class OtherDocumentsServiceImpl extends AbstractProjectServiceImpl implements OtherDocumentsService {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    private GrantOfferLetterService grantOfferLetterService;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    @Transactional
    public ServiceResult<Void> saveDocumentsSubmitDateTime(Long projectId, ZonedDateTime date) {

        return getProject(projectId).andOnSuccess(project ->
                retrieveUploadedDocuments(projectId).handleSuccessOrFailure(
                        failure -> serviceFailure(PROJECT_SETUP_OTHER_DOCUMENTS_MUST_BE_UPLOADED_BEFORE_SUBMIT),
                        success -> setDocumentsSubmittedDate(project, date)));
    }

    private ServiceResult<List<FileEntryResource>> retrieveUploadedDocuments(Long projectId) {

        ServiceResult<FileEntryResource> collaborationAgreementFile = getCollaborationAgreementFileEntryDetails(projectId);
        ServiceResult<FileEntryResource> exploitationPlanFile = getExploitationPlanFileEntryDetails(projectId);

        return aggregate(asList(collaborationAgreementFile, exploitationPlanFile));
    }

    @Override
    public ServiceResult<FileEntryResource> getCollaborationAgreementFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getCollaborationAgreement();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });
    }

    @Override
    public ServiceResult<FileEntryResource> getExploitationPlanFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getExploitationPlan();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });
    }

    private ServiceResult<Void> setDocumentsSubmittedDate(Project project, ZonedDateTime date) {
        project.setDocumentsSubmittedDate(date);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Boolean> isOtherDocumentsSubmitAllowed(Long projectId, Long userId) {

        ServiceResult<Project> project = getProject(projectId);
        Optional<ProjectUser> projectManager = getExistingProjectManager(project.getSuccessObject());

        return retrieveUploadedDocuments(projectId).handleSuccessOrFailure(
                failure -> serviceSuccess(false),
                success -> projectManager.isPresent() && projectManager.get().getUser().getId().equals(userId) && project.getSuccessObject().getDocumentsSubmittedDate() == null ?
                        serviceSuccess(true) :
                        serviceSuccess(false));
    }

    private Optional<ProjectUser> getExistingProjectManager(Project project) {
        List<ProjectUser> projectUsers = project.getProjectUsers();
        List<ProjectUser> projectManagers = simpleFilter(projectUsers, pu -> pu.getRole().isProjectManager());
        return getOnlyElementOrEmpty(projectManagers);
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createCollaborationAgreementFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkCollaborationAgreementFileToProject(project, fileDetails)));
    }

    private FileEntryResource linkCollaborationAgreementFileToProject(Project project, Pair<File, FileEntry> fileDetails) {
        FileEntry fileEntry = fileDetails.getValue();
        linkCollaborationAgreementFileEntryToProject(fileEntry, project);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    private void linkCollaborationAgreementFileEntryToProject(FileEntry fileEntry, Project project) {
        project.setOtherDocumentsApproved(ApprovalType.UNSET);
        project.setCollaborationAgreement(fileEntry);
    }

    @Override
    public ServiceResult<FileAndContents> getCollaborationAgreementFileContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getCollaborationAgreement();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            ServiceResult<Supplier<InputStream>> getFileResult = fileService.getFileByFileEntryId(fileEntry.getId());
            return getFileResult.andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), inputStream));
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateCollaborationAgreementFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(this::validateProjectIsInSetup).
                andOnSuccess(project -> fileService.updateFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturnVoid(fileDetails -> linkCollaborationAgreementFileToProject(project, fileDetails)));
    }

    private ServiceResult<Project> validateProjectIsInSetup(final Project project) {
        if(!ProjectState.SETUP.equals(projectWorkflowHandler.getState(project))) {
            return serviceFailure(PROJECT_SETUP_ALREADY_COMPLETE);
        }

        return serviceSuccess(project);
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteCollaborationAgreementFile(Long projectId) {
        return getProject(projectId).
                andOnSuccess(this::validateProjectIsInSetup).
                andOnSuccess(project ->
                getCollaborationAgreement(project).andOnSuccess(fileEntry ->
                        fileService.deleteFileIgnoreNotFound(fileEntry.getId()).andOnSuccessReturnVoid(() ->
                                removeCollaborationAgreementFileFromProject(project))));
    }

    private ServiceResult<FileEntry> getCollaborationAgreement(Project project) {
        if (project.getCollaborationAgreement() == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        } else {
            return serviceSuccess(project.getCollaborationAgreement());
        }
    }

    private void removeCollaborationAgreementFileFromProject(Project project) {
        validateProjectIsInSetup(project).
                andOnSuccess(() -> project.setCollaborationAgreement(null));
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createExploitationPlanFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkExploitationPlanFileToProject(project, fileDetails)));
    }

    private FileEntryResource linkExploitationPlanFileToProject(Project project, Pair<File, FileEntry> fileDetails) {
        FileEntry fileEntry = fileDetails.getValue();
        linkExploitationPlanFileEntryToProject(fileEntry, project);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    private void linkExploitationPlanFileEntryToProject(FileEntry fileEntry, Project project) {
        project.setOtherDocumentsApproved(ApprovalType.UNSET);
        project.setExploitationPlan(fileEntry);
    }

    @Override
    public ServiceResult<FileAndContents> getExploitationPlanFileContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getExploitationPlan();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            ServiceResult<Supplier<InputStream>> getFileResult = fileService.getFileByFileEntryId(fileEntry.getId());
            return getFileResult.andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), inputStream));
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateExploitationPlanFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(this::validateProjectIsInSetup).
                andOnSuccess(project -> fileService.updateFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturnVoid(fileDetails -> linkExploitationPlanFileToProject(project, fileDetails)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteExploitationPlanFile(Long projectId) {
        return getProject(projectId).
        andOnSuccess(this::validateProjectIsInSetup).andOnSuccess(project ->
                getExploitationPlan(project).andOnSuccess(fileEntry ->
                        fileService.deleteFileIgnoreNotFound(fileEntry.getId()).andOnSuccessReturnVoid(() ->
                                removeExploitationPlanFileFromProject(project))));
    }

    private ServiceResult<FileEntry> getExploitationPlan(Project project) {
        if (project.getExploitationPlan() == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        } else {
            return serviceSuccess(project.getExploitationPlan());
        }
    }

    private void removeExploitationPlanFileFromProject(Project project) {

        validateProjectIsInSetup(project).
                andOnSuccess(() -> project.setExploitationPlan(null));
    }

    @Override
    @Transactional
    public ServiceResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approval) {
        //TODO INFUND-7493
        if (approval == null) {
            return serviceFailure(PROJECT_SETUP_OTHER_DOCUMENTS_APPROVAL_DECISION_MUST_BE_PROVIDED);
        }
        return getProject(projectId)
                .andOnSuccess(project -> {
                    if (ApprovalType.APPROVED.equals(project.getOtherDocumentsApproved())) {
                        return serviceFailure(PROJECT_SETUP_OTHER_DOCUMENTS_HAVE_ALREADY_BEEN_APPROVED);
                    }
                    project.setOtherDocumentsApproved(approval ? ApprovalType.APPROVED : ApprovalType.REJECTED);
                    if (approval.equals(false)) {
                        project.setDocumentsSubmittedDate(null);
                    }
                    return grantOfferLetterService.generateGrantOfferLetterIfReady(projectId).andOnFailure(() -> serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE));
                });
    }
}
