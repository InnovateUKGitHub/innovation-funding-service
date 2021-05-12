package org.innovateuk.ifs.assessment.transactional;


import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.mapper.AssessorProfileMapper;
import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.period.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.review.repository.ReviewParticipantRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.AffiliationMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.RoleProfileStatusRepository;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.CREATED;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.assignedAssessmentStates;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_NOTIFY_FAILED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CANNONT_NOTIFY_BY_COMPETITION_WHEN_MULTIPLE_ASSESSMENT_PERIODS;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INTERVIEW_ASSESSOR;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.PANEL_ASSESSOR;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.UserCreationResource.UserCreationResourceBuilder.anUserCreationResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

@Service
public class AssessorServiceImpl extends BaseTransactionalService implements AssessorService {

    private static final String WEB_CONTEXT = "/assessment";

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private AssessmentInviteService assessmentInviteService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private InterviewParticipantRepository interviewParticipantRepository;

    @Autowired
    private ReviewParticipantRepository reviewParticipantRepository;

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
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Autowired
    private RoleProfileStatusRepository roleProfileStatusRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Override
    @Transactional
    public ServiceResult<Void> registerAssessorByHash(String inviteHash, UserRegistrationResource userRegistrationResource) {

        return retrieveInvite(inviteHash).andOnSuccess(inviteResource -> {
            userRegistrationResource.setEmail(inviteResource.getEmail());
            userRegistrationResource.setRoles(singletonList(Role.ASSESSOR));
            return createUser(userRegistrationResource).andOnSuccessReturnVoid(created -> {
                assignCompetitionParticipantsToUser(created);
                createAssessorRoleProfileStatus(created);
                Profile profile = profileRepository.findById(created.getProfileId()).get();
                // profile is guaranteed to have been created by createUser(...)
                profile.addInnovationArea(innovationAreaMapper.mapToDomain(inviteResource.getInnovationArea()));
                profileRepository.save(profile);
            });
        });
    }

    private void createAssessorRoleProfileStatus(User user) {
        roleProfileStatusRepository.save(new RoleProfileStatus(user, ProfileRole.ASSESSOR));
    }

