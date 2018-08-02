package org.innovateuk.ifs.invite.transactional;

import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.service.ServiceFailureTestHelper.assertThatServiceFailureIs;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite().withApplication(application).build();
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

        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("email-invalid");
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        localValidatorFactory.validate(invite, errors);

        assertThat(errors.getErrorCount()).isOne();
    }

    @Test
    public void inviteCollaborators() throws Exception {
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).withName("AppName").build();
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();

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

        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nico@test.nl");
        InviteOrganisation inviteOrganisation =
                new InviteOrganisation("SomeOrg", null, singletonList(invite));
        invite.setInviteOrganisation(inviteOrganisation);

        when(organisationRepositoryMock.findById(leadOrganisation.getId())).thenReturn(Optional.of(leadOrganisation));
        when(notificationServiceMock.sendNotificationWithFlush(isA(Notification.class), eq(NotificationMedium.EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> results = inviteService.inviteCollaborators(singletonList(invite));

        assertThat(results.isSuccess()).isTrue();

        verify(organisationRepositoryMock).findById(leadOrganisation.getId());
        verify(notificationServiceMock).sendNotificationWithFlush(isA(Notification.class), eq(NotificationMedium.EMAIL));
    }

    @Test
    public void inviteCollaboratorsInvalid() throws Exception {
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

        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nicotest.nl");

        ServiceResult<Void> results =
                inviteService.inviteCollaborators(singletonList(invite));
        assertThat(results.isFailure()).isTrue();
    }

    @Test
    public void inviteCollaboratorsEmailFailsToSend() throws Exception {
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).withName("AppName").build();
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();

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

        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite().withApplication(application).build();
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
}
