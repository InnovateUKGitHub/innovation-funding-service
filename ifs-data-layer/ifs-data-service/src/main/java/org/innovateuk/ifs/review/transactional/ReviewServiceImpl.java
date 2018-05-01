package org.innovateuk.ifs.review.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.review.repository.ReviewParticipantRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;
import org.innovateuk.ifs.review.mapper.ReviewMapper;
import org.innovateuk.ifs.review.mapper.ReviewRejectOutcomeMapper;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.review.workflow.configuration.ReviewWorkflowHandler;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REVIEW_ACCEPT_FAILED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REVIEW_REJECT_FAILED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.review.resource.ReviewState.CREATED;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.review.domain.Review} data.
 */
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    static final DateTimeFormatter INVITE_DATE_FORMAT = ofPattern("d MMMM yyyy");

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ReviewParticipantRepository reviewParticipantRepository;

    @Autowired
    private ReviewWorkflowHandler workflowHandler;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private ReviewRejectOutcomeMapper reviewRejectOutcomeMapper;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_ASSESSMENT_REVIEW
    }

    @Override
    public ServiceResult<Void> assignApplicationToPanel(long applicationId) {
        return getApplication(applicationId)
                .andOnSuccessReturnVoid(application -> application.setInAssessmentReviewPanel(true));
    }

    @Override
    public ServiceResult<Void> unassignApplicationFromPanel(long applicationId) {
        return getApplication(applicationId)
                .andOnSuccess(this::unassignApplicationFromPanel)
                .andOnSuccessReturnVoid(this::withdrawAssessmentReviewsForApplication);
    }

    private ServiceResult<Application> unassignApplicationFromPanel(Application application) {
        application.setInAssessmentReviewPanel(false);
        return serviceSuccess(application);
    }

    private ServiceResult<Void> withdrawAssessmentReviewsForApplication(Application application) {
        reviewRepository
                .findByTargetIdAndActivityStateNot(application.getId(), ReviewState.WITHDRAWN)
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
        return serviceSuccess(reviewRepository.notifiable(competitionId));
    }

    @Override
    public ServiceResult<List<ReviewResource>> getReviews(long userId, long competitionId) {
        List<Review> reviews = reviewRepository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(userId, competitionId);
        return serviceSuccess(simpleMap(reviews, reviewMapper::mapToResource));
    }

    @Override
    public ServiceResult<Void> acceptReview(long assessmentReviewId) {
        return findAssessmentReview(assessmentReviewId).andOnSuccess(this::acceptAssessmentReview);
    }

    @Override
    public ServiceResult<Void> rejectReview(long assessmentReviewId,
                                            ReviewRejectOutcomeResource assessmentReviewRejectOutcome) {
        return findAssessmentReview(assessmentReviewId)
                .andOnSuccess(
                        r -> rejectAssessmentReview(r, reviewRejectOutcomeMapper.mapToDomain(assessmentReviewRejectOutcome)));
    }

    @Override
    public ServiceResult<ReviewResource> getReview(long assessmentReviewId) {
        return find(reviewRepository.findOne(assessmentReviewId), notFoundError(ReviewResource.class, assessmentReviewId))
                .andOnSuccessReturn(reviewMapper::mapToResource);
    }

    private ServiceResult<Review> findAssessmentReview(long assessmentReviewId) {
        return find(reviewRepository.findOne(assessmentReviewId), notFoundError(Review.class, assessmentReviewId));
    }

    private ServiceResult<Void> acceptAssessmentReview(Review review) {
        if (!workflowHandler.acceptInvitation(review)) {
            return serviceFailure(ASSESSMENT_REVIEW_ACCEPT_FAILED);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> rejectAssessmentReview(Review review, ReviewRejectOutcome rejectOutcome) {
        if (!workflowHandler.rejectInvitation(review, rejectOutcome)) {
            return serviceFailure(ASSESSMENT_REVIEW_REJECT_FAILED);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> createAssessmentReview(ReviewParticipant assessor, Application application) {
        if (!reviewRepository.existsByParticipantUserAndTargetAndActivityStateNot(assessor.getUser(), application, ReviewState.WITHDRAWN)) {
            Review review =  new Review(application, assessor);
            review.setProcessState(ReviewState.CREATED);
            reviewRepository.save(review);
        }
        return serviceSuccess();
    }

    private ServiceResult<Application> getApplication(long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId));
    }

    private List<ReviewParticipant> getAllAssessorsOnPanel(long competitionId) {
        return reviewParticipantRepository.getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(ParticipantStatus.ACCEPTED));}

    private List<Application> getAllApplicationsOnPanel(long competitionId) {
        return applicationRepository
                .findByCompetitionIdAndInAssessmentReviewPanelTrueAndApplicationProcessActivityState(competitionId, ApplicationState.SUBMITTED);
    }

    private ServiceResult<Void> notifyAllCreated(long competitionId) {
        reviewRepository
                .findByTargetCompetitionIdAndActivityState(competitionId, CREATED)
                .forEach(this::notifyInvitation);

        return serviceSuccess();
    }

    private void notifyInvitation(Review review) {
        workflowHandler.notifyInvitation(review);
        sendInviteNotification("Applications ready for review", review, Notifications.INVITE_ASSESSMENT_REVIEW);
    }

    private ServiceResult<Void> sendInviteNotification(String subject,
                                                       Review review,
                                                       Notifications notificationType) {
        User target  = review.getParticipant().getUser();
        NotificationTarget recipient = new UserNotificationTarget(target.getName(), target.getEmail());
        Notification notification = new Notification(
                systemNotificationSource,
                recipient,
                notificationType,
                asMap(
                        "subject", subject,
                        "name", review.getParticipant().getUser().getName(),
                        "competitionName", review.getTarget().getCompetition().getName(),
                        "panelDate", review.getTarget().getCompetition().getAssessmentPanelDate().format(INVITE_DATE_FORMAT),
                        "ifsUrl", webBaseUrl
                ));

        return notificationSender.sendNotification(notification).andOnSuccessReturnVoid();
    }
}