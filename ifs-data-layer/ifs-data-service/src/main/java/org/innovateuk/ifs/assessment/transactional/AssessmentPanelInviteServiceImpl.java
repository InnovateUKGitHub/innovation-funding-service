package org.innovateuk.ifs.assessment.transactional;


import org.innovateuk.ifs.assessment.mapper.CompetitionInviteMapper;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.AssessmentPanelInvite;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.repository.AssessmentPanelInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
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
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
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
    
    @Autowired
    private AssessmentPanelInviteRepository assessmentPanelInviteRepository;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;

    @Autowired
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    private CompetitionInviteMapper competitionInviteMapper;

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
    public ServiceResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        return getCompetition(competitionId).andOnSuccess(competition -> {

            String customTextPlain = stripHtml(assessorInviteSendResource.getContent());
            String customTextHtml = plainTextToHtml(customTextPlain);

            return ServiceResult.processAnyFailuresOrSucceed(simpleMap(
                    assessmentPanelInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED),
                    invite -> {
                        invite.send(loggedInUserSupplier.get(), ZonedDateTime.now());

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

    @Override
    public ServiceResult<CompetitionInviteResource> openInvite(String inviteHash) {
        return getByHashIfOpen(inviteHash)
                .andOnSuccessReturn(this::openInvite)
                .andOnSuccessReturn(competitionInviteMapper::mapToResource);
    }

    private AssessmentPanelInvite openInvite(AssessmentPanelInvite invite) {
        return assessmentPanelInviteRepository.save(invite.open());
    }

    @Override
    public ServiceResult<Void> acceptInvite(String inviteHash, UserResource currentUser) {
        final User user = userRepository.findOne(currentUser.getId());
        return getParticipantByInviteHash(inviteHash)
                .andOnSuccess(p -> accept(p, user))
                .andOnSuccessReturnVoid();
    }

    private ServiceResult<CompetitionParticipant> getParticipantByInviteHash(String inviteHash) {
        return find(competitionParticipantRepository.getByInviteHash(inviteHash), notFoundError(CompetitionParticipant.class, inviteHash));
    }

    @Override
    public ServiceResult<Void> rejectInvite(String inviteHash, RejectionReasonResource rejectionReason, Optional<String> rejectionComment) {
        return getRejectionReason(rejectionReason)
                .andOnSuccess(reason -> getParticipantByInviteHash(inviteHash)
                        .andOnSuccess(invite -> reject(invite, reason, rejectionComment)))
                .andOnSuccessReturnVoid();
    }


    private ServiceResult<AssessmentPanelInvite> getByHash(String inviteHash) {
        return find(assessmentPanelInviteRepository.getByHash(inviteHash), notFoundError(CompetitionInvite.class, inviteHash));
    }

    private ServiceResult<CompetitionParticipant> accept(CompetitionParticipant participant, User user) {
        if (participant.getInvite().getStatus() != OPENED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == ACCEPTED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == REJECTED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE, getInviteCompetitionName(participant)));
        } else {
            return
                    applyInnovationAreaToUserProfile(participant, user)
                            .andOnSuccessReturn(() -> participant.acceptAndAssignUser(user));
        }
    }

    private ServiceResult<CompetitionParticipant> reject(CompetitionParticipant participant, RejectionReason rejectionReason, Optional<String> rejectionComment) {
        if (participant.getInvite().getStatus() != OPENED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == ACCEPTED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == REJECTED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_REJECTED_INVITE, getInviteCompetitionName(participant)));
        } else {
            return serviceSuccess(participant.reject(rejectionReason, rejectionComment));
        }
    }

    private ServiceResult<RejectionReason> getRejectionReason(final RejectionReasonResource rejectionReason) {
        return find(rejectionReasonRepository.findOne(rejectionReason.getId()), notFoundError(RejectionReason.class, rejectionReason.getId()));
    }

    private String getInviteCompetitionName(CompetitionParticipant participant) {
        return participant.getInvite().getTarget().getName();
    }


    private ServiceResult<AssessmentPanelInvite> getByHashIfOpen(String inviteHash) {
        return getByHash(inviteHash).andOnSuccess(invite -> {

            if (!EnumSet.of(READY_TO_OPEN, IN_ASSESSMENT, CLOSED, OPEN).contains(invite.getTarget().getCompetitionStatus())) {
                return ServiceResult.serviceFailure(new Error(COMPETITION_INVITE_EXPIRED, invite.getTarget().getName()));
            }

            CompetitionParticipant participant = competitionParticipantRepository.getByInviteHash(inviteHash);

            if (participant == null) {
                return serviceSuccess(invite);
            }

            if (participant.getStatus() == ACCEPTED || participant.getStatus() == REJECTED) {
                return ServiceResult.serviceFailure(new Error(COMPETITION_INVITE_CLOSED, invite.getTarget().getName()));
            }
            return serviceSuccess(invite);
        });
    }
}


