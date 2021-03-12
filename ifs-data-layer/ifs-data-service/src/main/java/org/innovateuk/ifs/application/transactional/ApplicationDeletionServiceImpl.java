package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationHiddenFromDashboard;
import org.innovateuk.ifs.application.domain.DeletedApplicationAudit;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.resource.ApplicationUserCompositeId;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectToBeCreatedRepository;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service focused around the processing of Applications.
 */
@Service
public class ApplicationDeletionServiceImpl extends RootTransactionalService implements ApplicationDeletionService {

    public enum Notifications {
        APPLICATION_DELETED
    }

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private ProcessHistoryRepository processHistoryRepository;

    @Autowired
    private DeletedApplicationRepository deletedApplicationRepository;

    @Autowired
    private ApplicationHiddenFromDashboardRepository applicationHiddenFromDashboardRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ApplicationOrganisationAddressRepository applicationOrganisationAddressRepository;

    @Autowired
    private AverageAssessorScoreRepository averageAssessorScoreRepository;

    @Autowired
    private EuGrantTransferRepository euGrantTransferRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectToBeCreatedRepository projectToBeCreatedRepository;

    @Autowired
    private GrantProcessRepository grantProcessRepository;

    @Autowired
    private ApplicationProcessRepository applicationProcessRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private SupporterAssignmentRepository supporterAssignmentRepository;

    @Autowired
    private ApplicationKtaInviteRepository applicationKtaInviteRepository;

    @Override
    @Transactional
    public ServiceResult<Void> deleteApplication(long applicationId) {
        Application application = getApplication(applicationId).getSuccess();
        List<ProcessRole> processRoles = processRoleRepository.findByApplicationId(application.getId());

        return deleteApplicationData(application).andOnSuccess(() -> {
            deletedApplicationRepository.save(new DeletedApplicationAudit(applicationId));
            return sendNotification(application, processRoles);
        });
    }

    private ServiceResult<Void> deleteApplicationData(Application application) {
        applicationFinanceRepository.deleteByApplicationId(application.getId());
        processRoleRepository.deleteByApplicationId(application.getId());
        formInputResponseRepository.deleteByApplicationId(application.getId());
        questionStatusRepository.deleteByApplicationId(application.getId());
        applicationHiddenFromDashboardRepository.deleteByApplicationId(application.getId());
        processHistoryRepository.deleteByProcessId(application.getApplicationProcess().getId());
        applicationRepository.delete(application);
        applicationInviteRepository.deleteAll(application.getInvites());

        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> deleteMigratedApplication(long applicationId) {
        Application application = getApplication(applicationId).getSuccess();

        activityLogRepository.deleteByApplicationId(application.getId());
        applicationFinanceRepository.deleteByApplicationId(application.getId());
        applicationOrganisationAddressRepository.deleteByApplicationId(application.getId());
        averageAssessorScoreRepository.deleteByApplicationId(application.getId());
        euGrantTransferRepository.deleteByApplicationId(application.getId());
        projectRepository.deleteByApplicationId(application.getId());
        projectToBeCreatedRepository.deleteByApplicationId(application.getId());
        grantProcessRepository.deleteByApplicationId(application.getId());
        processRoleRepository.deleteByApplicationId(application.getId());
        formInputResponseRepository.deleteByApplicationId(application.getId());
        questionStatusRepository.deleteByApplicationId(application.getId());
        /*applicationProcessRepository.findByTargetId(application.getId()).stream().forEach(
                applicationProcess -> processHistoryRepository.deleteByProcessId(applicationProcess.getId()));
        applicationProcessRepository.deleteByTargetId(application.getId());
        assessmentRepository.findByTargetId(application.getId()).stream().forEach(
                assessmentProcess -> processHistoryRepository.deleteByProcessId(assessmentProcess.getId()));
        assessmentRepository.deleteByTargetId(application.getId());
        interviewRepository.findByTargetId(application.getId()).stream().forEach(
                interviewProcess -> processHistoryRepository.deleteByProcessId(interviewProcess.getId()));
        interviewRepository.deleteByTargetId(application.getId());
        interviewAssignmentRepository.findByTargetId(application.getId()).stream().forEach(
                interviewAssignmentProcess -> processHistoryRepository.deleteByProcessId(interviewAssignmentProcess.getId()));
        interviewAssignmentRepository.deleteByTargetId(application.getId());
        reviewRepository.findByTargetId(application.getId()).stream().forEach(
                reviewProcess -> processHistoryRepository.deleteByProcessId(reviewProcess.getId()));
        reviewRepository.deleteByTargetId(application.getId());
        supporterAssignmentRepository.findByTargetId(application.getId()).stream().forEach(
                supporterAssignmentProcess -> processHistoryRepository.deleteByProcessId(supporterAssignmentProcess.getId()));
        supporterAssignmentRepository.deleteByTargetId(application.getId());*/
        applicationInviteRepository.deleteAll(application.getInvites());
        applicationKtaInviteRepository.deleteByApplicationId(application.getId());
        applicationRepository.delete(application);

        return serviceSuccess();
    }

    private ServiceResult<Void> sendNotification(Application application, List<ProcessRole> processRoles) {

        List<NotificationMessage> notificationMessages = processRoles.stream()
                .filter(ProcessRole::isCollaborator)
                .map(ProcessRole::getUser)
                .filter(User::isActive)
                .map(applicant -> new NotificationMessage(new UserNotificationTarget(applicant.getName(), applicant.getEmail())))
                .collect(Collectors.toList());

        User leadApplicant = processRoles.stream()
                .filter(ProcessRole::isLeadApplicant)
                .map(ProcessRole::getUser)
                .findFirst()
                .get();

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationName", application.getName());
        notificationArguments.put("leadUserName", leadApplicant.getName());
        notificationArguments.put("leadEmail", leadApplicant.getEmail());

        Notification notification = new Notification(systemNotificationSource,
                notificationMessages,
                Notifications.APPLICATION_DELETED,
                notificationArguments);

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    @Override
    @Transactional
    public ServiceResult<Void> hideApplicationFromDashboard(ApplicationUserCompositeId id) {
        return getApplication(id.getApplicationId()).andOnSuccessReturnVoid((application) ->
            getUser(id.getUserId()).andOnSuccessReturnVoid(user ->
                    applicationHiddenFromDashboardRepository.save(new ApplicationHiddenFromDashboard(application, user))));
    }

    private ServiceResult<Application> getApplication(long id) {
        return find(applicationRepository.findById(id), notFoundError(Application.class, id));
    }
}
