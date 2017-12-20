package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
import org.innovateuk.ifs.assessment.panel.workflow.configuration.AssessmentReviewWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.competition.AssessmentPanelParticipant;
import org.innovateuk.ifs.invite.repository.AssessmentPanelParticipantRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.CREATED;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.UserRoleType.PANEL_ASSESSOR;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

@Service
@Transactional
public class AssessmentPanelServiceImpl implements AssessmentPanelService {

    static final DateTimeFormatter INVITE_DATE_FORMAT = ofPattern("d MMMM yyyy");


    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AssessmentReviewWorkflowHandler workflowHandler;

    @Autowired
    private AssessmentReviewRepository assessmentReviewRepository;

    @Autowired
    private AssessmentPanelParticipantRepository assessmentPanelParticipantRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ProcessRoleRepository processsRoleRepository;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_ASSESSMENT_REVIEW
    }

    @Override
    public ServiceResult<Void> assignApplicationToPanel(long applicationId) {
        return getApplication(applicationId)
                .andOnSuccessReturnVoid(application -> application.setInAssessmentPanel(true));
    }

    @Override
    public ServiceResult<Void> unassignApplicationFromPanel(long applicationId) {
        return getApplication(applicationId)
                .andOnSuccess(this::unassignApplicationFromPanel)
                .andOnSuccessReturnVoid(this::withdrawAssessmentReviewsForApplication);
    }

    private ServiceResult<Application> unassignApplicationFromPanel(Application application) {
        application.setInAssessmentPanel(false);
        return serviceSuccess(application);
    }

    private ServiceResult<Void> withdrawAssessmentReviewsForApplication(Application application) {
        assessmentReviewRepository
                .findByTargetIdAndActivityStateStateNot(application.getId(), State.WITHDRAWN)
                .forEach(workflowHandler::withdraw);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> createAndNotifyReviews(long competitionId) {
        getAllAssessorsOnPanel(competitionId)
                .forEach(assessor -> getAllApplicationsOnPanel(competitionId)
                        .forEach(application -> createAssessmentReview(assessor, application)));

        // deliberately keeping this separate from creation in anticipation of splitting out notification
        return notifyAllCreated(competitionId);
    }


    @Override
    public ServiceResult<Boolean> isPendingReviewNotifications(long competitionId) {
        return serviceSuccess(assessmentReviewRepository.notifiable(competitionId));
    }

    private ServiceResult<Void> createAssessmentReview(AssessmentPanelParticipant assessor, Application application) {
        if (!assessmentReviewRepository.existsByParticipantUserAndTargetAndActivityStateStateNot(assessor.getUser(), application, State.WITHDRAWN)) {
            final ProcessRole processRole = createProcessRoleForAssessmentReview(assessor, application);
            AssessmentReview assessmentReview =  new AssessmentReview(application, processRole);
            assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.CREATED));
            assessmentReviewRepository.save(assessmentReview);
        }
        return serviceSuccess();
    }

    private ProcessRole createProcessRoleForAssessmentReview(AssessmentPanelParticipant assessor, Application application) {
        final Role assessorRole = roleRepository.findOneByName(PANEL_ASSESSOR.getName());
        return processsRoleRepository.save(new ProcessRole(assessor.getUser(), application.getId(), assessorRole, null));
    }

    private ServiceResult<Application> getApplication(long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId));
    }

    private List<AssessmentPanelParticipant> getAllAssessorsOnPanel(long competitionId) {
        return assessmentPanelParticipantRepository.getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(ParticipantStatus.ACCEPTED));
    }

    private List<Application> getAllApplicationsOnPanel(long competitionId) {
        return applicationRepository
                .findByCompetitionIdAndInAssessmentPanelTrueAndApplicationProcessActivityStateState(competitionId, State.SUBMITTED);
    }

    private ServiceResult<Void> notifyAllCreated(long competitionId) {
        assessmentReviewRepository
                .findByTargetCompetitionIdAndActivityStateState(competitionId, CREATED.getBackingState())
                .forEach(this::notifyInvitation);

        return serviceSuccess();
    }

    private void notifyInvitation(AssessmentReview assessmentReview) {
        workflowHandler.notifyInvitation(assessmentReview);
        sendInviteNotification("Applications ready for review", assessmentReview, Notifications.INVITE_ASSESSMENT_REVIEW);
    }

    private ServiceResult<Void> sendInviteNotification(String subject,
                                                       AssessmentReview assessmentReview,
                                                       Notifications notificationType) {
        NotificationTarget recipient = new UserNotificationTarget(assessmentReview.getParticipant().getUser());
        Notification notification = new Notification(
                systemNotificationSource,
                recipient,
                notificationType,
                asMap(
                        "subject", subject,
                        "name", assessmentReview.getParticipant().getUser().getName(),
                        "competitionName", assessmentReview.getTarget().getCompetition().getName(),
                        "panelDate", assessmentReview.getTarget().getCompetition().getAssessmentPanelDate().format(INVITE_DATE_FORMAT),
                        "ifsUrl", webBaseUrl
                ));

        return notificationSender.sendNotification(notification).andOnSuccessReturnVoid();
    }
}