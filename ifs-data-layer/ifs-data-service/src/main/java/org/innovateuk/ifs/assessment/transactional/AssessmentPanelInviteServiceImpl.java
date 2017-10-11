package org.innovateuk.ifs.assessment.transactional;


import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.AssessmentPanelInvite;
import org.innovateuk.ifs.invite.domain.AssessmentPanelParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.repository.AssessmentPanelInviteRepository;
import org.innovateuk.ifs.invite.repository.AssessmentPanelParticipantRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;
import static org.innovateuk.ifs.util.CollectionFunctions.mapWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.util.StringFunctions.plainTextToHtml;
import static org.innovateuk.ifs.util.StringFunctions.stripHtml;


/*
 * Service for managing {@link AssessmentPanelInvite}s.
 */
@Service
@Transactional
public class AssessmentPanelInviteServiceImpl implements AssessmentPanelInviteService {

    private static final String WEB_CONTEXT_DASHBOARD = "/assessment/assessor/dashboard";
    private static final DateTimeFormatter inviteFormatter = ofPattern("d MMMM yyyy");
    private static final DateTimeFormatter detailsFormatter = ofPattern("d MMM yyyy");
    
    @Autowired
    private AssessmentPanelInviteRepository assessmentPanelInviteRepository;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private AssessmentPanelParticipantRepository assessmentPanelParticipantRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    private ParticipantStatusMapper participantStatusMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;


    enum Notifications {
        INVITE_ASSESSOR_TO_PANEL,
        INVITE_ASSESSOR_GROUP_TO_PANEL
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    public ServiceResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
        return getCompetition(competitionId).andOnSuccess(competition -> {
            List<AssessmentPanelInvite> invites = assessmentPanelInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED);

            List<String> recipients = simpleMap(invites, AssessmentPanelInvite::getName);
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

            List<AssessmentPanelInvite> invites = assessmentPanelInviteRepository.getByIdIn(inviteIds);
            List<String> recipients = simpleMap(invites, AssessmentPanelInvite::getName);
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
                    assessmentPanelInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED),
                    invite -> {
                        assessmentPanelParticipantRepository.save(
                                new AssessmentPanelParticipant(invite.send(loggedInUserSupplier.get(), ZonedDateTime.now()))
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
                assessmentPanelInviteRepository.getByIdIn(inviteIds),
                invite -> sendInviteNotification(
                        assessorInviteSendResource.getSubject(),
                        customTextPlain,
                        customTextHtml,
                        invite.sendOrResend(loggedInUserSupplier.get(), ZonedDateTime.now()),
                        Notifications.INVITE_ASSESSOR_GROUP_TO_PANEL
                )
        ));
    }

