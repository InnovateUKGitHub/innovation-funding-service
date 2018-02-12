package org.innovateuk.ifs.invite.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.ApplicationInviteMapper;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
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

@RunWith(MockitoJUnitRunner.class)
public class InviteServiceImplTest {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    private NotificationService notificationService;

    @Mock
    private ApplicationInviteMapper applicationInviteMapper;

    @Mock
    private InviteOrganisationMapper inviteOrganisationMapper;

    @Mock
    private QuestionReassignmentService questionReassignmentServiceMock;

    @Mock
    private ApplicationService applicationServiceMock;

    @Mock
    private ApplicationInviteRepository applicationInviteRepositoryMock;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepositoryMock;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

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
    public void findOneByHash() {
        String hash = "123abc";
        ApplicationInvite applicationInvite = newApplicationInvite().build();

        when(applicationInviteRepositoryMock.getByHash(hash)).thenReturn(applicationInvite);

        ServiceResult<ApplicationInvite> result = inviteService.findOneByHash(hash);

        assertEquals(applicationInvite, result.getSuccess());
    }

    @Test
    public void validatorEmpty() {
        Application application = newApplication().withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant");
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisationId(leadOrganisation.getId()).build();
        application.setProcessRoles(asList(processRole1));

        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite().withApplication(application).build();
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        localValidatorFactory.validate(invite, errors);

        errors.getFieldErrors().forEach(f -> log.debug(String.format("Before: Field error: %s %s => %s =>  %s", f.getCode(), f.getObjectName(), f.getField(), f.getDefaultMessage())));
        assertEquals(2, errors.getErrorCount());
    }

    @Test
    public void validatorEmail() {
        Application application = newApplication().withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant");
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisationId(leadOrganisation.getId()).build();
        application.setProcessRoles(asList(processRole1));

        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("email-invalid");
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        localValidatorFactory.validate(invite, errors);

        errors.getFieldErrors().forEach(f -> log.debug(String.format("Before: Field error: %s %s => %s =>  %s", f.getCode(), f.getObjectName(), f.getField(), f.getDefaultMessage())));
        assertEquals(1, errors.getErrorCount());
    }

    @Test
    public void inviteCollaborators() throws Exception {
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

        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nico@test.nl");
        InviteOrganisation inviteOrganisation = new InviteOrganisation("SomeOrg", null, Arrays.asList(invite));
        invite.setInviteOrganisation(inviteOrganisation);

        List<ServiceResult<Void>> results = inviteService.inviteCollaborators("http:localhost:189809", Arrays.asList(invite));
        assertEquals(1, results.size());
        assertTrue(results.get(0).isSuccess());
    }

    @Test
    public void inviteCollaboratorsInvalid() throws Exception {
        Application application = newApplication().withName("AppName").build();
        Role role1 = new Role(1L, "leadapplicant");
        User leadApplicant = newUser().withEmailAddress("Email@email.com").withFirstName("Nico").build();
        Organisation leadOrganisation = newOrganisation().withName("Empire Ltd").build();
        ProcessRole processRole1 = newProcessRole().with(id(1L)).withApplication(application).withUser(leadApplicant).withRole(role1).withOrganisationId(leadOrganisation.getId()).build();
        application.setProcessRoles(asList(processRole1));

        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite().withApplication(application).build();
        invite.setName("Nico");
        invite.setEmail("nicotest.nl");

        List<ServiceResult<Void>> results = inviteService.inviteCollaborators("http:localhost:189809", Arrays.asList(invite));
        assertEquals(1, results.size());
        assertTrue(results.get(0).isFailure());
    }

