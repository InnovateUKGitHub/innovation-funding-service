package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.mapper.AssessorProfileMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.AffiliationMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProfileRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.RoleService;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_NOTIFY_FAILED;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

@Service
public class AssessorServiceImpl extends BaseTransactionalService implements AssessorService {

    private static final String WEB_CONTEXT = "/assessment";

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private CompetitionInviteService competitionInviteService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessorProfileMapper assessorProfileMapper;

    @Autowired
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AffiliationMapper affiliationMapper;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Override
    public ServiceResult<Void> registerAssessorByHash(String inviteHash, UserRegistrationResource userRegistrationResource) {

        // TODO: Handle failures gracefully and hand them back to the webservice
        return retrieveInvite(inviteHash).andOnSuccess(inviteResource -> {
            userRegistrationResource.setEmail(inviteResource.getEmail());
            return getAssessorRoleResource().andOnSuccess(assessorRole -> {
                userRegistrationResource.setRoles(singletonList(assessorRole));
                return createUser(userRegistrationResource).andOnSuccessReturnVoid(created -> {
                    assignCompetitionParticipantsToUser(created);
                    Profile profile = profileRepository.findOne(created.getProfileId());
                    // profile is guaranteed to have been created by createUser(...)
                    profile.addInnovationArea(innovationAreaMapper.mapToDomain(inviteResource.getInnovationArea()));
                    profileRepository.save(profile);
                });
            });
        });
    }

    @Override
    public ServiceResult<AssessorProfileResource> getAssessorProfile(Long assessorId) {
        return getAssessor(assessorId)
                .andOnSuccess(user -> getProfile(user.getProfileId())
                        .andOnSuccessReturn(
                                profile -> {
                                    // TODO INFUND-7750 - tidy up assessor profile DTOs
                                    UserResource userResource = userMapper.mapToResource(user);
                                    ProfileResource profileResource = assessorProfileMapper.mapToResource(profile);
                                    profileResource.setAffiliations(affiliationMapper.mapToResource(user.getAffiliations()));
                                    return new AssessorProfileResource(
                                            userResource,
                                            profileResource
                                    );
                                }
                        )
                );
    }

    @Override
    public ServiceResult<Void> notifyAssessorsByCompetition(long competitionId) {
        return getCompetition(competitionId).andOnSuccess(competition -> {
            List<Assessment> assessments = assessmentRepository.findByActivityStateStateAndTargetCompetitionId(
                    AssessmentStates.CREATED.getBackingState(),
                    competitionId
            );

            return processAnyFailuresOrSucceed(simpleMap(assessments, this::attemptNotifyAssessorTransition))
                    .andOnSuccess(() -> assessments.stream()
                            .collect(Collectors.groupingBy(assessment -> assessment.getParticipant().getUser()))
                            .forEach((user, userAssessments) -> sendNotification(user, competition))
                    );
        });
    }

    private ServiceResult<Void> attemptNotifyAssessorTransition(Assessment assessment) {
        if (!assessmentWorkflowHandler.notify(assessment)) {
            return serviceFailure(ASSESSMENT_NOTIFY_FAILED);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> sendNotification(User user, Competition competition) {
        NotificationTarget recipient = new UserNotificationTarget(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        Notification notification = new Notification(
                systemNotificationSource,
                singletonList(recipient),
                AssessmentServiceImpl.Notifications.ASSESSOR_HAS_ASSESSMENTS,
                asMap(
                        "name", user.getName(),
                        "competitionName", competition.getName(),
                        "acceptsDeadline", TimeZoneUtil.toUkTimeZone(competition.getAssessorAcceptsDate()).format(formatter),
                        "assessmentDeadline", TimeZoneUtil.toUkTimeZone(competition.getAssessorDeadlineDate()).format(formatter),
                        "competitionUrl", format("%s/assessor/dashboard/competition/%s", webBaseUrl + WEB_CONTEXT, competition.getId()))
        );

        EmailContent content = notificationSender.renderTemplates(notification).getSuccessObject().get(recipient);

        return notificationSender.sendEmailWithContent(notification, recipient, content).andOnSuccessReturnVoid();
    }

    private ServiceResult<Profile> getProfile(Long profileId) {
        return find(profileRepository.findOne(profileId), notFoundError(Profile.class, profileId));
    }

    private ServiceResult<User> getAssessor(long assessorId) {
        return find(userRepository.findByIdAndRolesName(assessorId, ASSESSOR.getName()), notFoundError(User.class, assessorId));
    }

    private ServiceResult<CompetitionInviteResource> retrieveInvite(String inviteHash) {
        return competitionInviteService.getInvite(inviteHash);
    }

    private void assignCompetitionParticipantsToUser(User user) {
        List<CompetitionParticipant> competitionParticipants = competitionParticipantRepository.getByInviteEmail(user.getEmail());
        competitionParticipants.forEach(competitionParticipant -> competitionParticipant.setUser(user));
        competitionParticipantRepository.save(competitionParticipants);
    }

    private ServiceResult<RoleResource> getAssessorRoleResource() {
        return roleService.findByUserRoleType(ASSESSOR);
    }

    private ServiceResult<User> createUser(UserRegistrationResource userRegistrationResource) {
        return registrationService.createUser(userRegistrationResource).andOnSuccess(
                created -> registrationService.activateUser(created.getId()).andOnSuccessReturn(result -> userRepository.findOne(created.getId())));
    }
}
