package org.innovateuk.ifs.assessment.transactional;


import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.AssessmentPanelInvite;
import org.innovateuk.ifs.invite.repository.AssessmentPanelInviteRepository;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.assessment.mapper.CompetitionInviteMapper;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
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


    private static final DateTimeFormatter inviteFormatter = ofPattern("d MMMM yyyy");
    private static final String WEB_CONTEXT = "/assessment";
    private static final DateTimeFormatter detailsFormatter = ofPattern("dd MMM yyyy");


    @Autowired
    private AssessmentPanelInviteRepository assessmentPanelInviteRepository;

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

    enum Notifications {
        INVITE_ASSESSOR,
        INVITE_ASSESSOR_GROUP
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
                    ""
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
                        return sendInviteNotification(
                                assessorInviteSendResource.getSubject(),
                                inviteFormatter,
                                customTextPlain,
                                customTextHtml,
                                invite,
                                CompetitionInviteServiceImpl.Notifications.INVITE_ASSESSOR_GROUP
                        );
                    }
            ));
        });
    }


        @Override
        public ServiceResult<AvailableAssessorPageResource> getAvailableAssessors ( long competitionId, Pageable
        pageable){
            final Page<CompetitionParticipant> pagedResult = competitionParticipantRepository.findParticipantsNotOnPanel(competitionId, pageable);

            return serviceSuccess(new AvailableAssessorPageResource(
                    pagedResult.getTotalElements(),
                    pagedResult.getTotalPages(),
                    simpleMap(pagedResult.getContent(), this::mapToAvailableAssessorResource),
                    pagedResult.getNumber(),
                    pagedResult.getSize()
            ));
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
        return renderer.renderTemplate(systemNotificationSource, notificationTarget, "invite_assessor_preview_text.txt",
                arguments).getSuccessObject();
    }


    private ServiceResult<Void> sendInviteNotification(String subject,
                                                       DateTimeFormatter formatter,
                                                       String customTextPlain,
                                                       String customTextHtml,
                                                       AssessmentPanelInvite invite,
                                                       CompetitionInviteServiceImpl.Notifications notificationType) {
        NotificationTarget recipient = new ExternalUserNotificationTarget(invite.getName(), invite.getEmail());
        Notification notification = new Notification(
                systemNotificationSource,
                recipient,
                notificationType,
                asMap(
                        "subject", subject,
                        "name", invite.getName(),
                        "competitionName", invite.getTarget().getName(),
                        "acceptsDate", invite.getTarget().getAssessorAcceptsDate().format(formatter),
                        "deadlineDate", invite.getTarget().getAssessorDeadlineDate().format(formatter),
                        "inviteUrl", format("%s/invite/competition/%s", webBaseUrl + WEB_CONTEXT, invite.getHash()),
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