    @Test
    public void createApplicationInvites() {
        Long applicationId = 1L;

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
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

        List<ApplicationInvite> savedInvites = ApplicationInviteBuilder.newApplicationInvite().build(5);

        List<ApplicationInvite> saveInvitesExpectations = argThat(lambdaMatches(invites -> {
            assertEquals(5, invites.size());
            assertEquals("testname", invites.get(0).getName());
            return true;
        }));


        when(applicationInviteRepositoryMock.save(saveInvitesExpectations)).thenReturn(savedInvites);
        when(applicationRepositoryMock.findOne(isA(Long.class))).thenReturn(newApplication().withId(1L).build());

        ServiceResult<InviteResultsResource> result = inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));
        assertTrue(result.isSuccess());

        verify(inviteOrganisationRepositoryMock).save(isA(InviteOrganisation.class));
        verify(applicationInviteRepositoryMock, times(5)).save(isA(ApplicationInvite.class));
    }

    @Test
    public void createApplicationInvitesWithInvalidInvitesNoApplicationId() {

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        assertInvalidInvites(inviteResources);
    }

    @Test
    public void createApplicationInvitesWithInvalidInvitesNoEmailAddress() {

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withId(1L)
                .withName("testname")
                .build(5);

        assertInvalidInvites(inviteResources);
    }

    @Test
    public void createApplicationInvitesWithInvalidInvitesNoName() {

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withId(1L)
                .withEmail("testemail")
                .build(5);

        assertInvalidInvites(inviteResources);
    }

    @Test
    public void createApplicationInvitesWithInvalidOrganisationInviteNoOrganisationName() {
        Long applicationId = 1L;

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
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

        ServiceResult<InviteResultsResource> result = inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));
        assertTrue(result.isFailure());

        verify(inviteOrganisationRepositoryMock, never()).save(isA(InviteOrganisation.class));
        verify(applicationInviteRepositoryMock, never()).save(isA(List.class));
    }

    @Test
    public void createApplicationInvites_inviteOrganisationResourceHasNoOrganisationResultsInNewInviteOrganisationWithoutOrganisationId() {
        Long applicationId = 1L;

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(1);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisationName("An invite organisation with an organisation")
                .withInviteResources(inviteResources)
                .build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList());
        when(inviteOrganisationRepositoryMock.findAll(isA(List.class))).thenReturn(newInviteOrganisation().build(inviteResources.size()));
        when(applicationRepositoryMock.findOne(isA(Long.class))).thenReturn(newApplication().withId(1L).build());

        ServiceResult<InviteResultsResource> result = inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));

        assertTrue(result.isSuccess());

        ArgumentCaptor<InviteOrganisation> argument = ArgumentCaptor.forClass(InviteOrganisation.class);
        verify(inviteOrganisationRepositoryMock, times(1)).save(argument.capture());
        assertEquals(inviteOrganisationResource.getOrganisationName(), argument.getValue().getOrganisationName());
        assertNull(argument.getValue().getOrganisation());

        verify(applicationInviteRepositoryMock, times(1)).save(isA(ApplicationInvite.class));
    }

    @Test
    public void createApplicationInvites_inviteOrganisationResourceHasOrganisationButNoInviteOrganisationExistsResultsInNewInviteOrganisationWithOrganisationId() {
        Long applicationId = 1L;

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(1);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisationName("An invite organisation with an organisation")
                .withOrganisation(2L)
                .withInviteResources(inviteResources)
                .build();

        Organisation organisation = newOrganisation().withId(3L).build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList());
        when(inviteOrganisationRepositoryMock.findAll(isA(List.class))).thenReturn(newInviteOrganisation().build(inviteResources.size()));
        when(organisationRepositoryMock.findOne(inviteOrganisationResource.getOrganisation())).thenReturn(organisation);
        when(applicationRepositoryMock.findOne(isA(Long.class))).thenReturn(newApplication().withId(1L).build());

        ServiceResult<InviteResultsResource> result = inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));

        assertTrue(result.isSuccess());

        ArgumentCaptor<InviteOrganisation> argument = ArgumentCaptor.forClass(InviteOrganisation.class);
        verify(inviteOrganisationRepositoryMock, times(1)).save(argument.capture());
        assertEquals(organisation.getId(), argument.getValue().getOrganisation().getId());
        assertEquals(inviteOrganisationResource.getOrganisationName(), argument.getValue().getOrganisationName());

        verify(applicationInviteRepositoryMock, times(1)).save(isA(ApplicationInvite.class));

    }

    @Test
    public void createApplicationInvites_inviteOrganisationResourceHasOrganisationAndInviteOrganisationExistsResultsInExistingInviteOrganisationWithOrganisationId() {
        Long applicationId = 1L;

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(1);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisationName("An invite organisation with an organisation")
                .withOrganisation(3L)
                .withInviteResources(inviteResources)
                .build();

        Organisation organisation = newOrganisation().withName("Already existing organisation name").withId(3L).build();
        InviteOrganisation inviteOrganisation = newInviteOrganisation().withOrganisationName("Already existing invite organisation name").withOrganisation(organisation).build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList());
        when(inviteOrganisationRepositoryMock.findAll(isA(List.class))).thenReturn(newInviteOrganisation().build(inviteResources.size()));
        when(organisationRepositoryMock.findOne(inviteOrganisationResource.getOrganisation())).thenReturn(organisation);
        when(inviteOrganisationRepositoryMock.findOneByOrganisationIdAndInvitesApplicationId(inviteOrganisation.getOrganisation().getId(), applicationId)).thenReturn(inviteOrganisation);

        when(applicationRepositoryMock.findOne(isA(Long.class))).thenReturn(newApplication().withId(1L).build());

        ServiceResult<InviteResultsResource> result = inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));

        assertTrue(result.isSuccess());

        ArgumentCaptor<InviteOrganisation> argument = ArgumentCaptor.forClass(InviteOrganisation.class);
        verify(inviteOrganisationRepositoryMock, times(1)).save(argument.capture());
        assertEquals(inviteOrganisation.getOrganisation().getName(), argument.getValue().getOrganisation().getName());
        assertEquals(inviteOrganisation.getOrganisationName(), argument.getValue().getOrganisationName());

        verify(applicationInviteRepositoryMock, times(1)).save(isA(ApplicationInvite.class));
    }

    @Test
    public void getInviteOrganisationByHash() {
        Competition competition = newCompetition().build();
        Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().build();

        ProcessRole leadApplicantProcessRole = newProcessRole().withUser(user).withRole(leadApplicantRole).withOrganisationId(organisation.getId()).build();
        Application application = newApplication().withCompetition(competition).withProcessRoles(leadApplicantProcessRole).build();
        InviteOrganisation inviteOrganisation = newInviteOrganisation().build();
        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite().withInviteOrganisation(inviteOrganisation).withApplication(application).build();
        ApplicationInviteResource inviteResource = newApplicationInviteResource().withApplication(application.getId()).build();

        InviteOrganisationResource expectedInviteOrganisationResoucr = newInviteOrganisationResource().
                withId(inviteOrganisation.getId()).
                withInviteResources(singletonList(inviteResource)).
                build();

        when(applicationInviteRepositoryMock.getByHash("an organisation hash")).thenReturn(invite);
        when(inviteOrganisationRepositoryMock.findOne(inviteOrganisation.getId())).thenReturn(inviteOrganisation);
        when(inviteOrganisationMapper.mapToResource(inviteOrganisation)).thenReturn(expectedInviteOrganisationResoucr);

        InviteOrganisationResource inviteOrganisationResource = inviteService.getInviteOrganisationByHash("an organisation hash").getSuccess();

        assertEquals(expectedInviteOrganisationResoucr, inviteOrganisationResource);
    }

    @Test
    public void getInviteOrganisationByHashButInviteOrganisationNotFound() {

        when(applicationInviteRepositoryMock.getByHash("an organisation hash")).thenReturn(null);

        ServiceResult<InviteOrganisationResource> organisationInvite = inviteService.getInviteOrganisationByHash("an organisation hash");
        assertTrue(organisationInvite.isFailure());
        assertTrue(organisationInvite.getFailure().is(notFoundError(ApplicationInvite.class, "an organisation hash")));
    }

    @Test
    public void removeApplicationInvite_deletesInviteOrganisationAndOrganisationApplicationDataOnLastInvite() throws Exception {
        User user = newUser().build();
        Application application = newApplication().build();
        Organisation organisation = newOrganisation().build();
        ApplicationInvite applicationInviteToDelete = newApplicationInvite()
                .withId(24521L)
                .withUser(user)
                .withApplication(application)
                .withInviteOrganisation(newInviteOrganisation().withOrganisation(organisation).build())
                .build();

        List<ProcessRole> inviteProcessRoles = newProcessRole().withOrganisationId(1L).build(1);
        ApplicationFinance applicationFinance = newApplicationFinance().build();

        when(applicationInviteMapper.mapIdToDomain(applicationInviteToDelete.getId())).thenReturn(applicationInviteToDelete);
        when(processRoleRepositoryMock.findByUserAndApplicationId(user, application.getId())).thenReturn(inviteProcessRoles);
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(application.getId(), organisation.getId())).thenReturn(applicationFinance);
        when(applicationServiceMock.getProgressPercentageBigDecimalByApplicationId(application.getId())).thenReturn(serviceSuccess(BigDecimal.valueOf(35L)));

        ServiceResult<Void> applicationInviteResult = inviteService.removeApplicationInvite(applicationInviteToDelete.getId());

        InOrder inOrder = inOrder(questionReassignmentServiceMock, processRoleRepositoryMock, inviteOrganisationRepositoryMock, applicationFinanceRepositoryMock);
        inOrder.verify(questionReassignmentServiceMock).reassignCollaboratorResponsesAndQuestionStatuses(applicationInviteToDelete.getTarget().getId(), inviteProcessRoles, applicationInviteToDelete.getTarget().getLeadApplicantProcessRole());
        inOrder.verify(processRoleRepositoryMock).delete(inviteProcessRoles);
        inOrder.verify(inviteOrganisationRepositoryMock).delete(applicationInviteToDelete.getInviteOrganisation());
        inOrder.verify(applicationFinanceRepositoryMock).delete(applicationFinance);
        inOrder.verifyNoMoreInteractions();

        assertTrue(applicationInviteResult.isSuccess());
    }

    @Test
    public void removeApplicationInvite_deletesInviteFromInviteOrganisationButNotOrganisationFinanceData() throws Exception {
        User user = newUser().build();
        Application application = newApplication().build();
        Organisation organisation = newOrganisation().build();
        InviteOrganisation inviteOrganisation = newInviteOrganisation()
                .withOrganisation(organisation).build();

        List<ApplicationInvite> applicationInvites = newApplicationInvite()
                .withUser(user)
                .withApplication(application)
                .withInviteOrganisation(inviteOrganisation)
                .build(2);

        ApplicationInvite applicationInviteToDelete = applicationInvites.get(0);
        inviteOrganisation.setInvites(applicationInvites);

        List<ProcessRole> inviteProcessRoles = newProcessRole().withOrganisationId(organisation.getId()).build(1);

        when(applicationInviteMapper.mapIdToDomain(applicationInviteToDelete.getId())).thenReturn(applicationInviteToDelete);
        when(processRoleRepositoryMock.findByUserAndApplicationId(user, application.getId())).thenReturn(inviteProcessRoles);
        when(processRoleRepositoryMock.findByApplicationIdAndOrganisationId(application.getId(), organisation.getId())).thenReturn(newProcessRole().withOrganisationId(1L).build(1));

        ServiceResult<Void> applicationInviteResult = inviteService.removeApplicationInvite(applicationInviteToDelete.getId());

        InviteOrganisation expectedApplicationInvite = inviteOrganisation;
        expectedApplicationInvite.getInvites().remove(applicationInviteToDelete);

        InOrder inOrder = inOrder(questionReassignmentServiceMock, processRoleRepositoryMock, inviteOrganisationRepositoryMock, applicationFinanceRepositoryMock);
        inOrder.verify(questionReassignmentServiceMock).reassignCollaboratorResponsesAndQuestionStatuses(applicationInviteToDelete.getTarget().getId(), inviteProcessRoles, applicationInviteToDelete.getTarget().getLeadApplicantProcessRole());
        inOrder.verify(processRoleRepositoryMock).delete(inviteProcessRoles);
        inOrder.verify(inviteOrganisationRepositoryMock).save(expectedApplicationInvite);
        inOrder.verifyNoMoreInteractions();

        assertTrue(applicationInviteResult.isSuccess());
    }

    @Test
    public void removeApplicationInvite_deletesInviteFromInviteOrganisationAndOrganisationFinanceData() throws Exception {
        User user = newUser().build();
        Application application = newApplication().build();
        Organisation organisation = newOrganisation().build();
        InviteOrganisation inviteOrganisation = newInviteOrganisation()
                .withOrganisation(organisation).build();

        List<ApplicationInvite> applicationInvites = newApplicationInvite()
                .withUser(user)
                .withApplication(application)
                .withInviteOrganisation(inviteOrganisation)
                .build(2);

        ApplicationInvite applicationInviteToDelete = applicationInvites.get(0);
        inviteOrganisation.setInvites(applicationInvites);

        List<ProcessRole> inviteProcessRoles = newProcessRole().withOrganisationId(organisation.getId()).build(1);
        ApplicationFinance applicationFinance = newApplicationFinance().build();

        when(applicationInviteMapper.mapIdToDomain(applicationInviteToDelete.getId())).thenReturn(applicationInviteToDelete);
        when(processRoleRepositoryMock.findByUserAndApplicationId(user, application.getId())).thenReturn(inviteProcessRoles);
        when(processRoleRepositoryMock.findByApplicationIdAndOrganisationId(application.getId(), organisation.getId())).thenReturn(inviteProcessRoles);
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(application.getId(), organisation.getId())).thenReturn(applicationFinance);
        when(applicationServiceMock.getProgressPercentageBigDecimalByApplicationId(application.getId())).thenReturn(serviceSuccess(BigDecimal.valueOf(35L)));

        ServiceResult<Void> applicationInviteResult = inviteService.removeApplicationInvite(applicationInviteToDelete.getId());

        InviteOrganisation expectedApplicationInvite = inviteOrganisation;
        expectedApplicationInvite.getInvites().remove(applicationInviteToDelete);

        InOrder inOrder = inOrder(questionReassignmentServiceMock, processRoleRepositoryMock, inviteOrganisationRepositoryMock, applicationFinanceRepositoryMock);
        inOrder.verify(questionReassignmentServiceMock).reassignCollaboratorResponsesAndQuestionStatuses(applicationInviteToDelete.getTarget().getId(), inviteProcessRoles, applicationInviteToDelete.getTarget().getLeadApplicantProcessRole());
        inOrder.verify(processRoleRepositoryMock).delete(inviteProcessRoles);
        inOrder.verify(applicationFinanceRepositoryMock).delete(applicationFinance);
        inOrder.verify(inviteOrganisationRepositoryMock).save(expectedApplicationInvite);
        inOrder.verifyNoMoreInteractions();

        assertTrue(applicationInviteResult.isSuccess());
        assertNull(inviteOrganisation.getOrganisation());
    }

    private void assertInvalidInvites(List<ApplicationInviteResource> inviteResources) {
        Long applicationId = 1L;

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .withOrganisationName("new organisation")
                .build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList(inviteOrganisationResource));
        when(applicationRepositoryMock.findOne(null)).thenReturn(newApplication().withId(1L).build());
        when(inviteOrganisationRepositoryMock.findAll(isA(List.class))).thenReturn(newInviteOrganisation().build(1));

        ServiceResult<InviteResultsResource> result = inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));
        assertTrue(result.isFailure());

        verify(inviteOrganisationRepositoryMock, never()).save(isA(InviteOrganisation.class));
        verify(applicationInviteRepositoryMock, never()).save(isA(List.class));
    }
}
