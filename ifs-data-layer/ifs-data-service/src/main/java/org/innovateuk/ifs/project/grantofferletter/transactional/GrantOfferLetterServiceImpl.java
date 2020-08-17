package org.innovateuk.ifs.project.grantofferletter.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.docusign.resource.DocusignRequest;
import org.innovateuk.ifs.docusign.resource.DocusignType;
import org.innovateuk.ifs.docusign.transactional.DocusignService;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.string.resource.StringResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.docusign.resource.DocusignRequest.DocusignRequestBuilder.aDocusignRequest;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.resource.ProjectState.ON_HOLD;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

@Service
public class GrantOfferLetterServiceImpl extends BaseTransactionalService implements GrantOfferLetterService {

    private static final Log LOG = LogFactory.getLog(GrantOfferLetterServiceImpl.class);

    private static final String GOL_STATE_ERROR = "Set Grant Offer Letter workflow status to sent failed for project %s";

    private static final String PROJECT_STATE_ERROR = "Set project status to live failed for project %s";

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private DocusignService docusignService;

    @Autowired
    private GrantOfferLetterWorkflowHandler grantOfferLetterWorkflowHandler;


    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    public enum NotificationsGol {
        GRANT_OFFER_LETTER_PROJECT_MANAGER,
        PROJECT_LIVE
    }

