package org.innovateuk.ifs.assessment.transactional;


import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterview;
import org.innovateuk.ifs.assessment.interview.mapper.AssessmentInterviewPanelInviteMapper;
import org.innovateuk.ifs.assessment.interview.repository.AssessmentInterviewRepository;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState;
import org.innovateuk.ifs.assessment.mapper.AssessorCreatedInviteMapper;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteOverviewMapper;
import org.innovateuk.ifs.assessment.mapper.AvailableAssessorMapper;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.competition.AssessmentInterviewPanelInvite;
import org.innovateuk.ifs.invite.domain.competition.AssessmentInterviewPanelParticipant;
import org.innovateuk.ifs.invite.domain.competition.CompetitionAssessmentParticipant;
import org.innovateuk.ifs.invite.mapper.AssessmentInterviewPanelParticipantMapper;
import org.innovateuk.ifs.invite.repository.AssessmentInterviewPanelInviteRepository;
import org.innovateuk.ifs.invite.repository.AssessmentInterviewPanelParticipantRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.invite.transactional.InviteService;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
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
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
import static org.innovateuk.ifs.invite.domain.competition.CompetitionParticipantRole.INTERVIEW_ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.mapWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.util.StringFunctions.plainTextToHtml;
import static org.innovateuk.ifs.util.StringFunctions.stripHtml;

/**
 * Service for managing {@link AssessmentInterviewPanelInvite}s.
 */
@Service
@Transactional
public class AssessmentInterviewPanelInviteServiceImpl extends InviteService<AssessmentInterviewPanelInvite> implements AssessmentInterviewPanelInviteService {

    private static final String WEB_CONTEXT = "/interview";

    @Autowired
    private AssessmentInterviewPanelInviteRepository assessmentInterviewPanelInviteRepository;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private AssessmentInterviewPanelParticipantRepository assessmentInterviewPanelParticipantRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private AssessmentInterviewPanelInviteMapper assessmentInterviewPanelInviteMapper;

    @Autowired
    private AssessmentInterviewPanelParticipantMapper assessmentInterviewPanelParticipantMapper;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private AssessmentInterviewRepository assessmentInterviewRepository;

    @Autowired
    private AvailableAssessorMapper availableAssessorMapper;

    @Autowired
    private AssessorInviteOverviewMapper assessorInviteOverviewMapper;

    @Autowired
    private AssessorCreatedInviteMapper assessorCreatedInviteMapper;

    enum Notifications {
        INVITE_ASSESSOR_GROUP_TO_INTERVIEW
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    protected Class<AssessmentInterviewPanelInvite> getInviteClass() {
        return AssessmentInterviewPanelInvite.class;
    }

    @Override
    protected InviteRepository<AssessmentInterviewPanelInvite> getRepository() {
        return assessmentInterviewPanelInviteRepository;
    }

    @Override
    public ServiceResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
        return getCompetition(competitionId).andOnSuccess(competition -> {
            List<AssessmentInterviewPanelInvite> invites = assessmentInterviewPanelInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED);

            List<String> recipients = simpleMap(invites, AssessmentInterviewPanelInvite::getName);
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

            List<AssessmentInterviewPanelInvite> invites = assessmentInterviewPanelInviteRepository.getByIdIn(inviteIds);
            List<String> recipients = simpleMap(invites, AssessmentInterviewPanelInvite::getName);
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
                    assessmentInterviewPanelInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED),
                    invite -> {
                        assessmentInterviewPanelParticipantRepository.save(
                                new AssessmentInterviewPanelParticipant(invite.send(loggedInUserSupplier.get(), now()))
                        );

                        return sendInviteNotification(
                                assessorInviteSendResource.getSubject(),
                                customTextPlain,
                                customTextHtml,
                                invite,
                                Notifications.INVITE_ASSESSOR_GROUP_TO_INTERVIEW
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
                assessmentInterviewPanelInviteRepository.getByIdIn(inviteIds),
                invite -> sendInviteNotification(
                        assessorInviteSendResource.getSubject(),
                        customTextPlain,
                        customTextHtml,
                        invite.sendOrResend(loggedInUserSupplier.get(), now()),
                        Notifications.INVITE_ASSESSOR_GROUP_TO_INTERVIEW
                )
        ));
    }