    @Override
    public ServiceResult<AssessorProfileResource> getAssessorProfile(long assessorId) {
        return getUser(assessorId)
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
    @Transactional
    public ServiceResult<Void> notifyAssessorsByCompetition(long competitionId) {
        return getCompetition(competitionId).andOnSuccess(competition-> {
            List<AssessmentPeriod> assessmentPeriods = assessmentPeriodRepository.findByCompetitionId(competitionId);
            return assessmentPeriods.size() == 1 ?
                    notifyAssessorsByAssessmentPeriodId(assessmentPeriods.get(0).getId()) :
                    serviceFailure(CANNONT_NOTIFY_BY_COMPETITION_WHEN_MULTIPLE_ASSESSMENT_PERIODS);
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> notifyAssessorsByAssessmentPeriodId(long id) {
        return find(assessmentPeriodRepository.findById(id), notFoundError(Assessment.class, id))
                .andOnSuccess(assessmentPeriod -> {
                    List<Assessment> assessmentsToNotify = assessmentRepository.findByTargetAssessmentPeriodIdAndAndActivityStateIn(assessmentPeriod.getId(), EnumSet.of(CREATED));
                    return processAnyFailuresOrSucceed(simpleMap(assessmentsToNotify, this::attemptNotifyAssessorTransition))
                            .andOnSuccess(() -> assessmentsToNotify.stream()
                                    .collect(groupingBy(assessment -> assessment.getParticipant().getUser()))
                                    .forEach((user, userAssessments) -> sendNotificationToAssessor(user, assessmentPeriod))
                            );
                });
    }

    @Override
    public ServiceResult<Boolean> hasApplicationsAssigned(long assessorId) {
        return serviceSuccess(hasAnyAssessmentsAssigned(assessorId) || hasAnyPanelsAssigned(assessorId) || hasAnyInterviewsAssigned(assessorId));
    }

    private boolean hasAnyInterviewsAssigned(long userId) {
        return interviewParticipantRepository
                .findByUserIdAndRole(userId, INTERVIEW_ASSESSOR)
                .stream()
                .filter(participant -> now().isBefore(participant.getInvite().getTarget().getPanelDate()))
                .findAny()
                .isPresent();
    }

    private boolean hasAnyPanelsAssigned(long userId) {
        return reviewParticipantRepository
                .findByUserIdAndRole(userId, PANEL_ASSESSOR)
                .stream()
                .filter(participant -> now().isBefore(participant.getInvite().getTarget().getAssessmentPanelDate()))
                .findAny()
                .isPresent();
    }

    private boolean hasAnyAssessmentsAssigned(long userId) {
        return assessmentRepository
                .findByActivityStateInAndParticipantUserId(assignedAssessmentStates, userId)
                .stream()
                .filter(assessment -> isAssessmentClosed(assessment.getTarget().getCompetition()))
                .findAny()
                .isPresent();
    }

    private boolean isAssessmentClosed(Competition competition) {
        if (competition.getAssessmentClosedDate() == null) {
            return true;
        }

        return now().isBefore(competition.getAssessmentClosedDate());
    }

    private ServiceResult<Void> attemptNotifyAssessorTransition(Assessment assessment) {
        if (!assessmentWorkflowHandler.notify(assessment)) {
            return serviceFailure(ASSESSMENT_NOTIFY_FAILED);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> sendNotificationToAssessor(User user, AssessmentPeriod assessmentPeriod) {
        NotificationTarget recipient = new UserNotificationTarget(user.getName(), user.getEmail());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        Competition competition = assessmentPeriod.getCompetition();
        Notification notification = new Notification(
                systemNotificationSource,
                recipient,
                Notifications.ASSESSOR_HAS_ASSESSMENTS,
                asMap(
                        "name", user.getName(),
                        "competitionName", competition.getName(),
                        "competitionId", competition.getId(),
                        "acceptsDeadline", TimeZoneUtil.toUkTimeZone(competition.getAssessorAcceptsDate(assessmentPeriod)).format(formatter),
                        "assessmentDeadline", TimeZoneUtil.toUkTimeZone(competition.getAssessorDeadlineDate(assessmentPeriod)).format(formatter),
                        "competitionUrl", format("%s/assessor/dashboard/competition/%s", webBaseUrl + WEB_CONTEXT, competition.getId()))
        );

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private ServiceResult<Profile> getProfile(Long profileId) {
        return find(profileRepository.findById(profileId), notFoundError(Profile.class, profileId));
    }

    private ServiceResult<CompetitionInviteResource> retrieveInvite(String inviteHash) {
        return assessmentInviteService.getInvite(inviteHash);
    }

    private void assignCompetitionParticipantsToUser(User user) {
        List<AssessmentParticipant> competitionParticipants = assessmentParticipantRepository.getByInviteEmail(user.getEmail());
        competitionParticipants.forEach(competitionParticipant -> competitionParticipant.setUser(user));
        assessmentParticipantRepository.saveAll(competitionParticipants);
    }

    private ServiceResult<User> createUser(UserRegistrationResource userRegistrationResource) {
        return registrationService.createUser(anUserCreationResource()
                .withFirstName(userRegistrationResource.getFirstName())
                .withLastName(userRegistrationResource.getLastName())
                .withEmail(userRegistrationResource.getEmail())
                .withPassword(userRegistrationResource.getPassword())
                .withAddress(userRegistrationResource.getAddress())
                .withPhoneNumber(userRegistrationResource.getPhoneNumber())
                .withRole(Role.ASSESSOR)
                .build())
                .andOnSuccess(
                        created -> registrationService.activateAssessorAndSendDiversitySurvey(created.getId()).andOnSuccessReturn(
                        result -> userRepository.findById(created.getId()).orElse(null)
                )
        );
    }

    enum Notifications {
        ASSESSOR_HAS_ASSESSMENTS
    }
}