    @Override
        public ServiceResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, Pageable pageable) {
            final Page<CompetitionParticipant> pagedResult = competitionParticipantRepository.findParticipantsNotOnPanel(competitionId, pageable);

            return serviceSuccess(new AvailableAssessorPageResource(
                    pagedResult.getTotalElements(),
                    pagedResult.getTotalPages(),
                    simpleMap(pagedResult.getContent(), this::mapToAvailableAssessorResource),
                    pagedResult.getNumber(),
                    pagedResult.getSize()
            ));
        }

    @Override
    public ServiceResult<List<Long>> getAvailableAssessorIds(long competitionId) {
        List<CompetitionParticipant> result = competitionParticipantRepository.findParticipantsNotOnPanel(competitionId);

        return serviceSuccess(simpleMap(result, competitionParticipant -> competitionParticipant.getUser().getId()));
    }

    private AvailableAssessorResource mapToAvailableAssessorResource(CompetitionParticipant participant) {
        User assessor = participant.getUser();
        Profile profile = profileRepository.findOne(assessor.getProfileId());

        AvailableAssessorResource availableAssessor = new AvailableAssessorResource();
        availableAssessor.setId(assessor.getId());
        availableAssessor.setEmail(assessor.getEmail());
        availableAssessor.setName(assessor.getName());
        availableAssessor.setBusinessType(profile.getBusinessType());
        availableAssessor.setCompliant(profile.isCompliant(assessor));
        availableAssessor.setInnovationAreas(simpleMap(profile.getInnovationAreas(), innovationAreaMapper::mapToResource));

        return availableAssessor;
    }

    @Override
    public ServiceResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, Pageable pageable) {
        Page<AssessmentPanelInvite> pagedResult = assessmentPanelInviteRepository.getByCompetitionIdAndStatus(competitionId, CREATED, pageable);

        List<AssessorCreatedInviteResource> createdInvites = simpleMap(
                pagedResult.getContent(),
                competitionInvite -> {
                    AssessorCreatedInviteResource assessorCreatedInvite = new AssessorCreatedInviteResource();
                    assessorCreatedInvite.setName(competitionInvite.getName());
                    assessorCreatedInvite.setInnovationAreas(getInnovationAreasForInvite(competitionInvite));
                    assessorCreatedInvite.setCompliant(isUserCompliant(competitionInvite));
                    assessorCreatedInvite.setEmail(competitionInvite.getEmail());
                    assessorCreatedInvite.setInviteId(competitionInvite.getId());

                    if (competitionInvite.getUser() != null) {
                        assessorCreatedInvite.setId(competitionInvite.getUser().getId());
                    }

                    return assessorCreatedInvite;
                }
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
                getUserById(invite.getUserId()).andOnSuccess(user ->
                        getByEmailAndCompetition(user.getEmail(), invite.getCompetitionId()).andOnFailure(() ->
                                inviteUserToCompetition(user, invite.getCompetitionId())
                        )))).andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId, Pageable pageable) {
        Page<AssessmentPanelParticipant> pagedResult = assessmentPanelParticipantRepository.getPanelAssessorsByCompetitionAndStatusContains(
                    competitionId,
                    asList(PENDING, REJECTED),
                    pageable);

        List<AssessorInviteOverviewResource> inviteOverviews = simpleMap(
                pagedResult.getContent(),
                participant -> {
                    AssessorInviteOverviewResource assessorInviteOverview = new AssessorInviteOverviewResource();
                    assessorInviteOverview.setName(participant.getInvite().getName());
                    assessorInviteOverview.setStatus(participantStatusMapper.mapToResource(participant.getStatus()));
                    assessorInviteOverview.setDetails(getDetails(participant));
                    assessorInviteOverview.setInviteId(participant.getInvite().getId());

                    if (participant.getUser() != null) {
                        Profile profile = profileRepository.findOne(participant.getUser().getProfileId());

                        assessorInviteOverview.setId(participant.getUser().getId());
                        assessorInviteOverview.setBusinessType(profile.getBusinessType());
                        assessorInviteOverview.setCompliant(profile.isCompliant(participant.getUser()));
                        assessorInviteOverview.setInnovationAreas(simpleMap(profile.getInnovationAreas(), innovationAreaMapper::mapToResource));
                    }

                    return assessorInviteOverview;
                });

        return serviceSuccess(new AssessorInviteOverviewPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                inviteOverviews,
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));
    }

    private String getDetails(AssessmentPanelParticipant participant) {
        String details = null;

        if (participant.getStatus() == REJECTED) {
            details = format("Invite declined as %s", lowerCase(participant.getRejectionReason().getReason()));
        } else if (participant.getStatus() == PENDING) {
            if (participant.getInvite().getSentOn() != null) {
                details = format("Invite sent: %s", participant.getInvite().getSentOn().format(detailsFormatter));
            }
        }

        return details;
    }

    @Override
    public ServiceResult<List<Long>> getNonAcceptedAssessorInviteIds(long competitionId) {
        List<AssessmentPanelParticipant> participants = assessmentPanelParticipantRepository.getPanelAssessorsByCompetitionAndStatusContains(
                competitionId,
                asList(PENDING, REJECTED));

        return serviceSuccess(simpleMap(participants, participant -> participant.getInvite().getId()));
    }

    private ServiceResult<AssessmentPanelInvite> inviteUserToCompetition(User user, long competitionId) {
        return getCompetition(competitionId)
                .andOnSuccessReturn(
                        competition -> assessmentPanelInviteRepository.save(new AssessmentPanelInvite(user, generateInviteHash(), competition))
                );

    }

    private ServiceResult<Competition> getCompetition(long competitionId) {
        return find(competitionRepository.findOne(competitionId), notFoundError(Competition.class, competitionId));
    }


    private String getInvitePreviewContent(NotificationTarget notificationTarget, Map<String, Object> arguments) {

        return renderer.renderTemplate(systemNotificationSource, notificationTarget, "invite_assessors_to_assessors_panel_text.txt",
                arguments).getSuccessObject();
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
                                                       AssessmentPanelInvite invite,
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
                        "inviteUrl", webBaseUrl + WEB_CONTEXT_DASHBOARD,
                        "customTextPlain", customTextPlain,
                        "customTextHtml", customTextHtml
                ));

        return notificationSender.sendNotification(notification).andOnSuccessReturnVoid();
    }

    private ServiceResult<User> getUserById(long id) {
        return find(userRepository.findOne(id), notFoundError(User.class, id));
    }

    private ServiceResult<AssessmentPanelInvite> getByEmailAndCompetition(String email, long competitionId) {
        return find(assessmentPanelInviteRepository.getByEmailAndCompetitionId(email, competitionId), notFoundError(CompetitionInvite.class, email, competitionId));
    }

    private boolean isUserCompliant(AssessmentPanelInvite competitionInvite) {
        if (competitionInvite == null || competitionInvite.getUser() == null) {
            return false;
        }
        Profile profile = profileRepository.findOne(competitionInvite.getUser().getProfileId());
        return profile.isCompliant(competitionInvite.getUser());
    }

    private List<InnovationAreaResource> getInnovationAreasForInvite(AssessmentPanelInvite competitionInvite) {
        return profileRepository.findOne(competitionInvite.getUser().getProfileId()).getInnovationAreas().stream()
                .map(innovationAreaMapper::mapToResource)
                .collect(toList());
    }
}


