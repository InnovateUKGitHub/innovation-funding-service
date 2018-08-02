package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.mapper.AssessorCreatedInviteMapper;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteOverviewMapper;
import org.innovateuk.ifs.assessment.mapper.AvailableAssessorMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
import org.innovateuk.ifs.interview.mapper.InterviewInviteMapper;
import org.innovateuk.ifs.interview.mapper.InterviewParticipantMapper;
import org.innovateuk.ifs.interview.repository.InterviewInviteRepository;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.invite.transactional.InviteService;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
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
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INTERVIEW_ASSESSOR;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.PREVIEW_TEMPLATES_PATH;
import static org.innovateuk.ifs.util.CollectionFunctions.mapWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/*
 * Service for managing {@link InterviewInvite}s.
 */
@Service
@Transactional
public class InterviewInviteServiceImpl extends InviteService<InterviewInvite> implements InterviewInviteService {

    private static final String WEB_CONTEXT = "/assessment";

    @Autowired
    private InterviewInviteRepository interviewInviteRepository;

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private InterviewParticipantRepository interviewParticipantRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private InterviewInviteMapper interviewInviteMapper;

    @Autowired
    private InterviewParticipantMapper interviewParticipantMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private InterviewRepository interviewRepository;

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
    protected Class<InterviewInvite> getInviteClass() {
        return InterviewInvite.class;
    }

    @Override
    protected InviteRepository<InterviewInvite> getInviteRepository() {
        return interviewInviteRepository;
    }