    @Override
    public ServiceResult<FileAndContents> getSignedGrantOfferLetterFileAndContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            FileEntry fileEntry = project.getSignedGrantOfferLetter();
            return getFileAndContentsResult(fileEntry);
        });
    }

    @Override
    public ServiceResult<FileAndContents> getGrantOfferLetterFileAndContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            FileEntry fileEntry = project.getGrantOfferLetter();
            return getFileAndContentsResult(fileEntry);
        });
    }

    @Override
    public ServiceResult<FileAndContents> getAdditionalContractFileAndContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            FileEntry fileEntry = project.getAdditionalContractFile();
            return getFileAndContentsResult(fileEntry);
        });
    }

    private FailingOrSucceedingResult<FileAndContents, ServiceFailure> getFileAndContentsResult(FileEntry fileEntry) {
        if (fileEntry == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        }

        ServiceResult<Supplier<InputStream>> getFileResult = fileService.getFileByFileEntryId(fileEntry.getId());
        return getFileResult.andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), inputStream));
    }

    @Override
    public ServiceResult<FileEntryResource> getSignedGrantOfferLetterFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getSignedGrantOfferLetter();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });

    }

    @Override
    public ServiceResult<FileEntryResource> getGrantOfferLetterFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getGrantOfferLetter();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });
    }

    @Override
    public ServiceResult<FileEntryResource> getAdditionalContractFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getAdditionalContractFile();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });

    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createSignedGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, true)));

    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, false)));
    }

    private FileEntryResource linkGrantOfferLetterFileToProject(Project project, Pair<File, FileEntry> fileDetails, boolean signed) {
        FileEntry fileEntry = fileDetails.getValue();

        if (signed) {
            project.setSignedGrantOfferLetter(fileEntry);
        } else {
            project.setGrantOfferLetter(fileEntry);
        }

        return fileEntryMapper.mapToResource(fileEntry);
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeGrantOfferLetterFileEntry(Long projectId) {
        return getProject(projectId).
                andOnSuccess(project -> validateProjectIsActive(project).
                        andOnSuccess(() -> validateRemoveGrantOfferLetter(project)).
                        andOnSuccess(() -> removeGrantOfferLetterFileFromProject(project)).
                        andOnSuccess(fileEntry -> fileService.deleteFileIgnoreNotFound(fileEntry.getId())).
                        andOnSuccessReturnVoid());
    }

    private ServiceResult<Void> validateRemoveGrantOfferLetter(Project project) {
        return getCurrentlyLoggedInUser().andOnSuccess(user ->
                golWorkflowHandler.removeGrantOfferLetter(project, user) ?
                        serviceSuccess() : serviceFailure(GRANT_OFFER_LETTER_CANNOT_BE_REMOVED));
    }

    private ServiceResult<FileEntry> removeGrantOfferLetterFileFromProject(Project project) {

        return validateProjectIsActive(project).andOnSuccessReturn(() -> {
            FileEntry fileEntry = project.getGrantOfferLetter();
            project.setGrantOfferLetter(null);
            return fileEntry;
        });

    }

    @Override
    @Transactional
    public ServiceResult<Void> removeSignedGrantOfferLetterFileEntry(Long projectId) {
        return getProject(projectId).
                andOnSuccess(this::validateProjectIsActive).
                andOnSuccess(project -> getCurrentlyLoggedInUser().
                andOnSuccess(user -> removeSignedGrantOfferLetterIfAllowed(project, user).
                andOnSuccessReturnVoid()));
    }

    private ServiceResult<FileEntry> removeSignedGrantOfferLetterIfAllowed(Project project, User user) {

        if (!golWorkflowHandler.removeSignedGrantOfferLetter(project, user)) {
            return serviceFailure(GRANT_OFFER_LETTER_CANNOT_BE_REMOVED);
        }

        return removeSignedGrantOfferLetterFileFromProject(project).
               andOnSuccess(fileEntry -> fileService.deleteFileIgnoreNotFound(fileEntry.getId()));
    }

    private ServiceResult<FileEntry> removeSignedGrantOfferLetterFileFromProject(Project project) {
        return validateProjectIsActive(project).andOnSuccessReturn(() -> {
            FileEntry fileEntry = project.getSignedGrantOfferLetter();
            project.setSignedGrantOfferLetter(null);
            return fileEntry;
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> resetGrantOfferLetterFileEntry(Long projectId) {
        Project project = getProject(projectId).getSuccess();

        if (project.getGrantOfferLetter() != null) {
            fileService.deleteFileIgnoreNotFound(project.getGrantOfferLetter().getId());
            project.setGrantOfferLetter(null);
        }
        if (project.getAdditionalContractFile() != null) {
            fileService.deleteFileIgnoreNotFound(project.getAdditionalContractFile().getId());
            project.setAdditionalContractFile(null);
        }
        grantOfferLetterWorkflowHandler.grantOfferLetterReset(project, getCurrentlyLoggedInUser().getSuccess());
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createAdditionalContractFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkAdditionalContractFileToProject(project, fileDetails)));

    }

    private FileEntryResource linkAdditionalContractFileToProject(Project project, Pair<File, FileEntry> fileDetails) {
        FileEntry fileEntry = fileDetails.getValue();
        project.setAdditionalContractFile(fileEntry);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateSignedGrantOfferLetterFile(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).andOnSuccess(this::validateProjectIsActive).
                andOnSuccess(project -> {
                    if (golWorkflowHandler.isSent(project)) {
                        return fileService.updateFile(fileEntryResource, inputStreamSupplier).
                                andOnSuccessReturnVoid(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, true));
                    } else {
                        return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_SENT_BEFORE_UPLOADING_SIGNED_COPY);
                    }
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> submitGrantOfferLetter(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            if (project.getSignedGrantOfferLetter() == null) {
                return serviceFailure(CommonFailureKeys.SIGNED_GRANT_OFFER_LETTER_MUST_BE_UPLOADED_BEFORE_SUBMIT);
            }
            if (!golWorkflowHandler.sign(project)) {
                return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_CANNOT_SET_SIGNED_STATE);
            }
            project.setOfferSubmittedDate(ZonedDateTime.now());
            return serviceSuccess();
        });
    }

    private ServiceResult<Project> validateProjectIsActive(final Project project) {
        if (!projectWorkflowHandler.getState(project).isActive()) {
            return serviceFailure(PROJECT_SETUP_ALREADY_COMPLETE);
        }

        return serviceSuccess(project);
    }

    @Override
    @Transactional
    public ServiceResult<Void> sendGrantOfferLetter(Long projectId) {

        return getProject(projectId).andOnSuccess(project -> {
            if (project.getGrantOfferLetter() == null) {
                return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_AVAILABLE_BEFORE_SEND);
            }

            User projectManager = getExistingProjectManager(project).get().getUser();

            if (project.isUseDocusignForGrantOfferLetter()) {
                return sendGrantOfferLetterSuccess(project).andOnSuccess(() ->
                     docusignService.send(docusignRequest(projectManager, project))
                    .andOnSuccessReturnVoid((project::setSignedGolDocusignDocument)));
            } else {
                NotificationTarget pmTarget = createProjectManagerNotificationTarget(projectManager);

                Map<String, Object> notificationArguments = new HashMap<>();
                notificationArguments.put("dashboardUrl", webBaseUrl);
                notificationArguments.put("applicationId", project.getApplication().getId());
                notificationArguments.put("competitionName", project.getApplication().getCompetition().getName());

                return sendGrantOfferLetterSuccess(project).andOnSuccess(() -> {
                    Notification notification = new Notification(systemNotificationSource,
                            singletonList(pmTarget),
                            NotificationsGol.GRANT_OFFER_LETTER_PROJECT_MANAGER,
                            notificationArguments);

                    return notificationService.sendNotificationWithFlush(notification, EMAIL);
                });
            }
        });
    }

    private ServiceResult<Void> sendGrantOfferLetterSuccess(Project project) {

        return getCurrentlyLoggedInUser().andOnSuccess(user -> {

            if (golWorkflowHandler.grantOfferLetterSent(project, user)) {
                return serviceSuccess();
            } else {
                LOG.error(String.format(GOL_STATE_ERROR, project.getId()));
                return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
            }
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, GrantOfferLetterApprovalResource grantOfferLetterApprovalResource) {

        return validateApprovalOrRejection(grantOfferLetterApprovalResource).andOnSuccess(() ->
            getProject(projectId).andOnSuccess(project -> {
                if (golWorkflowHandler.isReadyToApprove(project)) {
                    if (ApprovalType.APPROVED.equals(grantOfferLetterApprovalResource.getApprovalType()) && !isOnHold(project)) {
                        return approveGOL(project)
                                .andOnSuccess(() -> moveProjectToLiveState(project));
                    } else if (ApprovalType.REJECTED.equals(grantOfferLetterApprovalResource.getApprovalType())) {
                        return rejectGOL(project, grantOfferLetterApprovalResource.getRejectionReason());
                    }
                }
                return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_NOT_READY_TO_APPROVE);
        }));
    }

    private boolean isOnHold(Project project) {
        return ON_HOLD.equals(project.getProjectProcess().getProcessState());
    }

    private ServiceResult<Void> validateApprovalOrRejection(GrantOfferLetterApprovalResource grantOfferLetterApprovalResource) {
        if (ApprovalType.REJECTED.equals(grantOfferLetterApprovalResource.getApprovalType())) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(grantOfferLetterApprovalResource.getRejectionReason())) {
                return serviceSuccess();
            }
        } else if (ApprovalType.APPROVED.equals(grantOfferLetterApprovalResource.getApprovalType())) {
            return serviceSuccess();
        }

        return serviceFailure(GENERAL_INVALID_ARGUMENT);
    }

    private ServiceResult<Void> moveProjectToLiveState(Project project) {

        if (!projectWorkflowHandler.grantOfferLetterApproved(project, project.getProjectUsersWithRole(PROJECT_MANAGER).get(0))) {
            LOG.error(String.format(PROJECT_STATE_ERROR, project.getId()));
            return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
        }
        return notifyProjectIsLive(project.getId());
    }

    private ServiceResult<Void> approveGOL(Project project) {

        return getCurrentlyLoggedInUser().andOnSuccess(user -> {

            if (golWorkflowHandler.grantOfferLetterApproved(project, user)) {
                return serviceSuccess();
            } else {
                LOG.error(String.format(GOL_STATE_ERROR, project.getId()));
                return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
            }
        });
    }

    private ServiceResult<Void> rejectGOL(Project project, String golRejectionReason) {

        return getCurrentlyLoggedInUser().andOnSuccess(user -> {

            if (golWorkflowHandler.grantOfferLetterRejected(project, user)) {
                project.setOfferSubmittedDate(null);
                project.setGrantOfferLetterRejectionReason(golRejectionReason);
                if (project.isUseDocusignForGrantOfferLetter()) {
                    User projectManager = getExistingProjectManager(project).get().getUser();
                    docusignService.resend(project.getSignedGolDocusignDocument().getId(), docusignRequest(projectManager, project))
                            .andOnSuccessReturnVoid((project::setSignedGolDocusignDocument));
                }
                return serviceSuccess();
            } else {
                LOG.error(String.format(GOL_STATE_ERROR, project.getId()));
                return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
            }
        });
    }

    @Override
    public ServiceResult<GrantOfferLetterStateResource> getGrantOfferLetterState(Long projectId) {
        return getProject(projectId).andOnSuccess(
                golWorkflowHandler::getExtendedState);
    }

    @Override
    public ServiceResult<StringResource> getDocusignUrl(long projectId) {
        return getProject(projectId).andOnSuccessReturn(project -> {
            User projectManager = getExistingProjectManager(project).get().getUser();
            return new StringResource(docusignService.getDocusignUrl(project.getSignedGolDocusignDocument().getEnvelopeId(), projectManager.getId(),
                    projectManager.getName(), projectManager.getEmail(), String.format("/project-setup/project/%d/offer", projectId)));
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> importGrantOfferLetter(long projectId) {
        return getProject(projectId).andOnSuccess(project ->
            docusignService.importDocument(project.getSignedGolDocusignDocument().getEnvelopeId()));

    }


    private Optional<ProjectUser> getExistingProjectManager(Project project) {
        List<ProjectUser> projectUsers = project.getProjectUsers();
        List<ProjectUser> projectManagers = simpleFilter(projectUsers, pu -> pu.getRole().isProjectManager());
        return getOnlyElementOrEmpty(projectManagers);
    }

    private List<NotificationTarget> getLiveProjectNotificationTargets(Project project) {
        List<NotificationTarget> notificationTargets = new ArrayList<>();

        User projectManager = getExistingProjectManager(project).get().getUser();
        NotificationTarget projectManagerTarget = createProjectManagerNotificationTarget(projectManager);

        List<ProjectUser> financeContacts = simpleFilter(project.getProjectUsers(), pu -> pu.getRole().isFinanceContact());
        List<NotificationTarget> financeContactTargets = simpleMap(financeContacts, pu -> new UserNotificationTarget(pu.getUser().getName(), pu.getUser().getEmail()));

        List<NotificationTarget> uniqueFinanceContactTargets =
                simpleFilterNot(financeContactTargets, target -> target.getEmailAddress().equals(projectManager.getEmail()));

        notificationTargets.add(projectManagerTarget);
        notificationTargets.addAll(uniqueFinanceContactTargets);

        return notificationTargets;
    }

    private NotificationTarget createProjectManagerNotificationTarget(final User projectManager) {
        String fullName = getProjectManagerFullName(projectManager);

        return new UserNotificationTarget(fullName, projectManager.getEmail());
    }

    private String getProjectManagerFullName(User projectManager) {
        // At this stage, validation has already been done to ensure that first name and last name are not empty
        return projectManager.getFirstName() + " " + projectManager.getLastName();
    }

    private ServiceResult<Void> notifyProjectIsLive(Long projectId) {

        Project project = projectRepository.findById(projectId).get();
        List<NotificationTarget> notificationTargets = getLiveProjectNotificationTargets(project);

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationId", project.getApplication().getId());
        notificationArguments.put("projectName", project.getName());
        notificationArguments.put("projectStartDate", project.getTargetStartDate().format(DateTimeFormatter.ofPattern("d MMMM yyyy")));
        notificationArguments.put("projectSetupUrl", webBaseUrl + "/project-setup/project/" + project.getId());

        Notification notification = new Notification(systemNotificationSource, notificationTargets, NotificationsGol.PROJECT_LIVE, notificationArguments);

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private DocusignRequest docusignRequest(User projectManager, Project project) {
        return aDocusignRequest()
            .withRecipientUserId(projectManager.getId())
            .withName(projectManager.getName())
            .withEmail(projectManager.getEmail())
            .withSubject("Innovate UK sent you a document to review and sign")
            .withEmailBody(getEmailBody(projectManager.getName(), project.getApplication().getId(), project.getName()))
            .withFileAndContents(getGrantOfferLetterFileAndContents(project.getId()).getSuccess())
            .withDocusignType(DocusignType.SIGNED_GRANT_OFFER_LETTER)
            .withRedirectUrl(String.format("/project-setup/project/%d/offer", project.getId())).build();

    }
    private String getEmailBody(String name, long applicationId, String projectName) {
       return String.format("<p>Dear %s</p>" +
               "<p>Please sign the grant offer letter for application %d: %s.</p>" +
               "<p>The 'Review Document' link will take you to DocuSign's secure website, where you can safely sign the letter. It will then be submitted to us for approval and you will be notified when it has been approved.</p>" +
               "<p>Yours sincerely</p>" +
               "Innovate UK, part of UK Research and Innovation<br/>" +
               "Tel: 0300 321 4357<br/>" +
               "Email: competitions@innovateuk.ukri.org", name, applicationId, projectName);
    }
}