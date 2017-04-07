package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.mapper.AssessorInviteToSendMapper;
import org.innovateuk.ifs.assessment.mapper.CompetitionInviteMapper;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProfileRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.invite.constant.InviteStatus.*;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
import static org.innovateuk.ifs.util.CollectionFunctions.mapWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.util.StringFunctions.plainTextToHtml;
import static org.innovateuk.ifs.util.StringFunctions.stripHtml;

/**
 * Service for managing {@link org.innovateuk.ifs.invite.domain.CompetitionInvite}s.
 */
@Service
@Transactional
public class CompetitionInviteServiceImpl implements CompetitionInviteService {

    private static final String WEB_CONTEXT = "/assessment";

    @Autowired
    private CompetitionInviteRepository competitionInviteRepository;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private CompetitionInviteMapper competitionInviteMapper;

    @Autowired
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    private AssessorInviteToSendMapper toSendMapper;

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

    @Autowired
    private RoleRepository roleRepository;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_ASSESSOR
    }

    @Override
    public ServiceResult<AssessorInviteToSendResource> getCreatedInvite(long inviteId) {
        return getById(inviteId).andOnSuccess(invite -> {
            if (invite.getStatus() != CREATED) {
                return ServiceResult.serviceFailure(new Error(COMPETITION_INVITE_ALREADY_SENT, invite.getTarget().getName()));
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            NotificationTarget notificationTarget = new ExternalUserNotificationTarget(invite.getName(), invite.getEmail());

            AssessorInviteToSendResource resource = toSendMapper.mapToResource(invite);
            resource.setContent(getInviteContent(notificationTarget, asMap("name", invite.getName(),
                    "competitionName", invite.getTarget().getName(),
                    "acceptsDate", invite.getTarget().getAssessorAcceptsDate().format(formatter),
                    "deadlineDate", invite.getTarget().getAssessorDeadlineDate().format(formatter),
                    "inviteUrl", format("%s/invite/competition/%s", webBaseUrl + WEB_CONTEXT, invite.getHash()))));

            return serviceSuccess(resource);
        });
    }

    @Override
    public ServiceResult<CompetitionInviteResource> getInvite(String inviteHash) {
        return getByHashIfOpen(inviteHash)
                .andOnSuccessReturn(competitionInviteMapper::mapToResource);
    }

    @Override
    public ServiceResult<CompetitionInviteResource> openInvite(String inviteHash) {
        return getByHashIfOpen(inviteHash)
                .andOnSuccessReturn(this::openInvite)
                .andOnSuccessReturn(competitionInviteMapper::mapToResource);
    }

    @Override
    public ServiceResult<Void> acceptInvite(String inviteHash, UserResource currentUser) {
        final User user = userRepository.findOne(currentUser.getId());
        return getParticipantByInviteHash(inviteHash)
                .andOnSuccess(p -> accept(p, user))
                .andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<Void> rejectInvite(String inviteHash, RejectionReasonResource rejectionReason, Optional<String> rejectionComment) {
        return getRejectionReason(rejectionReason)
                .andOnSuccess(reason -> getParticipantByInviteHash(inviteHash)
                        .andOnSuccess(invite -> reject(invite, reason, rejectionComment)))
                .andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<Boolean> checkExistingUser(@P("inviteHash") String inviteHash) {
        return getByHash(inviteHash).andOnSuccessReturn(invite -> {
            if (invite.getUser() != null) {
                return TRUE;
            }

            return userRepository.findByEmail(invite.getEmail()).isPresent();
        });
    }

    @Override
    public ServiceResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, Pageable pageable, Optional<Long> innovationArea) {
        Page<User> pagedResult;

        if (innovationArea.isPresent()) {
            pagedResult = userRepository.findAssessorsByCompetitionAndInnovationArea(
                    competitionId,
                    innovationArea.orElse(null),
                    pageable
            );
        } else {
            pagedResult = userRepository.findAssessorsByCompetition(competitionId, pageable);
        }

        List<AvailableAssessorResource> availableAssessors = simpleMap(pagedResult.getContent(), assessor -> {
            Profile profile = profileRepository.findOne(assessor.getProfileId());

            AvailableAssessorResource availableAssessor = new AvailableAssessorResource();
            availableAssessor.setId(assessor.getId());
            availableAssessor.setEmail(assessor.getEmail());
            availableAssessor.setName(assessor.getName());
            availableAssessor.setBusinessType(profile.getBusinessType());
            availableAssessor.setCompliant(profile.isCompliant(assessor));
            availableAssessor.setInnovationAreas(simpleMap(profile.getInnovationAreas(), innovationAreaMapper::mapToResource));

            return availableAssessor;
        });

        return serviceSuccess(new AvailableAssessorPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                availableAssessors,
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));
    }


    @Override
    public ServiceResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, Pageable pageable) {
        Page<CompetitionInvite> pagedResult = competitionInviteRepository.getByCompetitionIdAndStatus(competitionId, CREATED, pageable);

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
    public ServiceResult<CompetitionInviteStatisticsResource> getInviteStatistics(long competitionId) {
        CompetitionInviteStatisticsResource statisticsResource = new CompetitionInviteStatisticsResource();
        statisticsResource.setInvited(competitionInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)));
        statisticsResource.setInviteList(competitionInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(CREATED)));
        statisticsResource.setAccepted(competitionParticipantRepository.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ACCEPTED));
        statisticsResource.setDeclined(competitionParticipantRepository.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, REJECTED));
        return serviceSuccess(statisticsResource);
    }

    @Override
    public ServiceResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                                   Pageable pageable,
                                                                                   Optional<Long> innovationArea,
                                                                                   Optional<ParticipantStatus> status,
                                                                                   Optional<Boolean> compliant) {
        Page<CompetitionParticipant> pagedResult;

        if (innovationArea.isPresent() || compliant.isPresent()) {
            // We want to avoid performing the potentially expensive join on Profile if possible
            pagedResult = competitionParticipantRepository.getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant(
                    competitionId,
                    innovationArea.orElse(null),
                    status.orElse(null),
                    compliant.orElse(null),
                    pageable
            );
        } else {
            pagedResult = competitionParticipantRepository.getAssessorsByCompetitionAndStatus(
                    competitionId,
                    status.orElse(null),
                    pageable
            );
        }

        List<AssessorInviteOverviewResource> inviteOverviews = simpleMap(
                pagedResult.getContent(),
                participant -> {
                    AssessorInviteOverviewResource assessorInviteOverview = new AssessorInviteOverviewResource();
                    assessorInviteOverview.setName(participant.getInvite().getName());
                    assessorInviteOverview.setStatus(participantStatusMapper.mapToResource(participant.getStatus()));
                    assessorInviteOverview.setDetails(getDetails(participant));

                    if (participant.getUser() != null) {
                        Profile profile = profileRepository.findOne(participant.getUser().getProfileId());

                        assessorInviteOverview.setId(participant.getUser().getId());
                        assessorInviteOverview.setBusinessType(profile.getBusinessType());
                        assessorInviteOverview.setCompliant(profile.isCompliant(participant.getUser()));
                        assessorInviteOverview.setInnovationAreas(simpleMap(profile.getInnovationAreas(), innovationAreaMapper::mapToResource));
                    } else {
                        assessorInviteOverview.setInnovationAreas(singletonList(
                                innovationAreaMapper.mapToResource(participant.getInvite().getInnovationArea())
                        ));
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

    @Override
    public ServiceResult<CompetitionInviteResource> inviteUser(NewUserStagedInviteResource stagedInvite) {
        return getByEmailAndCompetition(stagedInvite.getEmail(), stagedInvite.getCompetitionId()).handleSuccessOrFailure(
                failure -> getCompetition(stagedInvite.getCompetitionId())
                        .andOnSuccess(competition -> getInnovationArea(stagedInvite.getInnovationAreaId())
                                .andOnSuccess(innovationArea ->
                                        inviteUserToCompetition(
                                                stagedInvite.getName(),
                                                stagedInvite.getEmail(),
                                                competition,
                                                innovationArea
                                        )
                                )
                        )
                        .andOnSuccessReturn(competitionInviteMapper::mapToResource),
                success -> serviceFailure(Error.globalError(
                        "validation.competitionInvite.create.email.exists",
                        singletonList(stagedInvite.getEmail())
                ))
        );
    }

    @Override
    public ServiceResult<Void> inviteNewUsers(List<NewUserStagedInviteResource> newUserStagedInvites, long competitionId) {
        return getCompetition(competitionId).andOnSuccessReturn(competition ->
                mapWithIndex(newUserStagedInvites, (index, invite) ->
                        getByEmailAndCompetition(invite.getEmail(), competitionId).handleSuccessOrFailure(
                                failure -> getInnovationArea(invite.getInnovationAreaId())
                                        .andOnSuccess(innovationArea ->
                                                inviteUserToCompetition(invite.getName(), invite.getEmail(), competition, innovationArea)
                                        )
                                        .andOnFailure(() -> serviceFailure(Error.fieldError(
                                                "invites[" + index + "].innovationArea",
                                                invite.getInnovationAreaId(),
                                                "validation.competitionInvite.create.innovationArea.required"
                                                ))
                                        ),
                                success -> serviceFailure(Error.fieldError(
                                        "invites[" + index + "].email",
                                        invite.getEmail(),
                                        "validation.competitionInvite.create.email.exists"
                                ))
                        )
                ))
                .andOnSuccess(list -> aggregate(list))
                .andOnSuccessReturnVoid();
    }

    private String getDetails(CompetitionParticipant participant) {
        String details = null;

        if (participant.getStatus() == REJECTED) {
            details = format("Invite declined as %s", lowerCase(participant.getRejectionReason().getReason()));
        } else if (participant.getStatus() == PENDING) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            if (participant.getInvite().getSentOn() != null) {
                details = format("Invite sent: %s", participant.getInvite().getSentOn().format(formatter));
            }
        }

        return details;
    }

    private ServiceResult<InnovationArea> getInnovationArea(long innovationCategoryId) {
        return find(innovationAreaRepository.findOne(innovationCategoryId), notFoundError(Category.class, innovationCategoryId, INNOVATION_AREA));
    }

    private ServiceResult<CompetitionInvite> inviteUserToCompetition(String name, String email, Competition competition, InnovationArea innovationArea) {
        return serviceSuccess(
                competitionInviteRepository.save(new CompetitionInvite(name, email, generateInviteHash(), competition, innovationArea))
        );
    }

    @Override
    public ServiceResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource stagedInvite) {
        return getUserByEmail(stagedInvite.getEmail()) // I'm not particularly tied to finding by email, vs id
                .andOnSuccess(user -> inviteUserToCompetition(user, stagedInvite.getCompetitionId()))
                .andOnSuccessReturn(competitionInviteMapper::mapToResource);
    }

    private ServiceResult<CompetitionInvite> inviteUserToCompetition(User user, long competitionId) {
        return getCompetition(competitionId)
                .andOnSuccessReturn(
                        competition -> competitionInviteRepository.save(new CompetitionInvite(user, generateInviteHash(), competition))
                );
    }

    private ServiceResult<Competition> getCompetition(long competitionId) {
        return find(competitionRepository.findOne(competitionId), notFoundError(Competition.class, competitionId));
    }

    private ServiceResult<User> getUserByEmail(String email) {
        return find(userRepository.findByEmail(email), notFoundError(User.class, email));
    }

    @Override
    public ServiceResult<Void> sendInvite(long inviteId, AssessorInviteSendResource assessorInviteSendResource) {
        return getById(inviteId).andOnSuccess(invite -> {
            competitionParticipantRepository.save(new CompetitionParticipant(invite.send(loggedInUserSupplier.get(), ZonedDateTime.now())));

            if (invite.isNewAssessorInvite()) {
                userRepository.findByEmail(invite.getEmail()).ifPresent(this::addAssessorRoleToUser);
            }

            // Strip any HTML that may have been added to the content by the user.
            String bodyPlain = stripHtml(assessorInviteSendResource.getContent());

            // HTML'ify the plain content to add line breaks.
            String bodyHtml = plainTextToHtml(bodyPlain);

            NotificationTarget recipient = new ExternalUserNotificationTarget(invite.getName(), invite.getEmail());
            Notification notification = new Notification(systemNotificationSource, singletonList(recipient),
                    Notifications.INVITE_ASSESSOR, asMap(
                    "subject", assessorInviteSendResource.getSubject(),
                    "bodyPlain", bodyPlain,
                    "bodyHtml", bodyHtml
            ));

            return notificationSender.sendNotification(notification);
        }).andOnSuccessReturnVoid();
    }

    private void addAssessorRoleToUser(User user) {
        Role assessorRole = roleRepository.findOneByName(UserRoleType.ASSESSOR.getName());
        user.addRole(assessorRole);
    }

    @Override
    public ServiceResult<Void> deleteInvite(String email, long competitionId) {
        return getByEmailAndCompetition(email, competitionId).andOnSuccess(this::deleteInvite);
    }

    private ServiceResult<CompetitionInvite> getByHash(String inviteHash) {
        return find(competitionInviteRepository.getByHash(inviteHash), notFoundError(CompetitionInvite.class, inviteHash));
    }

    private ServiceResult<CompetitionInvite> getById(long id) {
        return find(competitionInviteRepository.findOne(id), notFoundError(CompetitionInvite.class, id));
    }

    private String getInviteContent(NotificationTarget notificationTarget, Map<String, Object> arguments) {
        return renderer.renderTemplate(systemNotificationSource, notificationTarget, "invite_assessor_editable_text.txt",
                arguments).getSuccessObject();
    }

    private ServiceResult<CompetitionInvite> getByEmailAndCompetition(String email, long competitionId) {
        return find(competitionInviteRepository.getByEmailAndCompetitionId(email, competitionId), notFoundError(CompetitionInvite.class, email, competitionId));
    }

    private ServiceResult<Void> deleteInvite(CompetitionInvite invite) {
        if (invite.getStatus() != CREATED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_INVITE_CANNOT_DELETE_ONCE_SENT, invite.getEmail()));
        }

        competitionInviteRepository.delete(invite);
        return serviceSuccess();
    }

    private ServiceResult<CompetitionInvite> getByHashIfOpen(String inviteHash) {
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

    private CompetitionInvite openInvite(CompetitionInvite invite) {
        return competitionInviteRepository.save(invite.open());
    }

    private ServiceResult<CompetitionParticipant> getParticipantByInviteHash(String inviteHash) {
        return find(competitionParticipantRepository.getByInviteHash(inviteHash), notFoundError(CompetitionParticipant.class, inviteHash));
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

    private ServiceResult<Participant> applyInnovationAreaToUserProfile(CompetitionParticipant participant, User user) {
        if (participant.getInvite().isNewAssessorInvite()) {
            return getProfileForUser(user).andOnSuccessReturn(
                    profile -> {
                        profile.addInnovationArea(participant.getInvite().getInnovationArea());
                        return participant;
                    }
            );
        } else {
            return serviceSuccess(participant);
        }
    }

    private ServiceResult<Profile> getProfileForUser(User user) {
        return find(profileRepository.findOne(user.getProfileId()), notFoundError(Profile.class, user.getProfileId()));
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

    private boolean isUserCompliant(CompetitionInvite competitionInvite) {
        if (competitionInvite == null || competitionInvite.getUser() == null) {
            return false;
        }
        Profile profile = profileRepository.findOne(competitionInvite.getUser().getProfileId());
        return profile.isCompliant(competitionInvite.getUser());
    }

    private List<InnovationAreaResource> getInnovationAreasForInvite(CompetitionInvite competitionInvite) {
        if (competitionInvite.isNewAssessorInvite()) {
            return singletonList(innovationAreaMapper.mapToResource(competitionInvite.getInnovationArea()));
        } else {
            return profileRepository.findOne(competitionInvite.getUser().getProfileId()).getInnovationAreas().stream()
                    .map(innovationAreaMapper::mapToResource)
                    .collect(toList());
        }
    }
}
