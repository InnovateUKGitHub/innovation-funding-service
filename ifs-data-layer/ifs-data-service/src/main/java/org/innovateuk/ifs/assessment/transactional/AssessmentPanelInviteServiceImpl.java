package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.mapper.CompetitionInviteMapper;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.repository.AssessmentPanelInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.invite.constant.InviteStatus.*;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.util.CollectionFunctions.mapWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for managing {@link AssessmentPanelInvite}s.
 */
@Service
@Transactional
public class AssessmentPanelInviteServiceImpl implements AssessmentPanelInviteService {

    private static final String WEB_CONTEXT = "/assessment";
    private static final DateTimeFormatter inviteFormatter = ofPattern("d MMMM yyyy");
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

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_ASSESSOR,
        INVITE_ASSESSOR_GROUP
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

