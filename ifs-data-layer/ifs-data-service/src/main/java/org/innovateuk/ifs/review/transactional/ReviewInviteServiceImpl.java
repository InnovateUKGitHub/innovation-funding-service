package org.innovateuk.ifs.review.transactional;


import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.assessment.mapper.AssessorCreatedInviteMapper;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteOverviewMapper;
import org.innovateuk.ifs.assessment.mapper.AvailableAssessorMapper;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.review.mapper.ReviewParticipantMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.review.repository.ReviewInviteRepository;
import org.innovateuk.ifs.review.repository.ReviewParticipantRepository;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.invite.transactional.InviteService;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.mapper.ReviewInviteMapper;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.PANEL_ASSESSOR;
import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.PREVIEW_TEMPLATES_PATH;
import static org.innovateuk.ifs.util.CollectionFunctions.mapWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.util.StringFunctions.plainTextToHtml;
import static org.innovateuk.ifs.util.StringFunctions.stripHtml;

/*
 * Service for managing {@link ReviewInvite}s.
 */
@Service
@Transactional
public class ReviewInviteServiceImpl extends InviteService<ReviewInvite> implements ReviewInviteService {

    private static final String WEB_CONTEXT = "/assessment";

    @Autowired
    private ReviewInviteRepository reviewInviteRepository;

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private ReviewParticipantRepository reviewParticipantRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ReviewInviteMapper reviewInviteMapper;

    @Autowired
    private ReviewParticipantMapper reviewParticipantMapper;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AvailableAssessorMapper availableAssessorMapper;

    @Autowired
    private AssessorInviteOverviewMapper assessorInviteOverviewMapper;