    @Override
        public ServiceResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, Pageable pageable) {
            final Page<CompetitionAssessmentParticipant> pagedResult = competitionParticipantRepository.findParticipantsNotOnInterviewPanel(competitionId, pageable);

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
        List<CompetitionAssessmentParticipant> result = competitionParticipantRepository.findParticipantsNotOnInterviewPanel(competitionId);

        return serviceSuccess(simpleMap(result, competitionParticipant -> competitionParticipant.getUser().getId()));
    }

    @Override
    public ServiceResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, Pageable pageable) {
        Page<AssessmentInterviewPanelInvite> pagedResult = assessmentInterviewPanelInviteRepository.getByCompetitionIdAndStatus(competitionId, CREATED, pageable);

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
        Page<AssessmentInterviewPanelParticipant> pagedResult = assessmentInterviewPanelParticipantRepository.getInterviewPanelAssessorsByCompetitionAndStatusContains(
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
        List<AssessmentInterviewPanelParticipant> participants = assessmentInterviewPanelParticipantRepository.getInterviewPanelAssessorsByCompetitionAndStatusContains(
                competitionId,
                asList(PENDING, REJECTED));

        return serviceSuccess(simpleMap(participants, participant -> participant.getInvite().getId()));
    }

    private ServiceResult<AssessmentInterviewPanelInvite> inviteUserToCompetition(User user, long competitionId) {
        return getCompetition(competitionId)
                .andOnSuccessReturn(
                        competition -> assessmentInterviewPanelInviteRepository.save(new AssessmentInterviewPanelInvite(user, generateInviteHash(), competition))
                );

    }

    @Override
    public ServiceResult<List<AssessmentInterviewPanelParticipantResource>> getAllInvitesByUser(long userId) {
        List<AssessmentInterviewPanelParticipantResource> assessmentInterviewPanelParticipantResources =
                assessmentInterviewPanelParticipantRepository
                .findByUserIdAndRole(userId, INTERVIEW_ASSESSOR)
                .stream()
                .filter(participant -> now().isBefore(participant.getInvite().getTarget().getPanelDate()))
                .map(assessmentInterviewPanelParticipantMapper::mapToResource)
                .collect(toList());

        assessmentInterviewPanelParticipantResources.forEach(this::determineStatusOfPanelApplications);

        return serviceSuccess(assessmentInterviewPanelParticipantResources);
    }

    private ServiceResult<Competition> getCompetition(long competitionId) {
        return find(competitionRepository.findOne(competitionId), notFoundError(Competition.class, competitionId));
    }

    private String getInvitePreviewContent(NotificationTarget notificationTarget, Map<String, Object> arguments) {

        return renderer.renderTemplate(systemNotificationSource, notificationTarget, "invite_assessors_to_interview_panel_text.txt",
                arguments).getSuccess();
    }

    private String getInvitePreviewContent(Competition competition) {
        NotificationTarget notificationTarget = new ExternalUserNotificationTarget("", "");

        return getInvitePreviewContent(notificationTarget, asMap(
                "competitionName", competition.getName()
        ));
    }

    private ServiceResult<Void> sendInviteNotification(String subject,
                                                       String customTextPlain,
                                                       String customTextHtml,
                                                       AssessmentInterviewPanelInvite invite,
                                                       Notifications notificationType) {
        NotificationTarget recipient = new ExternalUserNotificationTarget(invite.getName(), invite.getEmail());
        Notification notification = new Notification(
                systemNotificationSource,
                recipient,
                notificationType,
                asMap(
                        "subject", subject,
                        "name", invite.getName(),
                        "competitionName", invite.getTarget().getName(),
                        "inviteUrl", format("%s/invite/interview/%s", webBaseUrl + WEB_CONTEXT, invite.getHash()),
                        "customTextPlain", customTextPlain,
                        "customTextHtml", customTextHtml
                ));

        return notificationSender.sendNotification(notification).andOnSuccessReturnVoid();
    }

    private ServiceResult<AssessmentInterviewPanelInvite> getByEmailAndCompetition(String email, long competitionId) {
        return find(assessmentInterviewPanelInviteRepository.getByEmailAndCompetitionId(email, competitionId), notFoundError(AssessmentInterviewPanelInvite.class, email, competitionId));
    }

    @Override
    public ServiceResult<AssessmentInterviewPanelInviteResource> openInvite(String inviteHash) {
        return getByHashIfOpen(inviteHash)
                .andOnSuccessReturn(this::openInvite)
                .andOnSuccessReturn(assessmentInterviewPanelInviteMapper::mapToResource);
    }

    private AssessmentInterviewPanelInvite openInvite(AssessmentInterviewPanelInvite invite) {
        return assessmentInterviewPanelInviteRepository.save(invite.open());
    }

    @Override
    public ServiceResult<Boolean> checkExistingUser(String inviteHash) {
        return super.checkExistingUser(inviteHash);
    }

    private ServiceResult<AssessmentInterviewPanelInvite> getByHashIfOpen(String inviteHash) {
        return getByHash(inviteHash).andOnSuccess(invite -> {

            if (invite.getTarget().getPanelDate() == null || now().isAfter(invite.getTarget().getPanelDate())) {
                return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_INVITE_EXPIRED, invite.getTarget().getName()));
            }

            AssessmentInterviewPanelParticipant participant = assessmentInterviewPanelParticipantRepository.getByInviteHash(inviteHash);

            if (participant == null) {
                return serviceSuccess(invite);
            }

            if (participant.getStatus() == ACCEPTED || participant.getStatus() == REJECTED) {
                return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_INVITE_CLOSED, invite.getTarget().getName()));
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
                        assessmentInterviewPanelInviteRepository.deleteByCompetitionIdAndStatus(competition.getId(), CREATED));
    }

    private ServiceResult<Void> deleteInvite(AssessmentInterviewPanelInvite invite) {
        if (invite.getStatus() != CREATED) {
            return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_INVITE_CANNOT_DELETE_ONCE_SENT, invite.getEmail()));
        }

        assessmentInterviewPanelInviteRepository.delete(invite);
        return serviceSuccess();
    }

    private void determineStatusOfPanelApplications(AssessmentInterviewPanelParticipantResource assessmentInterviewPanelParticipantResource) {

        List<AssessmentInterview> reviews = assessmentInterviewRepository.
                findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(
                        assessmentInterviewPanelParticipantResource.getUserId(),
                        assessmentInterviewPanelParticipantResource.getCompetitionId());

        assessmentInterviewPanelParticipantResource.setAwaitingApplications(getApplicationsPendingForPanelCount(reviews));
    }

    private Long getApplicationsPendingForPanelCount(List<AssessmentInterview> reviews) {
        return reviews.stream().filter(review -> review.getActivityState().equals(AssessmentInterviewState.PENDING)).count();
    }
}