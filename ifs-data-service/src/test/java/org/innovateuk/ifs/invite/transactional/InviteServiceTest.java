package org.innovateuk.ifs.invite.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.ApplicationInviteMapper;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.builder.InviteResourceBuilder.newInviteResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class InviteServiceTest extends BaseUnitTestMocksTest {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    NotificationService notificationService;
    @Mock
    ApplicationInviteMapper applicationInviteMapper;
    @Mock
    InviteOrganisationMapper inviteOrganisationMapper;

    @InjectMocks
    private InviteServiceImpl inviteService = new InviteServiceImpl();
    private LocalValidatorFactoryBean localValidatorFactory;


    @Before
    public void setup() {
        when(applicationInviteRepositoryMock.save(any(ApplicationInvite.class))).thenReturn(new ApplicationInvite());
        ServiceResult<Void> result = serviceSuccess();
        when(notificationService.sendNotification(any(), eq(NotificationMedium.EMAIL))).thenReturn(result);

        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();

        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());
    }


    @Test
    public void testValidatorEmpty() {
        Application application = newApplication().withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant");
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisationId(leadOrganisation.getId()).build();
        application.setProcessRoles(asList(processRole1));

        ApplicationInvite invite = newInvite().withApplication(application).build();
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        localValidatorFactory.validate(invite, errors);

        errors.getFieldErrors().forEach(f -> log.debug(String.format("Before: Field error: %s %s => %s =>  %s", f.getCode(), f.getObjectName(), f.getField(), f.getDefaultMessage())));
        assertEquals(2, errors.getErrorCount());
    }

    @Test
    public void testValidatorEmail() {
        Application application = newApplication().withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant");
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisationId(leadOrganisation.getId()).build();
        application.setProcessRoles(asList(processRole1));

        ApplicationInvite invite = newInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("email-invalid");
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        localValidatorFactory.validate(invite, errors);

        errors.getFieldErrors().forEach(f -> log.debug(String.format("Before: Field error: %s %s => %s =>  %s", f.getCode(), f.getObjectName(), f.getField(), f.getDefaultMessage())));
        assertEquals(1, errors.getErrorCount());
    }

    @Test
    public void testInviteCollaborators() throws Exception {
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant");
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();

        Organisation leadOrganisation = newOrganisation().
                withId(43L).
                withName("Empire Ltd").
                build();
        when(organisationRepositoryMock.findOne(leadOrganisation.getId())).thenReturn(leadOrganisation);

        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisationId(leadOrganisation.getId()).build();
        application.setProcessRoles(asList(processRole1));

        ApplicationInvite invite = newInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nico@test.nl");
        InviteOrganisation inviteOrganisation = new InviteOrganisation("SomeOrg", null, Arrays.asList(invite));
        invite.setInviteOrganisation(inviteOrganisation);

        List<ServiceResult<Void>> results = inviteService.inviteCollaborators("http:localhost:189809", Arrays.asList(invite));
        assertEquals(1, results.size());
        assertTrue(results.get(0).isSuccess());
    }

    @Test
    public void testInviteCollaboratorsInvalid() throws Exception {
        Application application = newApplication().withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant");
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisationId(leadOrganisation.getId()).build();
        application.setProcessRoles(asList(processRole1));

        ApplicationInvite invite = newInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nicotest.nl");

        List<ServiceResult<Void>> results = inviteService.inviteCollaborators("http:localhost:189809", Arrays.asList(invite));
        assertEquals(1, results.size());
        assertTrue(results.get(0).isFailure());
    }

    @Test
    public void testCreateApplicationInvites() {

        List<ApplicationInviteResource> inviteResources = newInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail", "testemail1", "testemail2", "testemail3", "testemail4")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .withOrganisationName("new organisation")
                .build();

        InviteOrganisation saveInviteOrganisationExpectations = argThat(lambdaMatches(inviteOrganisation -> {
            assertEquals("new organisation", inviteOrganisation.getOrganisationName());
            return true;
        }));

        when(inviteOrganisationRepositoryMock.save(saveInviteOrganisationExpectations)).thenReturn(saveInviteOrganisationExpectations);
        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList());

        when(inviteOrganisationRepositoryMock.findAll(isA(List.class))).thenReturn(newInviteOrganisation().build(inviteResources.size()));

        List<ApplicationInvite> savedInvites = newInvite().build(5);

        List<ApplicationInvite> saveInvitesExpectations = argThat(lambdaMatches(invites -> {
            assertEquals(5, invites.size());
            assertEquals("testname", invites.get(0).getName());
            return true;
        }));


        when(applicationInviteRepositoryMock.save(saveInvitesExpectations)).thenReturn(savedInvites);
        when(applicationRepositoryMock.findOne(isA(Long.class))).thenReturn(newApplication().withId(1L).build());

        ServiceResult<InviteResultsResource> result = inviteService.createApplicationInvites(inviteOrganisationResource);
        assertTrue(result.isSuccess());

        verify(inviteOrganisationRepositoryMock).save(isA(InviteOrganisation.class));
        verify(applicationInviteRepositoryMock).save(isA(List.class));
    }

    @Test
    public void testCreateApplicationInvitesWithInvalidInvitesNoApplicationId() {

        List<ApplicationInviteResource> inviteResources = newInviteResource()
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        assertInvalidInvites(inviteResources);
    }

    @Test
    public void testCreateApplicationInvitesWithInvalidInvitesNoEmailAddress() {

        List<ApplicationInviteResource> inviteResources = newInviteResource()
                .withId(1L)
                .withName("testname")
                .build(5);

        assertInvalidInvites(inviteResources);
    }

    @Test
    public void testCreateApplicationInvitesWithInvalidInvitesNoName() {

        List<ApplicationInviteResource> inviteResources = newInviteResource()
                .withId(1L)
                .withEmail("testemail")
                .build(5);

        assertInvalidInvites(inviteResources);
    }

    @Test
    public void testCreateApplicationInvitesWithInvalidOrganisationInviteNoOrganisationName() {

        List<ApplicationInviteResource> inviteResources = newInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList());
        when(inviteOrganisationRepositoryMock.findAll(isA(List.class))).thenReturn(newInviteOrganisation().build(inviteResources.size()));

        when(applicationRepositoryMock.findOne(isA(Long.class))).thenReturn(newApplication().withId(1L).build());

        ServiceResult<InviteResultsResource> result = inviteService.createApplicationInvites(inviteOrganisationResource);
        assertTrue(result.isFailure());
        
        verify(inviteOrganisationRepositoryMock, never()).save(isA(InviteOrganisation.class));
        verify(applicationInviteRepositoryMock, never()).save(isA(List.class));
    }

    @Ignore
    @Test
    public void testGetInviteOrganisationByHash() {

        Competition competition = newCompetition().build();
        Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().build();

        ProcessRole leadApplicantProcessRole = newProcessRole().withUser(user).withRole(leadApplicantRole).withOrganisationId(organisation.getId()).build();
        Application application = newApplication().withCompetition(competition).withProcessRoles(leadApplicantProcessRole).build();
        InviteOrganisation inviteOrganisation = newInviteOrganisation().build();
        ApplicationInvite invite = newInvite().withInviteOrganisation(inviteOrganisation).withApplication(application).build();
        ApplicationInviteResource inviteResource = newInviteResource().withOrganisation(1L).withApplication(application.getId()).build();


        when(applicationInviteRepositoryMock.getByHash("an organisation hash")).thenReturn(invite);

        ServiceResult<InviteOrganisationResource> organisationInvite = inviteService.getInviteOrganisationByHash("an organisation hash");
        assertTrue(organisationInvite.isSuccess());


        List<ApplicationInviteResource> expectedInvites = singletonList(inviteResource);

        InviteOrganisationResource expectedInviteOrganisation = newInviteOrganisationResource().
                withId(inviteOrganisation.getId()).
                withInviteResources(expectedInvites).
                build();

        assertEquals(expectedInviteOrganisation, organisationInvite.getSuccessObject());
    }

    @Test
    public void testGetInviteOrganisationByHashButInviteOrganisationNotFound() {

        when(applicationInviteRepositoryMock.getByHash("an organisation hash")).thenReturn(null);

        ServiceResult<InviteOrganisationResource> organisationInvite = inviteService.getInviteOrganisationByHash("an organisation hash");
        assertTrue(organisationInvite.isFailure());
        assertTrue(organisationInvite.getFailure().is(notFoundError(ApplicationInvite.class, "an organisation hash")));
    }

    @Test
    public void testRemoveApplicationInvite() {
        User user = newUser().build();
        Application application = newApplication().build();
        ApplicationInvite applicationInvite = newApplicationInvite()
                .withId(24521L)
                .withUser(user)
                .withApplication(application)
                .withInviteOrganisation(
                        newInviteOrganisation()
                                .withInvites(newInvite().build(2))
                                .build()
                )
                .build();

        List<ProcessRole> processRoles = newProcessRole().build(3);
        ProcessRole manyMembersOfOrg = processRoles.get(0);
        manyMembersOfOrg.setOrganisationId(1L);
        ProcessRole otherMemberOfOrg = newProcessRole().build();
        ProcessRole onlyMemberOfOrg = processRoles.get(1);
        onlyMemberOfOrg.setOrganisationId(2L);
        ProcessRole withoutQuestionStatus = processRoles.get(2);
        withoutQuestionStatus.setOrganisationId(3L);
        QuestionStatus singleStatusQuestion = new QuestionStatus(newQuestion().withMultipleStatuses(false).build(), null, onlyMemberOfOrg, true);
        QuestionStatus multipleStatusQuestionLastMember = new QuestionStatus(newQuestion().withMultipleStatuses(true).build(), null, onlyMemberOfOrg, true);
        QuestionStatus multipleStatusQuestionManyMember = new QuestionStatus(newQuestion().withMultipleStatuses(true).build(), null, manyMembersOfOrg, true);

        when(applicationInviteMapper.mapIdToDomain(applicationInvite.getId())).thenReturn(applicationInvite);
        when(processRoleRepositoryMock.findByUserAndApplicationId(any(User.class), any(Long.class))).thenReturn(processRoles);
        when(questionStatusRepositoryMock.findByApplicationIdAndMarkedAsCompleteById(any(Long.class), eq(manyMembersOfOrg.getId()))).thenReturn(newArrayList(multipleStatusQuestionManyMember));
        when(questionStatusRepositoryMock.findByApplicationIdAndMarkedAsCompleteById(any(Long.class), eq(onlyMemberOfOrg.getId()))).thenReturn(newArrayList(singleStatusQuestion, multipleStatusQuestionLastMember));
        when(questionStatusRepositoryMock.findByApplicationIdAndMarkedAsCompleteById(any(Long.class), eq(withoutQuestionStatus.getId()))).thenReturn(newArrayList());
        when(processRoleRepositoryMock.findByApplicationIdAndOrganisationId(any(Long.class), eq(manyMembersOfOrg.getOrganisationId()))).thenReturn(newArrayList(manyMembersOfOrg, otherMemberOfOrg));
        when(processRoleRepositoryMock.findByApplicationIdAndOrganisationId(any(Long.class), eq(onlyMemberOfOrg.getOrganisationId()))).thenReturn(newArrayList(onlyMemberOfOrg));


        ServiceResult<Void> applicationInviteResult = inviteService.removeApplicationInvite(applicationInvite.getId());
        assertTrue(applicationInviteResult.isSuccess());

        //Single status should be completed by lead
        assertThat(singleStatusQuestion.getMarkedAsCompleteBy(), equalTo(applicationInvite.getTarget().getLeadApplicantProcessRole()));

        //Verify last question status was deleted.
        verify(questionStatusRepositoryMock).delete(multipleStatusQuestionLastMember);

        //Verify many member question status is completed by other member.
        assertThat(multipleStatusQuestionManyMember.getMarkedAsCompleteBy(), equalTo(otherMemberOfOrg));


        ServiceResult<Void> applicationInviteResultFailure = inviteService.removeApplicationInvite(11L);
        assertTrue(applicationInviteResultFailure.isFailure());
    }


    private void assertInvalidInvites(List<ApplicationInviteResource> inviteResources) {
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .withOrganisationName("new organisation")
                .build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList(inviteOrganisationResource));
        when(applicationRepositoryMock.findOne(null)).thenReturn(newApplication().withId(1L).build());
        when(inviteOrganisationRepositoryMock.findAll(isA(List.class))).thenReturn(newInviteOrganisation().build(1));

        ServiceResult<InviteResultsResource> result = inviteService.createApplicationInvites(inviteOrganisationResource);
        assertTrue(result.isFailure());

        verify(inviteOrganisationRepositoryMock, never()).save(isA(InviteOrganisation.class));
        verify(applicationInviteRepositoryMock, never()).save(isA(List.class));
    }
}
