package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReviewRejectOutcome;
import org.innovateuk.ifs.assessment.panel.mapper.AssessmentReviewMapper;
import org.innovateuk.ifs.assessment.panel.mapper.AssessmentReviewRejectOutcomeMapper;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.assessment.panel.workflow.configuration.AssessmentReviewWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.competition.AssessmentPanelParticipant;
import org.innovateuk.ifs.invite.repository.AssessmentPanelParticipantRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.CREATED;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REVIEW_ACCEPT_FAILED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REVIEW_REJECT_FAILED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.UserRoleType.PANEL_ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

@Service
@Transactional
public class AssessmentPanelServiceImpl implements AssessmentPanelService {

    static final DateTimeFormatter INVITE_DATE_FORMAT = ofPattern("d MMMM yyyy");

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AssessmentPanelParticipantRepository assessmentPanelParticipantRepository;

    @Autowired
    private AssessmentReviewWorkflowHandler workflowHandler;

    @Autowired
    private AssessmentReviewRepository assessmentReviewRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private AssessmentReviewMapper assessmentReviewMapper;

    @Autowired
    private AssessmentReviewRejectOutcomeMapper assessmentReviewRejectOutcomeMapper;

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

    @Override
    public ServiceResult<List<AssessmentReviewResource>> getAssessmentReviews(long userId, long competitionId) {
        List<AssessmentReview> assessmentReviews = assessmentReviewRepository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId);
        return serviceSuccess(simpleMap(assessmentReviews, assessmentReviewMapper::mapToResource));
    }

    @Override
    public ServiceResult<Void> acceptAssessmentReview(long assessmentReviewId) {
        return findAssessmentReview(assessmentReviewId).andOnSuccess(this::acceptAssessmentReview);
    }

    @Override
    public ServiceResult<Void> rejectAssessmentReview(long assessmentReviewId,
                                                      AssessmentReviewRejectOutcomeResource assessmentReviewRejectOutcome) {
        return findAssessmentReview(assessmentReviewId)
                .andOnSuccess(
                        r -> rejectAssessmentReview(r, assessmentReviewRejectOutcomeMapper.mapToDomain(assessmentReviewRejectOutcome)));
    }

    @Override
    public ServiceResult<AssessmentReviewResource> getAssessmentReview(long assessmentReviewId) {
        return find(assessmentReviewRepository.findOne(assessmentReviewId), notFoundError(AssessmentReviewResource.class, assessmentReviewId))
                .andOnSuccessReturn(assessmentReviewMapper::mapToResource);
    }

    private ServiceResult<AssessmentReview> findAssessmentReview(long assessmentReviewId) {
        return find(assessmentReviewRepository.findOne(assessmentReviewId), notFoundError(AssessmentReview.class, assessmentReviewId));
    }

    private ServiceResult<Void> acceptAssessmentReview(AssessmentReview assessmentReview) {
        if (!workflowHandler.acceptInvitation(assessmentReview)) {
            return serviceFailure(ASSESSMENT_REVIEW_ACCEPT_FAILED);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> rejectAssessmentReview(AssessmentReview assessmentReview, AssessmentReviewRejectOutcome rejectOutcome) {
        if (!workflowHandler.rejectInvitation(assessmentReview, rejectOutcome)) {
            return serviceFailure(ASSESSMENT_REVIEW_REJECT_FAILED);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> createAssessmentReview(AssessmentPanelParticipant assessor, Application application) {
        if (!assessmentReviewRepository.existsByParticipantUserAndTargetAndActivityStateStateNot(assessor.getUser(), application, State.WITHDRAWN)) {
            final Role panelAssessorRole = roleRepository.findOneByName(PANEL_ASSESSOR.getName());
            final ActivityState createdActivityState = activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.CREATED);

            AssessmentReview assessmentReview =  new AssessmentReview(application, assessor, panelAssessorRole);
            assessmentReview.setActivityState(createdActivityState);
            assessmentReviewRepository.save(assessmentReview);
        }
        return serviceSuccess();
    }

    private ServiceResult<Application> getApplication(long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId));
    }

    private List<AssessmentPanelParticipant> getAllAssessorsOnPanel(long competitionId) {
        return assessmentPanelParticipantRepository.getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(ParticipantStatus.ACCEPTED));}

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