package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.BuilderAmendFunctions;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.authentication.service.RestIdentityProviderService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
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
import static java.util.Collections.emptyList;
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
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Disability.NO;
import static org.innovateuk.ifs.user.resource.Gender.NOT_STATED;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class AssessorServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessorService assessorService = new AssessorServiceImpl();

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(assessorService, "webBaseUrl", "https://ifs-local-dev");
    }

    @Test
    public void registerAssessorByHash_callCorrectServicesAndHaveSuccessfulOutcome() throws Exception {
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

        RoleResource roleResource = newRoleResource().build();

        InnovationAreaResource innovationAreaResource = newInnovationAreaResource().build();

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource()
                .withEmail(email)
                .withInnovationArea(innovationAreaResource)
                .build();

        when(profileRepositoryMock.findOne(anyLong())).thenReturn(newProfile().build());
        when(innovationAreaMapperMock.mapToDomain(innovationAreaResource)).thenReturn(newInnovationArea().build());

        when(competitionInviteServiceMock.getInvite(hash)).thenReturn(serviceSuccess(competitionInviteResource));
        when(roleServiceMock.findByUserRoleType(ASSESSOR)).thenReturn(serviceSuccess(roleResource));

        UserResource createdUserResource = newUserResource().build();
        User createdUser = newUser()
                .withEmailAddress(email)
                .build();

        List<CompetitionParticipant> participantsForOtherInvites = Stream.generate(
                () -> Mockito.spy(new CompetitionParticipant())).limit(2).collect(Collectors.toList());

        when(registrationServiceMock.createUser(userRegistrationResource)).thenReturn(serviceSuccess(createdUserResource));

        when(registrationServiceMock.activateUser(createdUserResource.getId())).thenReturn(serviceSuccess());
        when(competitionInviteServiceMock.acceptInvite(hash, createdUserResource)).thenReturn(serviceSuccess());
        when(userRepositoryMock.findOne(createdUserResource.getId())).thenReturn(createdUser);
        when(competitionParticipantRepositoryMock.getByInviteEmail(email)).thenReturn(participantsForOtherInvites);

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userRegistrationResource);

        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionInviteServiceMock, roleServiceMock, registrationServiceMock,
                userRepositoryMock, competitionParticipantRepositoryMock, innovationAreaMapperMock, profileRepositoryMock);
        inOrder.verify(competitionInviteServiceMock).getInvite(hash);
        inOrder.verify(roleServiceMock).findByUserRoleType(ASSESSOR);
        inOrder.verify(registrationServiceMock).createUser(userRegistrationResource);
        inOrder.verify(registrationServiceMock).activateUser(createdUserResource.getId());
        inOrder.verify(userRepositoryMock).findOne(createdUserResource.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteEmail(email);
        inOrder.verify(competitionParticipantRepositoryMock).save(participantsForOtherInvites);
        inOrder.verify(profileRepositoryMock).findOne(anyLong());
        inOrder.verify(innovationAreaMapperMock).mapToDomain(innovationAreaResource);
        inOrder.verify(profileRepositoryMock).save(any(Profile.class));
        inOrder.verifyNoMoreInteractions();

        participantsForOtherInvites.forEach(competitionParticipant -> {
            verify(competitionParticipant).setUser(createdUser);
        });
    }

    @Test
    public void registerAssessorByHash_inviteDoesNotExistResultsInFailureAndSkippingUserRegistrationAndInviteAcceptance() throws Exception {
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

        ServiceResult<CompetitionInviteResource> inviteResult = serviceFailure(notFoundError(CompetitionInvite.class, hash));

        when(competitionInviteServiceMock.getInvite(hash)).thenReturn(inviteResult);

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userRegistrationResource);

        verify(competitionInviteServiceMock).getInvite(hash);
        verifyNoMoreInteractions(roleServiceMock);
        verifyNoMoreInteractions(registrationServiceMock);
        verifyNoMoreInteractions(competitionInviteServiceMock);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(CompetitionInvite.class, "inviteHashNotExists")));
    }

    @Test
    public void registerAssessorByHash_userValidationFailureResultsInFailureAndNotAcceptingInvite() throws Exception {
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

        RoleResource roleResource = newRoleResource().build();

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource()
                .withEmail("email@example.com")
                .build();

        when(competitionInviteServiceMock.getInvite(hash)).thenReturn(serviceSuccess(competitionInviteResource));
        when(roleServiceMock.findByUserRoleType(ASSESSOR)).thenReturn(serviceSuccess(roleResource));

        when(registrationServiceMock.createUser(userRegistrationResource)).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userRegistrationResource);

        InOrder inOrder = inOrder(competitionInviteServiceMock, roleServiceMock, registrationServiceMock);
        inOrder.verify(competitionInviteServiceMock).getInvite(hash);
        inOrder.verify(roleServiceMock).findByUserRoleType(ASSESSOR);
        inOrder.verify(registrationServiceMock).createUser(userRegistrationResource);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void getAssessorProfile() throws Exception {
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

        when(userRepositoryMock.findByIdAndRolesName(assessorId, ASSESSOR.getName())).thenReturn(user);
        when(profileRepositoryMock.findOne(profileId)).thenReturn(profile);
        when(userMapperMock.mapToResource(user.get())).thenReturn(userResource);
        when(assessorProfileMapperMock.mapToResource(profile)).thenReturn(profileResource);

        AssessorProfileResource expectedAssessorProfileResource = newAssessorProfileResource()
                .withUser(userResource)
                .withProfile(profileResource)
                .build();

        AssessorProfileResource actualAssessorProfileResource = assessorService.getAssessorProfile(assessorId).getSuccessObjectOrThrowException();

        assertEquals(expectedAssessorProfileResource, actualAssessorProfileResource);

        InOrder inOrder = inOrder(userRepositoryMock, profileRepositoryMock, userMapperMock, assessorProfileMapperMock);
        inOrder.verify(userRepositoryMock).findByIdAndRolesName(assessorId, ASSESSOR.getName());
        inOrder.verify(profileRepositoryMock).findOne(profileId);
        inOrder.verify(userMapperMock).mapToResource(user.get());
        inOrder.verify(assessorProfileMapperMock).mapToResource(profile);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void notifyAssessorsByCompetition() throws Exception {
        Long competitionId = 1L;

        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, State.CREATED);
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
                .withActivityState(activityState)
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
                new UserNotificationTarget(users.get(0)),
                new UserNotificationTarget(users.get(1))
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

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);
        when(assessmentRepositoryMock.findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId)).thenReturn(assessments);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(true);

        when(notificationSender.renderTemplates(expectedNotification1))
                .thenReturn(serviceSuccess(asMap(recipients.get(0), emailContents.get(0))));
        when(notificationSender.renderTemplates(expectedNotification2))
                .thenReturn(serviceSuccess(asMap(recipients.get(1), emailContents.get(1))));
        when(notificationSender.sendEmailWithContent(expectedNotification1, recipients.get(0), emailContents.get(0)))
                .thenReturn(serviceSuccess(emptyList()));
        when(notificationSender.sendEmailWithContent(expectedNotification2, recipients.get(1), emailContents.get(1)))
                .thenReturn(serviceSuccess(emptyList()));

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationSender);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        inOrder.verify(notificationSender).renderTemplates(expectedNotification1);
        inOrder.verify(notificationSender).sendEmailWithContent(expectedNotification1, recipients.get(0), emailContents.get(0));
        inOrder.verify(notificationSender).renderTemplates(expectedNotification2);
        inOrder.verify(notificationSender).sendEmailWithContent(expectedNotification2, recipients.get(1), emailContents.get(1));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getErrors().isEmpty());
    }

    @Test
    public void notifyAssessorsByCompetition_oneEmailPerUser() throws Exception {
        Long competitionId = 1L;

        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, State.CREATED);
        Competition competition = newCompetition()
                .withId(competitionId)
                .withName("Test Competition")
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().minusDays(1))
                .build();
        User user = newUser().build();

        List<Assessment> assessments = newAssessment()
                .withId(2L, 3L)
                .withActivityState(activityState)
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
        NotificationTarget recipient = new UserNotificationTarget(user);
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

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);
        when(assessmentRepositoryMock.findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId)).thenReturn(assessments);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(true);

        when(notificationSender.renderTemplates(expectedNotification))
                .thenReturn(serviceSuccess(asMap(recipient, emailContent)));
        when(notificationSender.sendEmailWithContent(expectedNotification, recipient, emailContent))
                .thenReturn(serviceSuccess(emptyList()));

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationSender);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        inOrder.verify(notificationSender).renderTemplates(expectedNotification);
        inOrder.verify(notificationSender).sendEmailWithContent(expectedNotification, recipient, emailContent);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getErrors().isEmpty());

    }

    @Test
    public void notifyAssessorsByCompetition_competitionNotFound() throws Exception {
        long competitionId = 1L;

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(null);

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        verify(competitionRepositoryMock).findOne(competitionId);
        verifyNoMoreInteractions(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationSender);

        assertTrue(serviceResult.isFailure());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void notifyAssessorsByCompetition_oneTransitionFails() throws Exception {
        Long competitionId = 1L;

        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, State.CREATED);

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();
        List<Assessment> assessments = newAssessment()
                .withActivityState(activityState)
                .withId(2L, 3L)
                .build(2);

        when(assessmentRepositoryMock.findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId))
                .thenReturn(assessments);
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(false);

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationSender);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void notifyAssessorsByCompetition_allTransitionsFail() throws Exception {
        Long competitionId = 1L;

        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, State.CREATED);

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();
        List<Assessment> assessments = newAssessment()
                .withActivityState(activityState)
                .withId(2L, 3L)
                .build(2);

        when(assessmentRepositoryMock.findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId))
                .thenReturn(assessments);
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(false);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(false);

        ServiceResult<Void> serviceResult = assessorService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationSender);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(2, serviceResult.getErrors().size());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(1).getErrorKey());
    }
}
