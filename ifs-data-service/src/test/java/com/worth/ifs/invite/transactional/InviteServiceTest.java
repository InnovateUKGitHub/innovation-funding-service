package com.worth.ifs.invite.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.builder.ApplicationBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.builder.CompetitionBuilder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.builder.InviteBuilder;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class InviteServiceTest extends BaseUnitTestMocksTest {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    NotificationService notificationService;

    @InjectMocks
    private InviteServiceImpl inviteService = new InviteServiceImpl();
    private LocalValidatorFactoryBean localValidatorFactory;


    @Before
    public void setup() {
        when(inviteRepositoryMock.save(any(Invite.class))).thenReturn(new Invite());
        ServiceResult<Notification> result = serviceSuccess(new Notification());
        when(notificationService.sendNotification(any(), eq(NotificationMedium.EMAIL))).thenReturn(result);

        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }


    @Test
    public void testValidatorEmpty() {
        Application application = ApplicationBuilder.newApplication().withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant", null);
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisation(leadOrganisation).build();
        application.setProcessRoles(asList(processRole1));

        Invite invite = InviteBuilder.newInvite().withApplication(application).build();
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        localValidatorFactory.validate(invite, errors);

        errors.getFieldErrors().forEach(f -> log.debug(String.format("Before: Field error: %s %s => %s =>  %s", f.getCode(), f.getObjectName(), f.getField(), f.getDefaultMessage())));
        assertEquals(2, errors.getErrorCount());
    }

    @Test
    public void testValidatorEmail() {
        Application application = ApplicationBuilder.newApplication().withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant", null);
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisation(leadOrganisation).build();
        application.setProcessRoles(asList(processRole1));

        Invite invite = InviteBuilder.newInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("email-invalid");
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        localValidatorFactory.validate(invite, errors);

        errors.getFieldErrors().forEach(f -> log.debug(String.format("Before: Field error: %s %s => %s =>  %s", f.getCode(), f.getObjectName(), f.getField(), f.getDefaultMessage())));
        assertEquals(1, errors.getErrorCount());
    }

    @Test
    public void testInviteCollaborators() throws Exception {
        Competition competition = CompetitionBuilder.newCompetition().build();
        Application application = ApplicationBuilder.newApplication().withCompetition(competition).withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant", null);
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisation(leadOrganisation).build();
        application.setProcessRoles(asList(processRole1));

        Invite invite = InviteBuilder.newInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nico@test.nl");

        List<ServiceResult<Notification>> results = inviteService.inviteCollaborators("http:localhost:189809", Arrays.asList(invite));
        assertEquals(1, results.size());
        assertTrue(results.get(0).isSuccess());
    }

    @Test
    public void testInviteCollaboratorsInvalid() throws Exception {
        Application application = ApplicationBuilder.newApplication().withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant", null);
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisation(leadOrganisation).build();
        application.setProcessRoles(asList(processRole1));

        Invite invite = InviteBuilder.newInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nicotest.nl");

        List<ServiceResult<Notification>> results = inviteService.inviteCollaborators("http:localhost:189809", Arrays.asList(invite));
        assertEquals(1, results.size());
        assertTrue(results.get(0).isFailure());
    }
}