    @Override
    public ServiceResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
        return getCompetition(competitionId).andOnSuccess(competition -> {
            List<InterviewInvite> invites = interviewInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED);

            List<String> recipients = simpleMap(invites, InterviewInvite::getName);
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

            List<InterviewInvite> invites = interviewInviteRepository.getByIdIn(inviteIds);
            List<String> recipients = simpleMap(invites, InterviewInvite::getName);
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
        return getCompetition(competitionId).andOnSuccess(competition ->
                ServiceResult.processAnyFailuresOrSucceed(simpleMap(
                        interviewInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED),
                        invite -> {
                            interviewParticipantRepository.save(
                                    new InterviewParticipant(invite.send(loggedInUserSupplier.get(), now()))
                            );

                            return sendInviteNotification(
                                    assessorInviteSendResource.getSubject(),
                                    assessorInviteSendResource.getContent(),
                                    invite,
                                    Notifications.INVITE_ASSESSOR_GROUP_TO_INTERVIEW
                            );
                        }
                ))
        );
    }

    @Override
    public ServiceResult<Void> resendInvites(List<Long> inviteIds, AssessorInviteSendResource assessorInviteSendResource) {
        return ServiceResult.processAnyFailuresOrSucceed(simpleMap(
                interviewInviteRepository.getByIdIn(inviteIds),
                invite -> {
                    updateParticipantStatus(invite);

                    return  sendInviteNotification(
                            assessorInviteSendResource.getSubject(),
                            assessorInviteSendResource.getContent(),
                            invite.sendOrResend(loggedInUserSupplier.get(), now()),
                            Notifications.INVITE_ASSESSOR_GROUP_TO_INTERVIEW
                    );
                }
        ));
    }

    @Override
        public ServiceResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, Pageable pageable) {
            final Page<AssessmentParticipant> pagedResult = assessmentParticipantRepository.findParticipantsNotOnInterviewPanel(competitionId, pageable);

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
        List<AssessmentParticipant> result = assessmentParticipantRepository.findParticipantsNotOnInterviewPanel(competitionId);

        return serviceSuccess(simpleMap(result, competitionParticipant -> competitionParticipant.getUser().getId()));
    }

    @Override
    public ServiceResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, Pageable pageable) {
        Page<InterviewInvite> pagedResult = interviewInviteRepository.getByCompetitionIdAndStatus(competitionId, CREATED, pageable);

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
        Page<InterviewParticipant> pagedResult = interviewParticipantRepository.getInterviewPanelAssessorsByCompetitionAndStatusContains(
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
        List<InterviewParticipant> participants = interviewParticipantRepository.getInterviewPanelAssessorsByCompetitionAndStatusContains(
                competitionId,
                asList(PENDING, REJECTED));

        return serviceSuccess(simpleMap(participants, participant -> participant.getInvite().getId()));
    }

    private ServiceResult<InterviewInvite> inviteUserToCompetition(User user, long competitionId) {
        return getCompetition(competitionId)
                .andOnSuccessReturn(
                        competition -> interviewInviteRepository.save(new InterviewInvite(user, generateInviteHash(), competition))
                );

    }

    @Override
    public ServiceResult<List<InterviewParticipantResource>> getAllInvitesByUser(long userId) {
        List<InterviewParticipantResource> interviewParticipantResources =
                interviewParticipantRepository
                        .findByUserIdAndRole(userId, INTERVIEW_ASSESSOR)
                        .stream()
                        .filter(participant -> now().isBefore(participant.getInvite().getTarget().getPanelDate()))
                        .map(interviewParticipantMapper::mapToResource)
                        .collect(toList());

        interviewParticipantResources.forEach(this::determineStatusOfPanelApplications);

        return serviceSuccess(interviewParticipantResources);
    }

    private ServiceResult<Competition> getCompetition(long competitionId) {
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId));
    }

    private String getInvitePreviewContent(NotificationTarget notificationTarget, Map<String, Object> arguments) {

        return renderer.renderTemplate(systemNotificationSource, notificationTarget, PREVIEW_TEMPLATES_PATH + "invite_assessors_to_interview_panel_text.txt",
                arguments).getSuccess();
    }

    private String getInvitePreviewContent(Competition competition) {
        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        return getInvitePreviewContent(notificationTarget, asMap(
                "competitionName", competition.getName()
        ));
    }

    private ServiceResult<Void> sendInviteNotification(String subject,
                                                       String customTextHtml,
                                                       InterviewInvite invite,
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
                        "inviteUrl", format("%s/invite/interview/%s", webBaseUrl + WEB_CONTEXT, invite.getHash()),
                        "message", customTextHtml
                ));

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private ServiceResult<InterviewInvite> getByEmailAndCompetition(String email, long competitionId) {
        return find(interviewInviteRepository.getByEmailAndCompetitionId(email, competitionId), notFoundError(InterviewInvite.class, email, competitionId));
    }

    @Override
    public ServiceResult<InterviewInviteResource> openInvite(String inviteHash) {
        return getByHashIfOpen(inviteHash)
                .andOnSuccessReturn(this::openInvite)
                .andOnSuccessReturn(interviewInviteMapper::mapToResource);
    }

    private InterviewInvite openInvite(InterviewInvite invite) {
        return interviewInviteRepository.save(invite.open());
    }

    @Override
    public ServiceResult<Void> acceptInvite(String inviteHash) {
        return getParticipantByInviteHash(inviteHash)
                .andOnSuccess(InterviewInviteServiceImpl::accept)
                .andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<Void> rejectInvite(String inviteHash) {
        return getParticipantByInviteHash(inviteHash)
                .andOnSuccess(this::reject)
                .andOnSuccessReturnVoid();
    }

    private ServiceResult<InterviewParticipant> getParticipantByInviteHash(String inviteHash) {
        return find(interviewParticipantRepository.getByInviteHash(inviteHash), notFoundError(InterviewParticipant.class, inviteHash));
    }

    @Override
    public ServiceResult<Boolean> checkUserExistsForInvite(String inviteHash) {
        return super.checkUserExistsForInvite(inviteHash);
    }

    private static ServiceResult<InterviewParticipant> accept(InterviewParticipant participant) {
        User user = participant.getUser();
        if (participant.getInvite().getStatus() != OPENED) {
            return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == ACCEPTED) {
            return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == REJECTED) {
            return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE, getInviteCompetitionName(participant)));
        } else {
            return serviceSuccess( participant.acceptAndAssignUser(user));
        }
    }

    private ServiceResult<CompetitionParticipant> reject(InterviewParticipant participant) {
        if (participant.getInvite().getStatus() != OPENED) {
            return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == ACCEPTED) {
            return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == REJECTED) {
            return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_PARTICIPANT_CANNOT_REJECT_ALREADY_REJECTED_INVITE, getInviteCompetitionName(participant)));
        } else {
            return serviceSuccess(participant.reject());
        }
    }

    private static String getInviteCompetitionName(InterviewParticipant participant) {
        return participant.getInvite().getTarget().getName();
    }

    private ServiceResult<InterviewInvite> getByHashIfOpen(String inviteHash) {
        return getByHash(inviteHash).andOnSuccess(invite -> {

            if (invite.getTarget().getPanelDate() == null || now().isAfter(invite.getTarget().getPanelDate())) {
                return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_INVITE_EXPIRED, invite.getTarget().getName()));
            }

            InterviewParticipant participant = interviewParticipantRepository.getByInviteHash(inviteHash);

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
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                .andOnSuccessReturnVoid(competition ->
                        interviewInviteRepository.deleteByCompetitionIdAndStatus(competition.getId(), CREATED));
    }

    private ServiceResult<Void> deleteInvite(InterviewInvite invite) {
        if (invite.getStatus() != CREATED) {
            return ServiceResult.serviceFailure(new Error(INTERVIEW_PANEL_INVITE_CANNOT_DELETE_ONCE_SENT, invite.getEmail()));
        }

        interviewInviteRepository.delete(invite);
        return serviceSuccess();
    }

    private void determineStatusOfPanelApplications(InterviewParticipantResource interviewParticipantResource) {

        List<Interview> reviews = interviewRepository.
                findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(
                        interviewParticipantResource.getUserId(),
                        interviewParticipantResource.getCompetitionId());

        interviewParticipantResource.setAwaitingApplications(getApplicationsPendingForPanelCount(reviews));
    }

    private long getApplicationsPendingForPanelCount(List<Interview> interviews) {
        return (long) interviews.size();
    }

    private void updateParticipantStatus(InterviewInvite invite){
        InterviewParticipant interviewParticipant = interviewParticipantRepository.getByInviteHash(invite.getHash());
        if(interviewParticipant.getStatus() != PENDING){
            interviewParticipant.setStatus(PENDING);
            interviewParticipantRepository.save(interviewParticipant);
        }
    }
}