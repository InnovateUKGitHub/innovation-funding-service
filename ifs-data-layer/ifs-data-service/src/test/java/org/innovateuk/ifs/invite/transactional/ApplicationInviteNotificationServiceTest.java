package org.innovateuk.ifs.invite.transactional;

import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.Title;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.transactional.ApplicationInviteServiceImpl.Notifications.INVITE_COLLABORATOR;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.service.ServiceFailureTestHelper.assertThatServiceFailureIs;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(org.mockito.junit.MockitoJUnitRunner.Silent.class)
public class ApplicationInviteNotificationServiceTest {

    @Mock
    private ApplicationInviteRepository applicationInviteRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;

    @Mock
    private NotificationService notificationServiceMock;

    @InjectMocks
    private ApplicationInviteNotificationService inviteService = new ApplicationInviteNotificationService();

    private LocalValidatorFactoryBean localValidatorFactory;

    @Before
    public void setup() {

        when(applicationInviteRepositoryMock.save(any(ApplicationInvite.class))).thenReturn(new ApplicationInvite());

        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();

        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());
    }

    @Test
    public void validatorEmpty() {
        Application application = newApplication().withName("AppName").build();
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole()
                .with(id(1L))
                .withApplication(application)
                .withUser(leadApplicant)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisationId(leadOrganisation.getId())
                .build();
        application.setProcessRoles(singletonList(processRole1));

        ApplicationInvite invite = newApplicationInvite().withApplication(application).build();
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        localValidatorFactory.validate(invite, errors);

        assertThat(errors.getErrorCount()).isEqualTo(2);
    }

    @Test
    public void validatorEmail() {
        Application application = newApplication().withName("AppName").build();
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L))
                .withApplication(application)
                .withUser(leadApplicant)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisationId(leadOrganisation.getId())
                .build();
        application.setProcessRoles(singletonList(processRole1));

        ApplicationInvite invite = newApplicationInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("email-invalid");
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        localValidatorFactory.validate(invite, errors);

        assertThat(errors.getErrorCount()).isOne();
    }

    @Test
    public void inviteCollaborators() {
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).withName("AppName").build();

        User leadApplicant = newUser()
                .withTitle(Title.Dr)
                .withEmailAddress("Email@email.com")
                .withFirstName("Nico").build();

        Organisation leadOrganisation = newOrganisation()
                .withId(43L)
                .withName("Empire Ltd")
                .build();

        ProcessRole processRole1 = newProcessRole()
                .with(id(1L))
                .withApplication(application)
                .withUser(leadApplicant)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisationId(leadOrganisation.getId())
                .build();
        application.setProcessRoles(singletonList(processRole1));

        ApplicationInvite invite = newApplicationInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nico@test.nl");
        invite.setHash("hash234");
        InviteOrganisation inviteOrganisation =
                new InviteOrganisation("SomeOrg", null, singletonList(invite));
        invite.setInviteOrganisation(inviteOrganisation);

        when(organisationRepositoryMock.findById(leadOrganisation.getId())).thenReturn(Optional.of(leadOrganisation));
        when(notificationServiceMock.sendNotificationWithFlush(isA(Notification.class), eq(NotificationMedium.EMAIL))).thenReturn(serviceSuccess());
        when(organisationRepositoryMock.findById(leadOrganisation.getId())).thenReturn(Optional.of(leadOrganisation));
        when(notificationServiceMock.sendNotificationWithFlush(createNotificationExpectations(invite, application,
                competition, leadOrganisation), eq(NotificationMedium.EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> results = inviteService.inviteCollaborators(singletonList(invite));

        assertThat(results.isSuccess()).isTrue();
        assertFalse("hash234".equals(invite.getHash()));

        verify(organisationRepositoryMock).findById(leadOrganisation.getId());
        verify(notificationServiceMock).sendNotificationWithFlush(isA(Notification.class), eq(NotificationMedium.EMAIL));
        verify(organisationRepositoryMock).findById(leadOrganisation.getId());
        verify(notificationServiceMock).sendNotificationWithFlush(createNotificationExpectations(invite, application,
                competition, leadOrganisation), eq(NotificationMedium.EMAIL));
    }

    @Test
    public void inviteCollaboratorsInvalid() {
        Application application = newApplication().withName("AppName").build();

        User leadApplicant = newUser()
                .withTitle(Title.Dr)
                .withEmailAddress("Email@email.com")
                .withFirstName("Nico").build();

        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole()
                .with(id(1L))
                .withApplication(application)
                .withUser(leadApplicant)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisationId(leadOrganisation.getId())
                .build();
        application.setProcessRoles(singletonList(processRole1));

        ApplicationInvite invite = newApplicationInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nicotest.nl");

        ServiceResult<Void> results =
                inviteService.inviteCollaborators(singletonList(invite));
        assertThat(results.isFailure()).isTrue();
    }

    @Test
    public void inviteCollaboratorsEmailFailsToSend() {
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).withName("AppName").build();

        User leadApplicant = newUser()
                .withTitle(Title.Dr)
                .withEmailAddress("Email@email.com")
                .withFirstName("Nico").build();

        Organisation leadOrganisation = newOrganisation()
                .withId(43L)
                .withName("Empire Ltd")
                .build();

        ProcessRole processRole1 = newProcessRole()
                .with(id(1L))
                .withApplication(application)
                .withUser(leadApplicant)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisationId(leadOrganisation.getId())
                .build();
        application.setProcessRoles(singletonList(processRole1));

        ApplicationInvite invite = newApplicationInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nico@test.nl");
        InviteOrganisation inviteOrganisation =
                new InviteOrganisation("SomeOrg", null, singletonList(invite));
        invite.setInviteOrganisation(inviteOrganisation);

        when(organisationRepositoryMock.findById(leadOrganisation.getId())).thenReturn(Optional.of(leadOrganisation));
        when(notificationServiceMock.sendNotificationWithFlush(isA(Notification.class), eq(NotificationMedium.EMAIL))).thenReturn(serviceFailure(forbiddenError()));

        ServiceResult<Void> results = inviteService.inviteCollaborators(singletonList(invite));

        assertThatServiceFailureIs(results, forbiddenError());

        verify(organisationRepositoryMock).findById(leadOrganisation.getId());
        verify(notificationServiceMock).sendNotificationWithFlush(isA(Notification.class), eq(NotificationMedium.EMAIL));
    }

    @Test
    public void resendCollaboratorInvite() {
        Competition competition = newCompetition().build();
        Application application = newApplication()
                .withCompetition(competition)
                .withName("application name")
                .build();

        User leadApplicant = newUser()
                .withTitle(Title.Mr)
                .withEmailAddress("email.address")
                .withFirstName("Kieran").build();

        Organisation leadOrganisation = newOrganisation()
                .withId(43L)
                .withName("Imaginative organisation name")
                .build();

        ProcessRole processRole1 = newProcessRole()
                .with(id(1L))
                .withApplication(application)
                .withUser(leadApplicant)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisationId(leadOrganisation.getId())
                .build();
        application.setProcessRoles(singletonList(processRole1));

        ApplicationInvite invite = newApplicationInvite().withApplication(application).build();
        invite.setName("Steve Smith");
        invite.setEmail("valid@email.com");
        InviteOrganisation inviteOrganisation =
                new InviteOrganisation("Another imaginative organisation name", null, singletonList(invite));
        invite.setInviteOrganisation(inviteOrganisation);
        invite.setHash("hash123");

        when(organisationRepositoryMock.findById(leadOrganisation.getId())).thenReturn(Optional.of(leadOrganisation));
        when(notificationServiceMock.sendNotificationWithFlush(isA(Notification.class), eq(NotificationMedium.EMAIL)))
                .thenReturn(serviceSuccess());
        when(notificationServiceMock.sendNotificationWithFlush(createNotificationExpectations(invite,
                                                                                              application,
                                                                                              competition,
                                                                                              leadOrganisation),
                                                               eq(NotificationMedium.EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = inviteService.resendCollaboratorInvite(invite);

        assertTrue(result.isSuccess());
        assertEquals("hash123", invite.getHash());

        verify(organisationRepositoryMock).findById(leadOrganisation.getId());
        verify(notificationServiceMock).sendNotificationWithFlush(isA(Notification.class), eq(NotificationMedium.EMAIL));
        verify(organisationRepositoryMock).findById(leadOrganisation.getId());
        verify(notificationServiceMock).sendNotificationWithFlush(createNotificationExpectations(invite,
                                                                                                 application,
                                                                                                 competition,
                                                                                                 leadOrganisation),
                                                                  eq(NotificationMedium.EMAIL));

    }

    private Notification createNotificationExpectations(ApplicationInvite invite, Application application,
                                                        Competition competition, Organisation leadOrganisation) {

        Map<String, Object> expectedNotificationArguments = asMap(
                "leadApplicant", application.getLeadApplicant().getName(),
                "sentByName", loggedInUserSupplierMock.get().getName(),
                "leadOrganisation", leadOrganisation.getName(),
                "competitionName", competition.getName(),
                "inviteOrganisationName", invite.getInviteOrganisation().getOrganisationName(),
                "leadApplicantTitle", application.getLeadApplicant().getTitle(),
                "participationAction", "collaborate",
                "applicationId", application.getId(),
                "competitionUrl", format("null/competition/%s/overview", competition.getId()),
                "applicationName", application.getName()
        );

        return createLambdaMatcher(notification -> {
            assertEquals(singletonList(new UserNotificationTarget(invite.getName(), invite.getEmail())), notification.getTo());
            assertEquals(INVITE_COLLABORATOR, notification.getMessageKey());
            assertThat(notification.getGlobalArguments()).containsKey("inviteUrl");
            assertThat(notification.getGlobalArguments()).contains(expectedNotificationArguments.entrySet().toArray(new Map.Entry[0]));
            return true;
        });
    }
}
