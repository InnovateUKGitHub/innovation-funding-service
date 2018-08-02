package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.BuilderAmendFunctions;
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
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.AffiliationMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
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

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_NOTIFY_FAILED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Disability.NO;
import static org.innovateuk.ifs.user.resource.Gender.NOT_STATED;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class AssessorServiceImplTest extends BaseUnitTestMocksTest {

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    @Mock
    private AssessmentWorkflowHandler assessmentWorkflowHandlerMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ProfileRepository profileRepositoryMock;

    @Mock
    private InnovationAreaMapper innovationAreaMapperMock;

    @Mock
    private AssessmentInviteService assessmentInviteServiceMock;

    @Mock
    private RegistrationService registrationServiceMock;

    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepositoryMock;

    @Mock
    private UserMapper userMapperMock;

    @Mock
    private AssessorProfileMapper assessorProfileMapperMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private AffiliationMapper affiliationMapperMock;

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
                .withGender(NOT_STATED)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withDisability(NO)
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

        when(profileRepositoryMock.findById(anyLong())).thenReturn(Optional.of(newProfile().build()));
        when(innovationAreaMapperMock.mapToDomain(innovationAreaResource)).thenReturn(newInnovationArea().build());

        when(assessmentInviteServiceMock.getInvite(hash)).thenReturn(serviceSuccess(competitionInviteResource));

        UserResource createdUserResource = newUserResource().build();
        User createdUser = newUser()
                .withEmailAddress(email)
                .withProfileId(8L)
                .build();

        List<AssessmentParticipant> participantsForOtherInvites = Stream.generate(
                () -> Mockito.spy(new AssessmentParticipant())).limit(2).collect(Collectors.toList());

        when(registrationServiceMock.createUser(userRegistrationResource)).thenReturn(serviceSuccess(createdUserResource));

        when(registrationServiceMock.activateAssessorAndSendDiversitySurvey(createdUserResource.getId())).thenReturn(serviceSuccess());
        when(assessmentInviteServiceMock.acceptInvite(hash, createdUserResource)).thenReturn(serviceSuccess());
        when(userRepositoryMock.findById(createdUserResource.getId())).thenReturn(Optional.of(createdUser));
        when(assessmentParticipantRepositoryMock.getByInviteEmail(email)).thenReturn(participantsForOtherInvites);

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userRegistrationResource);

        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(assessmentInviteServiceMock, registrationServiceMock,
                                  userRepositoryMock, assessmentParticipantRepositoryMock, innovationAreaMapperMock, profileRepositoryMock);
        inOrder.verify(assessmentInviteServiceMock).getInvite(hash);
        inOrder.verify(registrationServiceMock).createUser(userRegistrationResource);
        inOrder.verify(registrationServiceMock).activateAssessorAndSendDiversitySurvey(createdUserResource.getId());
        inOrder.verify(userRepositoryMock).findById(createdUserResource.getId());
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteEmail(email);
        inOrder.verify(assessmentParticipantRepositoryMock).saveAll(participantsForOtherInvites);
        inOrder.verify(profileRepositoryMock).findById(anyLong());
        inOrder.verify(innovationAreaMapperMock).mapToDomain(innovationAreaResource);
        inOrder.verify(profileRepositoryMock).save(any(Profile.class));
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
                .withGender(NOT_STATED)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withDisability(NO)
                .withPassword("Password123")
                .build();

        ServiceResult<CompetitionInviteResource> inviteResult = serviceFailure(notFoundError(AssessmentInvite.class, hash));

        when(assessmentInviteServiceMock.getInvite(hash)).thenReturn(inviteResult);

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userRegistrationResource);

        verify(assessmentInviteServiceMock).getInvite(hash);
        verifyNoMoreInteractions(registrationServiceMock);
        verifyNoMoreInteractions(assessmentInviteServiceMock);

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
                .withGender(NOT_STATED)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withDisability(NO)
                .withPassword("Password123")
                .build();

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource()
                .withEmail("email@example.com")
                .build();

        when(assessmentInviteServiceMock.getInvite(hash)).thenReturn(serviceSuccess(competitionInviteResource));

        when(registrationServiceMock.createUser(userRegistrationResource)).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userRegistrationResource);

        InOrder inOrder = inOrder(assessmentInviteServiceMock, registrationServiceMock);
        inOrder.verify(assessmentInviteServiceMock).getInvite(hash);
        inOrder.verify(registrationServiceMock).createUser(userRegistrationResource);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
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

        when(userRepositoryMock.findByIdAndRoles(assessorId, Role.ASSESSOR)).thenReturn(user);
        when(profileRepositoryMock.findById(profileId)).thenReturn(Optional.of(profile));
        when(userMapperMock.mapToResource(user.get())).thenReturn(userResource);
        when(assessorProfileMapperMock.mapToResource(profile)).thenReturn(profileResource);

        AssessorProfileResource expectedAssessorProfileResource = newAssessorProfileResource()
                .withUser(userResource)
                .withProfile(profileResource)
                .build();

        AssessorProfileResource actualAssessorProfileResource = assessorService.getAssessorProfile(assessorId).getSuccess();

        assertEquals(expectedAssessorProfileResource, actualAssessorProfileResource);

        InOrder inOrder = inOrder(userRepositoryMock, profileRepositoryMock, userMapperMock, assessorProfileMapperMock, affiliationMapperMock);
        inOrder.verify(userRepositoryMock).findByIdAndRoles(assessorId, Role.ASSESSOR);
        inOrder.verify(profileRepositoryMock).findById(profileId);
        inOrder.verify(userMapperMock).mapToResource(user.get());
        inOrder.verify(assessorProfileMapperMock).mapToResource(profile);
        inOrder.verify(affiliationMapperMock).mapToResource(user.get().getAffiliations());
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
                systemNotificationSourceMock,
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
                systemNotificationSourceMock,
                singletonList(recipients.get(1)),
                AssessorServiceImpl.Notifications.ASSESSOR_HAS_ASSESSMENTS,
                asMap(
                        "name", users.get(1).getName(),
                        "competitionName", competition.getName(),
                        "acceptsDeadline", competition.getAssessorAcceptsDate().format(formatter),
                        "assessmentDeadline", competition.getAssessorDeadlineDate().format(formatter),
                        "competitionUrl", format("%s/assessor/dashboard/competition/%s", "https://ifs-local-dev/assessment", competition.getId()))
        );

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(assessmentRepositoryMock.findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId)).thenReturn(assessments);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(true);

        List<Notification> notifications = asList(expectedNotification1, expectedNotification2);

        notifications.forEach(notification -> when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess()));

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationServiceMock);
        inOrder.verify(competitionRepositoryMock).findById(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        notifications.forEach(notification -> inOrder.verify(notificationServiceMock).sendNotificationWithFlush(notification, EMAIL));

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
                systemNotificationSourceMock,
                singletonList(recipient),
                AssessorServiceImpl.Notifications.ASSESSOR_HAS_ASSESSMENTS,
                asMap(
                        "name", user.getName(),
                        "competitionName", competition.getName(),
                        "acceptsDeadline", competition.getAssessorAcceptsDate().format(formatter),
                        "assessmentDeadline", competition.getAssessorDeadlineDate().format(formatter),
                        "competitionUrl", format("%s/assessor/dashboard/competition/%s", "https://ifs-local-dev/assessment", competition.getId()))
        );

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(assessmentRepositoryMock.findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId)).thenReturn(assessments);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(true);

        when(notificationServiceMock.sendNotificationWithFlush(expectedNotification, EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationServiceMock);
        inOrder.verify(competitionRepositoryMock).findById(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(expectedNotification, EMAIL);

        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getErrors().isEmpty());
    }

    @Test
    public void notifyAssessorsByCompetition_competitionNotFound() {
        long competitionId = 1L;

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.empty());

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        verify(competitionRepositoryMock).findById(competitionId);
        verifyNoMoreInteractions(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationServiceMock);

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

        when(assessmentRepositoryMock.findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId))
                .thenReturn(assessments);
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(false);

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationServiceMock);
        inOrder.verify(competitionRepositoryMock).findById(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
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

        when(assessmentRepositoryMock.findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId))
                .thenReturn(assessments);
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(false);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(false);

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationServiceMock);
        inOrder.verify(competitionRepositoryMock).findById(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateAndTargetCompetitionId(AssessmentState.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(2, serviceResult.getErrors().size());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(1).getErrorKey());
    }
}