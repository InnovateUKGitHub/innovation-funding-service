package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.mapper.AssessorProfileMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.authentication.service.RestIdentityProviderService;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
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
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.review.repository.ReviewParticipantRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.AffiliationMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.RoleProfileStatusRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.assignedAssessmentStates;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_NOTIFY_FAILED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INTERVIEW_ASSESSOR;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.PANEL_ASSESSOR;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.interview.builder.InterviewInviteBuilder.newInterviewInvite;
import static org.innovateuk.ifs.interview.builder.InterviewParticipantBuilder.newInterviewParticipant;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.review.builder.ReviewInviteBuilder.newReviewInviteWithoutId;
import static org.innovateuk.ifs.review.builder.ReviewParticipantBuilder.newReviewParticipant;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class AssessorServiceImplTest extends BaseUnitTestMocksTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private InnovationAreaMapper innovationAreaMapper;

    @Mock
    private AssessmentInviteService assessmentInviteService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AssessorProfileMapper assessorProfileMapper;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AffiliationMapper affiliationMapper;

    @Mock
    private RoleProfileStatusRepository roleProfileStatusRepository;

    @Mock
    private InterviewParticipantRepository interviewParticipantRepository;

    @Mock
    private ReviewParticipantRepository reviewParticipantRepository;

    @InjectMocks
    private AssessorService assessorService = new AssessorServiceImpl();

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(assessorService, "webBaseUrl", "https://ifs-local-dev");
    }

    @Test
    public void registerAssessorByHash_callCorrectServicesAndHaveSuccessfulOutcome() {
        String hash = "testhash";
        String email = "email@example.com";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 567890")
                .withPassword("Password123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        InnovationAreaResource innovationAreaResource = newInnovationAreaResource().build();

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource()
                .withEmail(email)
                .withInnovationArea(innovationAreaResource)
                .build();

        when(profileRepository.findById(anyLong())).thenReturn(Optional.of(newProfile().build()));
        when(innovationAreaMapper.mapToDomain(innovationAreaResource)).thenReturn(newInnovationArea().build());

        when(assessmentInviteService.getInvite(hash)).thenReturn(serviceSuccess(competitionInviteResource));

        UserResource createdUserResource = newUserResource().build();
        User createdUser = newUser()
                .withEmailAddress(email)
                .withProfileId(8L)
                .build();

        List<AssessmentParticipant> participantsForOtherInvites = Stream.generate(
                () -> Mockito.spy(new AssessmentParticipant())).limit(2).collect(Collectors.toList());

        when(registrationService.createUser(userRegistrationResource)).thenReturn(serviceSuccess(createdUserResource));

        when(registrationService.activateAssessorAndSendDiversitySurvey(createdUserResource.getId())).thenReturn(serviceSuccess());
        when(assessmentInviteService.acceptInvite(hash, createdUserResource)).thenReturn(serviceSuccess());
        when(userRepository.findById(createdUserResource.getId())).thenReturn(Optional.of(createdUser));
        when(assessmentParticipantRepository.getByInviteEmail(email)).thenReturn(participantsForOtherInvites);

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userRegistrationResource);

        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(assessmentInviteService, registrationService,
                                  userRepository, assessmentParticipantRepository, innovationAreaMapper,
                                  profileRepository, roleProfileStatusRepository);
        inOrder.verify(assessmentInviteService).getInvite(hash);
        inOrder.verify(registrationService).createUser(userRegistrationResource);
        inOrder.verify(registrationService).activateAssessorAndSendDiversitySurvey(createdUserResource.getId());
        inOrder.verify(userRepository).findById(createdUserResource.getId());
        inOrder.verify(assessmentParticipantRepository).getByInviteEmail(email);
        inOrder.verify(assessmentParticipantRepository).saveAll(participantsForOtherInvites);
        inOrder.verify(roleProfileStatusRepository).save(any());
        inOrder.verify(profileRepository).findById(anyLong());
        inOrder.verify(innovationAreaMapper).mapToDomain(innovationAreaResource);
        inOrder.verify(profileRepository).save(any(Profile.class));
        inOrder.verifyNoMoreInteractions();

        participantsForOtherInvites.forEach(competitionParticipant -> {
            verify(competitionParticipant).setUser(createdUser);
        });
    }

    @Test
    public void registerAssessorByHash_inviteDoesNotExistResultsInFailureAndSkippingUserRegistrationAndInviteAcceptance() {
        String hash = "inviteHashNotExists";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 567890")
                .withPassword("Password123")
                .build();

        ServiceResult<CompetitionInviteResource> inviteResult = serviceFailure(notFoundError(AssessmentInvite.class, hash));

        when(assessmentInviteService.getInvite(hash)).thenReturn(inviteResult);

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userRegistrationResource);

        verify(assessmentInviteService).getInvite(hash);
        verifyNoMoreInteractions(registrationService);
        verifyNoMoreInteractions(assessmentInviteService);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(AssessmentInvite.class, "inviteHashNotExists")));
    }

    @Test
    public void registerAssessorByHash_userValidationFailureResultsInFailureAndNotAcceptingInvite() {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 567890")
                .withPassword("Password123")
                .build();

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource()
                .withEmail("email@example.com")
                .build();

        when(assessmentInviteService.getInvite(hash)).thenReturn(serviceSuccess(competitionInviteResource));

        when(registrationService.createUser(userRegistrationResource)).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userRegistrationResource);

        InOrder inOrder = inOrder(assessmentInviteService, registrationService);
        inOrder.verify(assessmentInviteService).getInvite(hash);
        inOrder.verify(registrationService).createUser(userRegistrationResource);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void hasApplicationsAssigned_Assessments() {
        long assessorId = 7L;

        when(assessmentRepository.existsByActivityStateInAndParticipantUserId(assignedAssessmentStates, assessorId)).thenReturn(TRUE);

        Boolean result = assessorService.hasApplicationsAssigned(assessorId).getSuccess();

        assertEquals(TRUE, result);

        InOrder inOrder = inOrder(assessmentRepository);
        inOrder.verify(assessmentRepository).existsByActivityStateInAndParticipantUserId(assignedAssessmentStates, assessorId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void hasApplicationsAssigned_ReviewPanel() {
        long assessorId = 7L;

        List<Milestone> milestones = newMilestone()
                .withDate(now().plusDays(1))
                .withType(ASSESSMENT_PANEL).build(1);

        Competition competition = newCompetition()
                .withMilestones(milestones)
                .build();

        ReviewInvite invite = newReviewInviteWithoutId()
                .withName("name1")
                .withEmail("test1@test.com")
                .withHash(generateInviteHash())
                .withCompetition(competition)
                .withStatus(SENT)
                .build();

        ReviewParticipant reviewParticipant = newReviewParticipant()
                .withCompetition(competition)
                .withInvite(invite)
                .build();

        when(reviewParticipantRepository.findByUserIdAndRole(assessorId, PANEL_ASSESSOR)).thenReturn(asList(reviewParticipant));
        when(assessmentRepository.existsByActivityStateInAndParticipantUserId(assignedAssessmentStates, assessorId)).thenReturn(FALSE);

        Boolean result = assessorService.hasApplicationsAssigned(assessorId).getSuccess();

        assertEquals(TRUE, result);

        InOrder inOrder = inOrder(assessmentRepository, reviewParticipantRepository);
        inOrder.verify(assessmentRepository).existsByActivityStateInAndParticipantUserId(assignedAssessmentStates, assessorId);
        inOrder.verify(reviewParticipantRepository).findByUserIdAndRole(assessorId, PANEL_ASSESSOR);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void hasApplicationsAssigned_Interview() {
        long assessorId = 7L;

        List<Milestone> milestones = newMilestone()
                .withDate(now().plusDays(1))
                .withType(PANEL_DATE).build(1);

        Competition competition = newCompetition()
                .withMilestones(milestones)
                .build();

        InterviewInvite invite = newInterviewInvite()
                .withName("name1")
                .withEmail("test1@test.com")
                .withHash(generateInviteHash())
                .withCompetition(competition)
                .withStatus(SENT)
                .build();

        InterviewParticipant interviewParticipant = newInterviewParticipant()
                .withCompetition(competition)
                .withInvite(invite)
                .build();

        when(interviewParticipantRepository.findByUserIdAndRole(assessorId, INTERVIEW_ASSESSOR)).thenReturn(asList(interviewParticipant));
        when(reviewParticipantRepository.findByUserIdAndRole(assessorId, PANEL_ASSESSOR)).thenReturn(emptyList());
        when(assessmentRepository.existsByActivityStateInAndParticipantUserId(assignedAssessmentStates, assessorId)).thenReturn(FALSE);

        Boolean result = assessorService.hasApplicationsAssigned(assessorId).getSuccess();

        assertEquals(TRUE, result);

        InOrder inOrder = inOrder(assessmentRepository,reviewParticipantRepository, interviewParticipantRepository);
        inOrder.verify(assessmentRepository).existsByActivityStateInAndParticipantUserId(assignedAssessmentStates, assessorId);
        inOrder.verify(reviewParticipantRepository).findByUserIdAndRole(assessorId, INTERVIEW_ASSESSOR);
        inOrder.verify(interviewParticipantRepository).findByUserIdAndRole(assessorId, INTERVIEW_ASSESSOR);
        inOrder.verifyNoMoreInteractions();
    }



    public boolean hasAnyInterviewsAssigned(long userId) {
        return interviewParticipantRepository
                .findByUserIdAndRole(userId, INTERVIEW_ASSESSOR)
                .stream()
                .filter(participant -> now().isBefore(participant.getInvite().getTarget().getPanelDate()))
                .findAny()
                .isPresent();
    }

    public boolean hasAnyPanelsAssigned(long userId) {
        return reviewParticipantRepository
                .findByUserIdAndRole(userId, PANEL_ASSESSOR)
                .stream()
                .filter(participant -> now().isBefore(participant.getInvite().getTarget().getAssessmentPanelDate()))
                .findAny()
                .isPresent();
    }

    public boolean hasAnyAssessmentsAssigned(long userId) {
        return assessmentRepository.existsByActivityStateInAndParticipantUserId(assignedAssessmentStates, userId);
    }

    @Test
    public void getAssessorProfile() {
        long assessorId = 7L;
        long profileId = 11L;

        Optional<User> user = Optional.of(
                newUser()
                        .withProfileId(profileId)
                        .build()
        );
        Profile profile = newProfile().build();

        UserResource userResource = newUserResource().build();
        ProfileResource profileResource = newProfileResource().build();

        when(userRepository.findByIdAndRoles(assessorId, Role.ASSESSOR)).thenReturn(user);
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(userMapper.mapToResource(user.get())).thenReturn(userResource);
        when(assessorProfileMapper.mapToResource(profile)).thenReturn(profileResource);

        AssessorProfileResource expectedAssessorProfileResource = newAssessorProfileResource()
                .withUser(userResource)
                .withProfile(profileResource)
                .build();

        AssessorProfileResource actualAssessorProfileResource = assessorService.getAssessorProfile(assessorId).getSuccess();

        assertEquals(expectedAssessorProfileResource, actualAssessorProfileResource);

        InOrder inOrder = inOrder(userRepository, profileRepository, userMapper, assessorProfileMapper, affiliationMapper);
        inOrder.verify(userRepository).findByIdAndRoles(assessorId, Role.ASSESSOR);
        inOrder.verify(profileRepository).findById(profileId);
        inOrder.verify(userMapper).mapToResource(user.get());
        inOrder.verify(assessorProfileMapper).mapToResource(profile);
        inOrder.verify(affiliationMapper).mapToResource(user.get().getAffiliations());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void notifyAssessorsByCompetition() {
        long competitionId = 1L;

        Competition competition = newCompetition()
                .withId(competitionId)
                .withName("Test Competition")
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().minusDays(1))
                .build();

        List<User> users = newUser()
                .withFirstName("Johnny", "Mary")
                .withLastName("Doe", "Poppins")
                .build(2);
        List<Assessment> assessments = newAssessment()
                .withId(2L, 3L)
                .withProcessState(AssessmentState.CREATED)
                .withApplication(
                        newApplication().withCompetition(competition).build(),
                        newApplication().withCompetition(competition).build()
                )
                .withParticipant(
                        newProcessRole().withUser(users.get(0)).build(),
                        newProcessRole().withUser(users.get(1)).build()
                )
                .build(2);

        List<EmailContent> emailContents = newEmailContentResource()
                .build(2);

        List<NotificationTarget> recipients = asList(
                new UserNotificationTarget(users.get(0).getName(), users.get(0).getEmail()),
                new UserNotificationTarget(users.get(1).getName(), users.get(1).getEmail())
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");

        Notification expectedNotification1 = new Notification(
                systemNotificationSource,
                singletonList(recipients.get(0)),
                AssessorServiceImpl.Notifications.ASSESSOR_HAS_ASSESSMENTS,
                asMap(
                        "name", users.get(0).getName(),
                        "competitionName", competition.getName(),
                        "acceptsDeadline", competition.getAssessorAcceptsDate().format(formatter),
                        "assessmentDeadline", competition.getAssessorDeadlineDate().format(formatter),
                        "competitionUrl", format("%s/assessor/dashboard/competition/%s", "https://ifs-local-dev/assessment", competition.getId()))
        );

        Notification expectedNotification2 = new Notification(
                systemNotificationSource,
                singletonList(recipients.get(1)),
                AssessorServiceImpl.Notifications.ASSESSOR_HAS_ASSESSMENTS,
                asMap(
                        "name", users.get(1).getName(),
                        "competitionName", competition.getName(),
                        "acceptsDeadline", competition.getAssessorAcceptsDate().format(formatter),
                        "assessmentDeadline", competition.getAssessorDeadlineDate().format(formatter),
                        "competitionUrl", format("%s/assessor/dashboard/competition/%s", "https://ifs-local-dev/assessment", competition.getId()))
        );

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(assessmentRepository.findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId)).thenReturn(assessments);
        when(assessmentWorkflowHandler.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandler.notify(same(assessments.get(1)))).thenReturn(true);

        List<Notification> notifications = asList(expectedNotification1, expectedNotification2);

        notifications.forEach(notification -> when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess()));

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepository, competitionRepository, assessmentWorkflowHandler, notificationService);
        inOrder.verify(competitionRepository).findById(competitionId);
        inOrder.verify(assessmentRepository).findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandler).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandler).notify(same(assessments.get(1)));
        notifications.forEach(notification -> inOrder.verify(notificationService).sendNotificationWithFlush(notification, EMAIL));

        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getErrors().isEmpty());
    }

    @Test
    public void notifyAssessorsByCompetition_oneEmailPerUser() {
        Long competitionId = 1L;

        Competition competition = newCompetition()
                .withId(competitionId)
                .withName("Test Competition")
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().minusDays(1))
                .build();
        User user = newUser().build();

        List<Assessment> assessments = newAssessment()
                .withId(2L, 3L)
                .withProcessState(AssessmentState.CREATED)
                .withApplication(
                        newApplication().withCompetition(competition).build(),
                        newApplication().withCompetition(competition).build()
                )
                .withParticipant(
                        newProcessRole().withUser(user).build(),
                        newProcessRole().withUser(user).build()
                )
                .build(2);

        EmailContent emailContent = newEmailContentResource().build();
        NotificationTarget recipient = new UserNotificationTarget(user.getName(), user.getEmail());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");

        Notification expectedNotification = new Notification(
                systemNotificationSource,
                singletonList(recipient),
                AssessorServiceImpl.Notifications.ASSESSOR_HAS_ASSESSMENTS,
                asMap(
                        "name", user.getName(),
                        "competitionName", competition.getName(),
                        "acceptsDeadline", competition.getAssessorAcceptsDate().format(formatter),
                        "assessmentDeadline", competition.getAssessorDeadlineDate().format(formatter),
                        "competitionUrl", format("%s/assessor/dashboard/competition/%s", "https://ifs-local-dev/assessment", competition.getId()))
        );

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(assessmentRepository.findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId)).thenReturn(assessments);
        when(assessmentWorkflowHandler.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandler.notify(same(assessments.get(1)))).thenReturn(true);

        when(notificationService.sendNotificationWithFlush(expectedNotification, EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepository, competitionRepository, assessmentWorkflowHandler, notificationService);
        inOrder.verify(competitionRepository).findById(competitionId);
        inOrder.verify(assessmentRepository).findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandler).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandler).notify(same(assessments.get(1)));
        inOrder.verify(notificationService).sendNotificationWithFlush(expectedNotification, EMAIL);

        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getErrors().isEmpty());
    }

    @Test
    public void notifyAssessorsByCompetition_competitionNotFound() {
        long competitionId = 1L;

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.empty());

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        verify(competitionRepository).findById(competitionId);
        verifyNoMoreInteractions(assessmentRepository, competitionRepository, assessmentWorkflowHandler, notificationService);

        assertTrue(serviceResult.isFailure());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void notifyAssessorsByCompetition_oneTransitionFails() {
        long competitionId = 1L;

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();
        List<Assessment> assessments = newAssessment()
                .withProcessState(AssessmentState.CREATED)
                .withId(2L, 3L)
                .build(2);

        when(assessmentRepository.findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId))
                .thenReturn(assessments);
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(assessmentWorkflowHandler.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandler.notify(same(assessments.get(1)))).thenReturn(false);

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepository, competitionRepository, assessmentWorkflowHandler, notificationService);
        inOrder.verify(competitionRepository).findById(competitionId);
        inOrder.verify(assessmentRepository).findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandler).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandler).notify(same(assessments.get(1)));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void notifyAssessorsByCompetition_allTransitionsFail() {
        long competitionId = 1L;

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();
        List<Assessment> assessments = newAssessment()
                .withProcessState(AssessmentState.CREATED)
                .withId(2L, 3L)
                .build(2);

        when(assessmentRepository.findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId))
                .thenReturn(assessments);
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(assessmentWorkflowHandler.notify(same(assessments.get(0)))).thenReturn(false);
        when(assessmentWorkflowHandler.notify(same(assessments.get(1)))).thenReturn(false);

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepository, competitionRepository, assessmentWorkflowHandler, notificationService);
        inOrder.verify(competitionRepository).findById(competitionId);
        inOrder.verify(assessmentRepository).findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandler).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandler).notify(same(assessments.get(1)));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(2, serviceResult.getErrors().size());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(1).getErrorKey());
    }
}