    @Autowired
    private AssessorCreatedInviteMapper assessorCreatedInviteMapper;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_ASSESSOR_TO_PANEL,
        INVITE_ASSESSOR_GROUP_TO_PANEL;
    }

    @Override
    protected Class<ReviewInvite> getInviteClass() {
        return ReviewInvite.class;
    }

    @Override
    protected InviteRepository<ReviewInvite> getInviteRepository() {
        return reviewInviteRepository;
    }

    @Override
    public ServiceResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
        return getCompetition(competitionId).andOnSuccess(competition -> {
            List<ReviewInvite> invites = reviewInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED);

            List<String> recipients = simpleMap(invites, ReviewInvite::getName);
            recipients.sort(String::compareTo);

            return serviceSuccess(new AssessorInvitesToSendResource(
                    recipients,
                    competition.getId(),
                    competition.getName(),
                    getInvitePreviewContent(competition)
            ));
        });
    }

    @Override
    public ServiceResult<AssessorInvitesToSendResource> getAllInvitesToResend(long competitionId, List<Long> inviteIds) {
        return getCompetition(competitionId).andOnSuccess(competition -> {

            List<ReviewInvite> invites = reviewInviteRepository.getByIdIn(inviteIds);
            List<String> recipients = simpleMap(invites, ReviewInvite::getName);
            recipients.sort(String::compareTo);

            return serviceSuccess(new AssessorInvitesToSendResource(
                    recipients,
                    competition.getId(),
                    competition.getName(),
                    getInvitePreviewContent(competition)
            ));
        });
    }

    @Override
    public ServiceResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        return getCompetition(competitionId).andOnSuccess(competition -> {

            String customTextPlain = stripHtml(assessorInviteSendResource.getContent());
            String customTextHtml = plainTextToHtml(customTextPlain);

            return ServiceResult.processAnyFailuresOrSucceed(simpleMap(
                    reviewInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED),
                    invite -> {
                        reviewParticipantRepository.save(
                                new ReviewParticipant(invite.send(loggedInUserSupplier.get(), now()))
                        );

                        return sendInviteNotification(
                                assessorInviteSendResource.getSubject(),
                                customTextPlain,
                                customTextHtml,
                                invite,
                                Notifications.INVITE_ASSESSOR_GROUP_TO_PANEL
                        );
                    }
            ));
        });
    }

    @Override
    public ServiceResult<Void> resendInvites(List<Long> inviteIds, AssessorInviteSendResource assessorInviteSendResource) {
        String customTextPlain = stripHtml(assessorInviteSendResource.getContent());
        String customTextHtml = plainTextToHtml(customTextPlain);

        return ServiceResult.processAnyFailuresOrSucceed(simpleMap(
                reviewInviteRepository.getByIdIn(inviteIds),
                invite -> {
                    updateParticipantStatus(invite);

                    return sendInviteNotification(
                            assessorInviteSendResource.getSubject(),
                            customTextPlain,
                            customTextHtml,
                            invite.sendOrResend(loggedInUserSupplier.get(), now()),
                            Notifications.INVITE_ASSESSOR_GROUP_TO_PANEL
                    );
                }
        ));
    }

    @Override
        public ServiceResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, Pageable pageable) {
            final Page<AssessmentParticipant> pagedResult = assessmentParticipantRepository.findParticipantsNotOnAssessmentPanel(competitionId, pageable);

        return serviceSuccess(new AvailableAssessorPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                simpleMap(pagedResult.getContent(), availableAssessorMapper::mapToResource),
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));
    }

    @Override
    public ServiceResult<List<Long>> getAvailableAssessorIds(long competitionId) {
        List<AssessmentParticipant> result = assessmentParticipantRepository.findParticipantsNotOnAssessmentPanel(competitionId);

        return serviceSuccess(simpleMap(result, competitionParticipant -> competitionParticipant.getUser().getId()));
    }

    @Override
    public ServiceResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, Pageable pageable) {
        Page<ReviewInvite> pagedResult = reviewInviteRepository.getByCompetitionIdAndStatus(competitionId, CREATED, pageable);

        List<AssessorCreatedInviteResource> createdInvites = simpleMap(
                pagedResult.getContent(),
                assessorCreatedInviteMapper::mapToResource
        );

        return serviceSuccess(new AssessorCreatedInvitePageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                createdInvites,
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));
    }

    @Override
    public ServiceResult<Void> inviteUsers(List<ExistingUserStagedInviteResource> stagedInvites) {
        return serviceSuccess(mapWithIndex(stagedInvites, (i, invite) ->
                getUser(invite.getUserId()).andOnSuccess(user ->
                        getByEmailAndCompetition(user.getEmail(), invite.getCompetitionId()).andOnFailure(() ->
                                inviteUserToCompetition(user, invite.getCompetitionId())
                        )))).andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                                   Pageable pageable,
                                                                                   List<ParticipantStatus> statuses) {
        Page<ReviewParticipant> pagedResult = reviewParticipantRepository.getPanelAssessorsByCompetitionAndStatusContains(
                competitionId,
                statuses,
                pageable);

        List<AssessorInviteOverviewResource> inviteOverviews = simpleMap(
                pagedResult.getContent(),
                assessorInviteOverviewMapper::mapToResource
        );

        return serviceSuccess(new AssessorInviteOverviewPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                inviteOverviews,
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));
    }

    @Override
    public ServiceResult<List<Long>> getNonAcceptedAssessorInviteIds(long competitionId) {
        List<ReviewParticipant> participants = reviewParticipantRepository.getPanelAssessorsByCompetitionAndStatusContains(
                competitionId,
                asList(PENDING, REJECTED));

        return serviceSuccess(simpleMap(participants, participant -> participant.getInvite().getId()));
    }

    private ServiceResult<ReviewInvite> inviteUserToCompetition(User user, long competitionId) {
        return getCompetition(competitionId)
                .andOnSuccessReturn(
                        competition -> reviewInviteRepository.save(new ReviewInvite(user, generateInviteHash(), competition))
                );

    }

    @Override
    public ServiceResult<List<ReviewParticipantResource>> getAllInvitesByUser(long userId) {
        List<ReviewParticipantResource> reviewParticipantResources =
                reviewParticipantRepository
                        .findByUserIdAndRole(userId, PANEL_ASSESSOR)
                        .stream()
                        .filter(participant -> now().isBefore(participant.getInvite().getTarget().getAssessmentPanelDate()))
                        .map(reviewParticipantMapper::mapToResource)
                        .collect(toList());

        reviewParticipantResources.forEach(this::determineStatusOfPanelApplications);

        return serviceSuccess(reviewParticipantResources);
    }

    private ServiceResult<Competition> getCompetition(long competitionId) {
        return find(competitionRepository.findOne(competitionId), notFoundError(Competition.class, competitionId));
    }

    private String getInvitePreviewContent(NotificationTarget notificationTarget, Map<String, Object> arguments) {

        return renderer.renderTemplate(systemNotificationSource, notificationTarget, PREVIEW_TEMPLATES_PATH + "invite_assessors_to_assessors_panel_text.txt",
                arguments).getSuccess();
    }

    private String getInvitePreviewContent(Competition competition) {
        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        return getInvitePreviewContent(notificationTarget, asMap(
                "competitionName", competition.getName()
        ));
    }

    private ServiceResult<Void> sendInviteNotification(String subject,
                                                       String customTextPlain,
                                                       String customTextHtml,
                                                       ReviewInvite invite,
                                                       Notifications notificationType) {
        NotificationTarget recipient = new UserNotificationTarget(invite.getName(), invite.getEmail());
        Notification notification = new Notification(
                systemNotificationSource,
                recipient,
                notificationType,
                asMap(
                        "subject", subject,
                        "name", invite.getName(),
                        "competitionName", invite.getTarget().getName(),
                        "inviteUrl", format("%s/invite/panel/%s", webBaseUrl + WEB_CONTEXT, invite.getHash()),
                        "customTextPlain", customTextPlain,
                        "customTextHtml", customTextHtml
                ));

        return notificationSender.sendNotification(notification).andOnSuccessReturnVoid();
    }

    private ServiceResult<ReviewInvite> getByEmailAndCompetition(String email, long competitionId) {
        return find(reviewInviteRepository.getByEmailAndCompetitionId(email, competitionId), notFoundError(AssessmentInvite.class, email, competitionId));
    }

    @Override
    public ServiceResult<ReviewInviteResource> openInvite(String inviteHash) {
        return getByHashIfOpen(inviteHash)
                .andOnSuccessReturn(this::openInvite)
                .andOnSuccessReturn(reviewInviteMapper::mapToResource);
    }

    private ReviewInvite openInvite(ReviewInvite invite) {
        return reviewInviteRepository.save(invite.open());
    }

    @Override
    public ServiceResult<Void> acceptInvite(String inviteHash) {
        return getParticipantByInviteHash(inviteHash)
                .andOnSuccess(ReviewInviteServiceImpl::accept)
                .andOnSuccess(this::assignAllPanelApplicationsToParticipant)
                .andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> assignAllPanelApplicationsToParticipant(ReviewParticipant participant) {
        Competition competition = participant.getProcess();
        List<Application> applicationsInPanel = applicationRepository.findByCompetitionAndInAssessmentReviewPanelTrueAndApplicationProcessActivityState(competition, ApplicationState.SUBMITTED);
        applicationsInPanel.forEach(application -> {
            Review review = new Review(application, participant);
            review.setProcessState(ReviewState.PENDING);
            reviewRepository.save(review);
        });
        return serviceSuccess();
    }

    private ServiceResult<ReviewParticipant> getParticipantByInviteHash(String inviteHash) {
        return find(reviewParticipantRepository.getByInviteHash(inviteHash), notFoundError(ReviewParticipant.class, inviteHash));
    }

    @Override
    public ServiceResult<Void> rejectInvite(String inviteHash) {
        return getParticipantByInviteHash(inviteHash)
                .andOnSuccess(this::reject)
                .andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<Boolean> checkUserExistsForInvite(String inviteHash) {
        return super.checkUserExistsForInvite(inviteHash);
    }

    private static ServiceResult<ReviewParticipant> accept(ReviewParticipant participant) {
        User user = participant.getUser();
        if (participant.getInvite().getStatus() != OPENED) {
            return ServiceResult.serviceFailure(new Error(ASSESSMENT_PANEL_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == ACCEPTED) {
            return ServiceResult.serviceFailure(new Error(ASSESSMENT_PANEL_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == REJECTED) {
            return ServiceResult.serviceFailure(new Error(ASSESSMENT_PANEL_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE, getInviteCompetitionName(participant)));
        } else {
            return serviceSuccess( participant.acceptAndAssignUser(user));
        }
    }

    private ServiceResult<CompetitionParticipant> reject(ReviewParticipant participant) {
        if (participant.getInvite().getStatus() != OPENED) {
            return ServiceResult.serviceFailure(new Error(ASSESSMENT_PANEL_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == ACCEPTED) {
            return ServiceResult.serviceFailure(new Error(ASSESSMENT_PANEL_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == REJECTED) {
            return ServiceResult.serviceFailure(new Error(ASSESSMENT_PANEL_PARTICIPANT_CANNOT_REJECT_ALREADY_REJECTED_INVITE, getInviteCompetitionName(participant)));
        } else {
            return serviceSuccess(participant.reject());
        }
    }

    private static String getInviteCompetitionName(ReviewParticipant participant) {
        return participant.getInvite().getTarget().getName();
    }

    private ServiceResult<ReviewInvite> getByHashIfOpen(String inviteHash) {
        return getByHash(inviteHash).andOnSuccess(invite -> {

            if (invite.getTarget().getAssessmentPanelDate() == null || now().isAfter(invite.getTarget().getAssessmentPanelDate())) {
                return ServiceResult.serviceFailure(new Error(ASSESSMENT_PANEL_INVITE_EXPIRED, invite.getTarget().getName()));
            }

            ReviewParticipant participant = reviewParticipantRepository.getByInviteHash(inviteHash);

            if (participant == null) {
                return serviceSuccess(invite);
            }

            if (participant.getStatus() == ACCEPTED || participant.getStatus() == REJECTED) {
                return ServiceResult.serviceFailure(new Error(ASSESSMENT_PANEL_INVITE_CLOSED, invite.getTarget().getName()));
            }
            return serviceSuccess(invite);
        });
    }

    @Override
    public ServiceResult<Void> deleteInvite(String email, long competitionId) {
        return getByEmailAndCompetition(email, competitionId).andOnSuccess(this::deleteInvite);
    }

    @Override
    public ServiceResult<Void> deleteAllInvites(long competitionId) {
        return find(competitionRepository.findOne(competitionId), notFoundError(Competition.class, competitionId))
                .andOnSuccessReturnVoid(competition ->
                        reviewInviteRepository.deleteByCompetitionIdAndStatus(competition.getId(), CREATED));
    }

    private ServiceResult<Void> deleteInvite(ReviewInvite invite) {
        if (invite.getStatus() != CREATED) {
            return ServiceResult.serviceFailure(new Error(ASSESSMENT_PANEL_INVITE_CANNOT_DELETE_ONCE_SENT, invite.getEmail()));
        }

        reviewInviteRepository.delete(invite);
        return serviceSuccess();
    }

    private void determineStatusOfPanelApplications(ReviewParticipantResource reviewParticipantResource) {

        List<Review> reviews = reviewRepository.
                findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(
                        reviewParticipantResource.getUserId(),
                        reviewParticipantResource.getCompetitionId());

        reviewParticipantResource.setAwaitingApplications(getApplicationsPendingForPanelCount(reviews));
    }

    private Long getApplicationsPendingForPanelCount(List<Review> reviews) {
        return reviews.stream().filter(review -> review.getProcessState().equals(ReviewState.PENDING)).count();
    }

    private void updateParticipantStatus(ReviewInvite invite){
        ReviewParticipant reviewParticipant = reviewParticipantRepository.getByInviteHash(invite.getHash());
        if(reviewParticipant.getStatus() != PENDING){
            reviewParticipant.setStatus(PENDING);
            reviewParticipantRepository.save(reviewParticipant);
        }
    }
}