package org.innovateuk.ifs.project.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.*;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.builder.MonitoringOfficerBuilder;
import org.innovateuk.ifs.project.builder.ProjectBuilder;
import org.innovateuk.ifs.project.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financecheck.domain.CostCategoryType;
import org.innovateuk.ifs.project.financecheck.domain.SpendProfile;
import org.innovateuk.ifs.project.financecheck.transactional.CostCategoryTypeStrategy;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.*;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeBuilder.newAddressType;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.*;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.invite.builder.ProjectInviteBuilder.newInvite;
import static org.innovateuk.ifs.invite.builder.ProjectInviteResourceBuilder.newInviteProjectResource;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsBuilder.newBankDetails;
import static org.innovateuk.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.builder.SpendProfileBuilder.newSpendProfile;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ProjectServiceImplTest extends BaseServiceUnitTest<ProjectService> {

    @Mock
    private CostCategoryTypeStrategy costCategoryTypeStrategyMock;

    @Mock
    private FinanceChecksGenerator financeChecksGeneratorMock;

    @Mock
    private EmailService projectEmailService;

    private Long projectId = 123L;
    private Long applicationId = 456L;
    private Long userId = 7L;
    private Long otherUserId = 8L;

    private Application application;
    private Organisation organisation;
    private Role leadApplicantRole;
    private Role projectManagerRole;
    private Role partnerRole;
    private User user;
    private User u;
    private ProcessRole leadApplicantProcessRole;
    private ProjectUser leadPartnerProjectUser;
    private List<PartnerOrganisation> po;
    private List<ProjectUserResource> puResource;
    private List<ProjectUser> pu;
    private Organisation o;
    private Project project;
    private Project p;
    private MonitoringOfficerResource monitoringOfficerResource;
    private BankDetails bankDetails;
    private SpendProfile spendProfile;

    private static final String webBaseUrl = "https://ifs-local-dev/dashboard";

    @Before
    public void setUp() {

        organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        leadApplicantRole = newRole(LEADAPPLICANT).build();
        projectManagerRole = newRole(UserRoleType.PROJECT_MANAGER).build();

        user = newUser().
                withId(userId).
                build();

        leadApplicantProcessRole = newProcessRole().
                withOrganisationId(organisation.getId()).
                withRole(leadApplicantRole).
                withUser(user).
                build();

        leadPartnerProjectUser = newProjectUser().
                withOrganisation(organisation).
                withRole(PROJECT_PARTNER).
                withUser(user).
                build();

        application = newApplication().
                withId(applicationId).
                withProcessRoles(leadApplicantProcessRole).
                withName("My Application").
                withDurationInMonths(5L).
                withStartDate(LocalDate.of(2017, 3, 2)).
                build();

        project = newProject().
                withId(projectId).
                withApplication(application).
                withProjectUsers(singletonList(leadPartnerProjectUser)).
                build();

        monitoringOfficerResource = newMonitoringOfficerResource()
                .withProject(1L)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();

        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        o = organisation;
        o.setOrganisationType(businessOrganisationType);

        partnerRole = newRole().
                withType(FINANCE_CONTACT).
                build();

        po = newPartnerOrganisation().
                withOrganisation(o).
                withLeadOrganisation(Boolean.TRUE).
                build(1);

        u = newUser().
                withEmailAddress("a@b.com").
                withFirstName("A").
                withLastName("B").
                build();

        pu = newProjectUser().
                withRole(PROJECT_FINANCE_CONTACT).
                withUser(u).
                withOrganisation(o).
                withInvite(newInvite().
                        build()).
                build(1);

        p = newProject().
                withProjectUsers(pu).
                withApplication(application).
                withPartnerOrganisations(po).
                withDateSubmitted(ZonedDateTime.now()).
                withOtherDocumentsApproved(ApprovalType.APPROVED).
                withSpendProfileSubmittedDate(ZonedDateTime.now()).
                build();

        puResource = newProjectUserResource().
                withProject(p.getId()).
                withOrganisation(o.getId()).
                withRole(partnerRole.getId()).
                withRoleName(PROJECT_PARTNER.getName()).
                build(1);

        bankDetails = newBankDetails().withOrganisation(o).withApproval(Boolean.TRUE).build();
        spendProfile = newSpendProfile().withOrganisation(o).withGeneratedDate(Calendar.getInstance()).withMarkedComplete(Boolean.TRUE).withApproval(ApprovalType.EMPTY).build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());
    }

    @Test
    public void testCreateProjectFromApplication() {

        Role partnerRole = newRole().withType(PARTNER).build();

        ProjectResource newProjectResource = newProjectResource().build();

        PartnerOrganisation savedProjectPartnerOrganisation = newPartnerOrganisation().
                withOrganisation(organisation).
                withLeadOrganisation(true).
                build();

        Project savedProject = newProject().
                withId(newProjectResource.getId()).
                withApplication(application).
                withProjectUsers(asList(leadPartnerProjectUser, newProjectUser().build())).
                withPartnerOrganisations(singletonList(savedProjectPartnerOrganisation)).
                build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);

        Project newProjectExpectations = createProjectExpectationsFromOriginalApplication();
        when(projectRepositoryMock.save(newProjectExpectations)).thenReturn(savedProject);

        CostCategoryType costCategoryTypeForOrganisation = newCostCategoryType().
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(newCostCategory().withName("Cat1", "Cat2").build(2)).
                        build()).
                build();

        when(costCategoryTypeStrategyMock.getOrCreateCostCategoryTypeForSpendProfile(savedProject.getId(),
                organisation.getId())).thenReturn(serviceSuccess(costCategoryTypeForOrganisation));

        when(financeChecksGeneratorMock.createMvpFinanceChecksFigures(savedProject, organisation, costCategoryTypeForOrganisation)).thenReturn(serviceSuccess());
        when(financeChecksGeneratorMock.createFinanceChecksFigures(savedProject, organisation)).thenReturn(serviceSuccess());

        when(projectDetailsWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);
        when(financeCheckWorkflowHandlerMock.projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser)).thenReturn(true);
        when(viabilityWorkflowHandlerMock.projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser)).thenReturn(true);
        when(eligibilityWorkflowHandlerMock.projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser)).thenReturn(true);
        when(golWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);
        when(projectWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);

        when(projectMapperMock.mapToResource(savedProject)).thenReturn(newProjectResource);

        ServiceResult<ProjectResource> project = service.createProjectFromApplication(applicationId);
        assertTrue(project.isSuccess());
        assertEquals(newProjectResource, project.getSuccessObject());

        verify(costCategoryTypeStrategyMock).getOrCreateCostCategoryTypeForSpendProfile(savedProject.getId(), organisation.getId());
        verify(financeChecksGeneratorMock).createMvpFinanceChecksFigures(savedProject, organisation, costCategoryTypeForOrganisation);
        verify(financeChecksGeneratorMock).createFinanceChecksFigures(savedProject, organisation);

        verify(projectDetailsWorkflowHandlerMock).projectCreated(savedProject, leadPartnerProjectUser);
        verify(financeCheckWorkflowHandlerMock).projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser);
        verify(viabilityWorkflowHandlerMock).projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser);
        verify(eligibilityWorkflowHandlerMock).projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser);
        verify(golWorkflowHandlerMock).projectCreated(savedProject, leadPartnerProjectUser);
        verify(projectWorkflowHandlerMock).projectCreated(savedProject, leadPartnerProjectUser);
        verify(projectMapperMock).mapToResource(savedProject);
    }

    @Test
    public void testCreateProjectFromApplicationAlreadyExists() {

        ProjectResource existingProjectResource = newProjectResource().build();
        Project existingProject = newProject().withApplication(application).build();

        when(projectRepositoryMock.findOneByApplicationId(applicationId)).thenReturn(existingProject);
        when(projectMapperMock.mapToResource(existingProject)).thenReturn(existingProjectResource);

        ServiceResult<ProjectResource> project = service.createProjectFromApplication(applicationId);
        assertTrue(project.isSuccess());
        assertEquals(existingProjectResource, project.getSuccessObject());

        verify(projectRepositoryMock).findOneByApplicationId(applicationId);
        verify(projectMapperMock).mapToResource(existingProject);

        verify(costCategoryTypeStrategyMock, never()).getOrCreateCostCategoryTypeForSpendProfile(any(Long.class), any(Long.class));
        verify(financeChecksGeneratorMock, never()).createMvpFinanceChecksFigures(any(Project.class), any(Organisation.class), any(CostCategoryType.class));
        verify(financeChecksGeneratorMock, never()).createFinanceChecksFigures(any(Project.class), any(Organisation.class));
        verify(projectDetailsWorkflowHandlerMock, never()).projectCreated(any(Project.class), any(ProjectUser.class));
        verify(financeCheckWorkflowHandlerMock, never()).projectCreated(any(PartnerOrganisation.class), any(ProjectUser.class));
        verify(golWorkflowHandlerMock, never()).projectCreated(any(Project.class), any(ProjectUser.class));
        verify(projectWorkflowHandlerMock, never()).projectCreated(any(Project.class), any(ProjectUser.class));

    }

    @Test
    public void testInvalidProjectManagerProvided() {

        ServiceResult<Void> result = service.setProjectManager(projectId, otherUserId);
        assertFalse(result.isSuccess());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER));
    }

    @Test
    public void testSetProjectManagerWhenProjectDetailsAlreadySubmitted() {

        Project existingProject = newProject().build();

        assertTrue(existingProject.getProjectUsers().isEmpty());

        when(projectRepositoryMock.findOne(projectId)).thenReturn(existingProject);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(existingProject)).thenReturn(true);

        ServiceResult<Void> result = service.setProjectManager(projectId, userId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED));

        assertTrue(existingProject.getProjectUsers().isEmpty());
    }

    @Test
    public void testValidProjectManagerProvided() {

        when(roleRepositoryMock.findOneByName(PROJECT_MANAGER.getName())).thenReturn(projectManagerRole);

        when(projectDetailsWorkflowHandlerMock.projectManagerAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> result = service.setProjectManager(projectId, userId);
        assertTrue(result.isSuccess());

        ProjectUser expectedProjectManager = newProjectUser().
                withId().
                withProject(project).
                withOrganisation(organisation).
                withRole(PROJECT_MANAGER).
                withUser(user).
                build();

        assertEquals(expectedProjectManager, project.getProjectUsers().get(project.getProjectUsers().size() - 1));
    }

    @Test
    public void testValidProjectManagerProvidedWithExistingProjectManager() {

        User differentUser = newUser().build();
        Organisation differentOrganisation = newOrganisation().build();

        @SuppressWarnings("unused")
        ProjectUser existingProjectManager = newProjectUser().
                withId(456L).
                withProject(project).
                withRole(PROJECT_MANAGER).
                withOrganisation(differentOrganisation).
                withUser(differentUser).
                build();

        when(roleRepositoryMock.findOneByName(PROJECT_MANAGER.getName())).thenReturn(projectManagerRole);

        when(projectDetailsWorkflowHandlerMock.projectManagerAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(leadPartnerProjectUser.getId()).build());

        ServiceResult<Void> result = service.setProjectManager(projectId, userId);
        assertTrue(result.isSuccess());

        ProjectUser expectedProjectManager = newProjectUser().
                withId(456L).
                withProject(project).
                withOrganisation(organisation).
                withRole(PROJECT_MANAGER).
                withUser(user).
                build();

        assertEquals(expectedProjectManager, project.getProjectUsers().get(project.getProjectUsers().size() - 1));

        verify(projectDetailsWorkflowHandlerMock).projectManagerAdded(project, leadPartnerProjectUser);
    }

    @Test
    public void testUpdateProjectStartDate() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isSuccess());

        verify(projectRepositoryMock).findOne(123L);
        assertEquals(validDate, existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateButProjectDoesntExist() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        when(projectRepositoryMock.findOne(123L)).thenReturn(null);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(notFoundError(Project.class, 123L)));
    }

    @Test
    public void testUpdateProjectStartDateButStartDateDoesntBeginOnFirstDayOfMonth() {

        LocalDate now = LocalDate.now();
        LocalDate dateNotOnFirstDayOfMonth = LocalDate.of(now.getYear(), now.getMonthValue(), 2).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, dateNotOnFirstDayOfMonth);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));

        verify(projectRepositoryMock, never()).findOne(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateButStartDateNotInFuture() {

        LocalDate now = LocalDate.now();
        LocalDate pastDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).minusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, pastDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));

        verify(projectRepositoryMock, never()).findOne(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateWhenProjectDetailsAlreadySubmitted() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        when(projectDetailsWorkflowHandlerMock.isSubmitted(existingProject)).thenReturn(true);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED));

        verify(projectRepositoryMock).findOne(123L);
        assertNull(existingProject.getTargetStartDate());
    }
    
    @Test
    public void testUpdateFinanceContact() {

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withId(7L).build();

        newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(PROJECT_PARTNER).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        when(organisationRepositoryMock.findOne(5L)).thenReturn(organisation);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, 7L);

        assertTrue(updateResult.isSuccess());

        List<ProjectUser> foundFinanceContacts = simpleFilter(project.getProjectUsers(), projectUser ->
                projectUser.getOrganisation().equals(organisation) &&
                        projectUser.getUser().equals(user) &&
                        projectUser.getProcess().equals(project) &&
                        projectUser.getRole().equals(PROJECT_FINANCE_CONTACT));

        assertEquals(1, foundFinanceContacts.size());
    }

    @Test
    public void testUpdateFinanceContactButUserIsNotExistingPartner() {

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withId(7L).build();
        newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(PROJECT_MANAGER).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        when(organisationRepositoryMock.findOne(5L)).thenReturn(organisation);

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, 7L);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION));

        verify(processRoleRepositoryMock, never()).save(isA(ProcessRole.class));
    }

    @Test
    public void testUpdateFinanceContactWhenNotPresentOnTheProject() {

        long userIdForUserNotOnProject = 6L;

        Project existingProject = newProject().withId(123L).build();
        Project anotherProject = newProject().withId(9999L).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);
        when(projectWorkflowHandlerMock.getState(existingProject)).thenReturn(ProjectState.SETUP);

        Organisation organisation = newOrganisation().withId(5L).build();
        when(organisationRepositoryMock.findOne(5L)).thenReturn(organisation);

        User user = newUser().withId(7L).build();
        newProjectUser().withOrganisation(organisation).withUser(user).withProject(anotherProject).withRole(PROJECT_PARTNER).build();

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, userIdForUserNotOnProject);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION));
    }

    @Test
    public void testUpdateFinanceContactAllowedWhenFinanceContactAlreadySet() {

        User anotherUser = newUser().build();
        Project existingProject = newProject().build();
        when(projectRepositoryMock.findOne(existingProject.getId())).thenReturn(existingProject);
        when(projectWorkflowHandlerMock.getState(existingProject)).thenReturn(ProjectState.SETUP);

        Organisation organisation = newOrganisation().build();
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);

        newProjectUser().
                withOrganisation(organisation).
                withUser(user, anotherUser).
                withProject(existingProject).
                withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).build(2);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> updateResult = service.updateFinanceContact(existingProject.getId(), organisation.getId(), anotherUser.getId());
        assertTrue(updateResult.isSuccess());

        List<ProjectUser> organisationFinanceContacts = existingProject.getProjectUsers(pu -> pu.getRole().equals(PROJECT_FINANCE_CONTACT) &&
                pu.getOrganisation().equals(organisation));

        assertEquals(1, organisationFinanceContacts.size());
        assertEquals(anotherUser, organisationFinanceContacts.get(0).getUser());
    }

    @Test
    public void testUpdateFinanceContactNotAllowedWhenProjectLive() {

        User anotherUser = newUser().build();
        Project existingProject = newProject().build();
        when(projectRepositoryMock.findOne(existingProject.getId())).thenReturn(existingProject);
        when(projectWorkflowHandlerMock.getState(existingProject)).thenReturn(ProjectState.LIVE);

        Organisation organisation = newOrganisation().build();
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);

        newProjectUser().
                withOrganisation(organisation).
                withUser(user, anotherUser).
                withProject(existingProject).
                withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).build(2);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> updateResult = service.updateFinanceContact(existingProject.getId(), organisation.getId(), anotherUser.getId());

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }

    @Test
    public void testInviteProjectManagerWhenProjectNotInDB() {

        Long projectId = 1L;

        InviteProjectResource inviteResource = newInviteProjectResource()
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(17L)
                .withInviteOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();


        when(inviteProjectMapperMock.mapToDomain(inviteResource)).thenReturn(newInvite().withEmailAddress("a@b.com").withName("A B").build());

        when(projectRepositoryMock.findOne(projectId)).thenThrow(new IllegalArgumentException());

        ServiceResult<Void> result = null;

        try {
            result = service.inviteProjectManager(projectId, inviteResource);
        } catch (Exception e) {

            // We expect an exception to be thrown
            assertTrue(e instanceof IllegalArgumentException);

            assertNull(result);
            verify(projectEmailService, never()).sendEmail(any(), any(), any());

            // This exception flow is the only expected flow, so return from here and assertFalse if no exception
            return;
        }

        // Should not reach here - we must get an exception
        assertFalse(true);
    }

    @Test
    public void testInviteProjectManagerWhenUnableToSendNotification() {

        Long projectId = 1L;

        InviteProjectResource inviteResource = newInviteProjectResource()
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(17L)
                .withInviteOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");
        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("projectName", "Project 1");
        globalArgs.put("leadOrganisation", organisation.getName());
        globalArgs.put("inviteOrganisationName", "Invite Organisation 1");
        globalArgs.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());
        when(projectEmailService.sendEmail(singletonList(to), globalArgs, ProjectServiceImpl.Notifications.INVITE_PROJECT_MANAGER)).
                thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE)));

        when(inviteProjectMapperMock.mapToDomain(inviteResource)).thenReturn(newInvite().withEmailAddress("a@b.com").withName("A B").build());

        ServiceResult<Void> result = service.inviteProjectManager(projectId, inviteResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE));
    }

    @Test
    public void testInviteProjectManagerSuccess() {

        Long projectId = 1L;

        InviteProjectResource inviteResource = newInviteProjectResource()
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(17L)
                .withInviteOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");
        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("projectName", "Project 1");
        globalArgs.put("leadOrganisation", organisation.getName());
        globalArgs.put("inviteOrganisationName", "Invite Organisation 1");
        globalArgs.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());
        when(projectEmailService.sendEmail(singletonList(to), globalArgs, ProjectServiceImpl.Notifications.INVITE_PROJECT_MANAGER)).thenReturn(serviceSuccess());

        when(inviteProjectMapperMock.mapToDomain(inviteResource)).thenReturn(newInvite().withEmailAddress("a@b.com").withName("A B").build());

        ServiceResult<Void> result = service.inviteProjectManager(projectId, inviteResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testInviteFinanceContactSuccess() {

        Long projectId = 1L;

        InviteProjectResource inviteResource = newInviteProjectResource()
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(17L)
                .withInviteOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("projectName", "Project 1");
        globalArgs.put("leadOrganisation", organisation.getName());
        globalArgs.put("inviteOrganisationName", "Invite Organisation 1");
        globalArgs.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());
        when(projectEmailService.sendEmail(singletonList(to), globalArgs, ProjectServiceImpl.Notifications.INVITE_FINANCE_CONTACT)).thenReturn(serviceSuccess());

        when(inviteProjectMapperMock.mapToDomain(inviteResource)).thenReturn(newInvite().withName("A B").withEmailAddress("a@b.com").build());

        ServiceResult<Void> result = service.inviteFinanceContact(projectId, inviteResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testFindByUserIdReturnsOnlyDistinctProjects() {

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withId(7L).build();

        ProjectUser projectUserWithPartnerRole = newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(PROJECT_PARTNER).build();
        ProjectUser projectUserWithFinanceRole = newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(PROJECT_FINANCE_CONTACT).build();

        List<ProjectUser> projectUserRecords = asList(projectUserWithPartnerRole, projectUserWithFinanceRole);

        ProjectResource projectResource = newProjectResource().withId(project.getId()).build();

        when(projectUserRepositoryMock.findByUserId(user.getId())).thenReturn(projectUserRecords);

        when(projectMapperMock.mapToResource(project)).thenReturn(projectResource);

        ServiceResult<List<ProjectResource>> result = service.findByUserId(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(result.getSuccessObject().size(), 1L);
    }

    @Test
    public void testUpdateProjectAddressToBeRegisteredAddress() {

        Organisation leadOrganisation = newOrganisation().withId(1L).build();
        AddressResource existingRegisteredAddressResource = newAddressResource().build();
        Address registeredAddress = newAddress().build();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(addressRepositoryMock.exists(existingRegisteredAddressResource.getId())).thenReturn(true);
        when(addressRepositoryMock.findOne(existingRegisteredAddressResource.getId())).thenReturn(registeredAddress);

        when(projectDetailsWorkflowHandlerMock.projectAddressAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), REGISTERED, existingRegisteredAddressResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateProjectAddressToBeOperatingAddress() {
       
        Organisation leadOrganisation = newOrganisation().withId(1L).build();
        AddressResource existingOperatingAddressResource = newAddressResource().build();
        Address operatingAddress = newAddress().build();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(addressRepositoryMock.exists(existingOperatingAddressResource.getId())).thenReturn(true);
        when(addressRepositoryMock.findOne(existingOperatingAddressResource.getId())).thenReturn(operatingAddress);

        when(projectDetailsWorkflowHandlerMock.projectAddressAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), OPERATING, existingOperatingAddressResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateProjectAddressToNewProjectAddress() {

        Organisation leadOrganisation = newOrganisation().withId(organisation.getId()).build();
        AddressResource newAddressResource = newAddressResource().build();
        Address newAddress = newAddress().build();
        AddressType projectAddressType = newAddressType().withId((long) PROJECT.getOrdinal()).withName(PROJECT.name()).build();
        OrganisationAddress organisationAddress = newOrganisationAddress().withOrganisation(leadOrganisation).withAddress(newAddress).withAddressType(projectAddressType).build();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(addressRepositoryMock.exists(newAddressResource.getId())).thenReturn(false);
        when(addressMapperMock.mapToDomain(newAddressResource)).thenReturn(newAddress);
        when(addressTypeRepositoryMock.findOne(PROJECT.getOrdinal())).thenReturn(projectAddressType);
        when(organisationAddressRepositoryMock.findByOrganisationIdAndAddressType(leadOrganisation.getId(), projectAddressType)).thenReturn(emptyList());
        when(organisationAddressRepositoryMock.save(organisationAddress)).thenReturn(organisationAddress);

        when(projectDetailsWorkflowHandlerMock.projectAddressAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), PROJECT, newAddressResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSubmitProjectDetails() {

        ZonedDateTime now = ZonedDateTime.now();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(projectDetailsWorkflowHandlerMock.submitProjectDetails(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> result = service.submitProjectDetails(project.getId(), now);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSubmitProjectDetailsButSubmissionNotAllowed() {

        ZonedDateTime now = ZonedDateTime.now();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(projectDetailsWorkflowHandlerMock.submitProjectDetails(project, leadPartnerProjectUser)).thenReturn(false);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> result = service.submitProjectDetails(project.getId(), now);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_SUBMITTED_IF_INCOMPLETE));
    }

    @Test
    public void testSaveMOWithDiffProjectIdInURLAndMOResource() {

        Long projectid = 1L;

        MonitoringOfficerResource monitoringOfficerResource = newMonitoringOfficerResource()
                .withProject(3L)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();

        ServiceResult<SaveMonitoringOfficerResult> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE));
    }

    @Test
    public void testSaveMOWhenProjectDetailsNotYetSubmitted() {

        Long projectid = 1L;

        Project projectInDB = newProject().withId(1L).build();

        when(projectRepositoryMock.findOne(projectid)).thenReturn(projectInDB);

        ServiceResult<SaveMonitoringOfficerResult> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        assertTrue(result.getFailure().is(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED));
    }

    @Test
    public void testSaveMOWhenMOExistsForAProject() {

        Long projectid = 1L;

        // Set this to different values, so that we can assert that it gets updated
        MonitoringOfficer monitoringOfficerInDB = MonitoringOfficerBuilder.newMonitoringOfficer()
                .withFirstName("def")
                .withLastName("klm")
                .withEmail("def.klm@gmail.com")
                .withPhoneNumber("079237439")
                .build();


        Project projectInDB = newProject().withId(1L).build();

        when(projectRepositoryMock.findOne(projectid)).thenReturn(projectInDB);
        when(monitoringOfficerRepositoryMock.findOneByProjectId(monitoringOfficerResource.getProject())).thenReturn(monitoringOfficerInDB);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(projectInDB)).thenReturn(true);

        ServiceResult<SaveMonitoringOfficerResult> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        // Assert that the MO in DB is updated with the correct values from MO Resource
        Assert.assertEquals("First name of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getFirstName(), monitoringOfficerResource.getFirstName());
        Assert.assertEquals("Last name of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getLastName(), monitoringOfficerResource.getLastName());
        Assert.assertEquals("Email of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getEmail(), monitoringOfficerResource.getEmail());
        Assert.assertEquals("Phone number of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getPhoneNumber(), monitoringOfficerResource.getPhoneNumber());

        Optional<SaveMonitoringOfficerResult> successResult = result.getOptionalSuccessObject();
        assertTrue(successResult.isPresent());
        assertTrue(successResult.get().isMonitoringOfficerSaved());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveMOWhenMODetailsRemainsTheSame() {

        Long projectId = 1L;

        // The details for the MO is set to the same as in resource
        MonitoringOfficer monitoringOfficerInDB = MonitoringOfficerBuilder.newMonitoringOfficer()
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();


        Project projectInDB = newProject().withId(1L).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(monitoringOfficerRepositoryMock.findOneByProjectId(monitoringOfficerResource.getProject())).thenReturn(monitoringOfficerInDB);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(projectInDB)).thenReturn(true);

        ServiceResult<SaveMonitoringOfficerResult> result = service.saveMonitoringOfficer(projectId, monitoringOfficerResource);

        Optional<SaveMonitoringOfficerResult> successResult = result.getOptionalSuccessObject();
        assertTrue(successResult.isPresent());
        assertFalse(successResult.get().isMonitoringOfficerSaved());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveMOWhenMODoesNotExistForAProject() {

        Long projectid = 1L;

        Project projectInDB = newProject().withId(1L).build();

        when(projectRepositoryMock.findOne(projectid)).thenReturn(projectInDB);
        when(monitoringOfficerRepositoryMock.findOneByProjectId(monitoringOfficerResource.getProject())).thenReturn(null);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(projectInDB)).thenReturn(true);

        ServiceResult<SaveMonitoringOfficerResult> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        Optional<SaveMonitoringOfficerResult> successResult = result.getOptionalSuccessObject();
        assertTrue(successResult.isPresent());
        assertTrue(successResult.get().isMonitoringOfficerSaved());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetMonitoringOfficerWhenMODoesNotExistInDB() {

        Long projectid = 1L;

        ServiceResult<MonitoringOfficerResource> result = service.getMonitoringOfficer(projectid);

        String errorKey = result.getFailure().getErrors().get(0).getErrorKey();
        Assert.assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.name(), errorKey);
    }

    @Test
    public void testGetMonitoringOfficerWhenMOExistsInDB() {

        Long projectid = 1L;

        MonitoringOfficer monitoringOfficerInDB = MonitoringOfficerBuilder.newMonitoringOfficer()
                .withFirstName("def")
                .withLastName("klm")
                .withEmail("def.klm@gmail.com")
                .withPhoneNumber("079237439")
                .build();

        when(monitoringOfficerRepositoryMock.findOneByProjectId(projectid)).thenReturn(monitoringOfficerInDB);

        ServiceResult<MonitoringOfficerResource> result = service.getMonitoringOfficer(projectid);

        assertTrue(result.isSuccess());

    }


    @Test
    public void testCannotSubmitDocumentsAlreadySubmitted() {

        Long projectId = 1L;
        ProjectUser projectUserToSet = newProjectUser()
                .withId(1L)
                .withUser(newUser().withId(1L).build())
                .withRole(PROJECT_MANAGER)
                .build();

        List<ProjectUser> pu = Collections.singletonList(projectUserToSet);

        Project projectInDB = newProject().withId(projectId).withProjectUsers(pu)
                .withOtherDocumentsApproved(ApprovalType.UNSET).withOtherDocumentsSubmittedDate(ZonedDateTime.now()).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        ServiceResult<Boolean> result = service.isOtherDocumentsSubmitAllowed(projectId, 1L);

        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());

        assertThat(projectInDB.getOtherDocumentsApproved(), Matchers.equalTo(ApprovalType.UNSET));

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsWhenProjectNotInDB() {

        Long projectId = 1L;

        when(projectRepositoryMock.findOne(projectId)).thenReturn(null);

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, true);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(CommonErrors.notFoundError(Project.class, projectId)));

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsWithoutDecisionError() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, null);

        assertTrue(result.isFailure());

        assertThat(projectInDB.getOtherDocumentsApproved(), Matchers.equalTo(ApprovalType.UNSET));

    }


    @Test
    public void testAcceptOrRejectOtherDocumentsAlreadyApprovedError() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId)
                .withOtherDocumentsApproved(ApprovalType.APPROVED).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, null);

        assertTrue(result.isFailure());

        assertThat(projectInDB.getOtherDocumentsApproved(), Matchers.equalTo(ApprovalType.APPROVED));

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsSuccess() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(projectGrantOfferServiceMock.generateGrantOfferLetterIfReady(1L)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, true);

        assertTrue(result.isSuccess());

        assertEquals(ApprovalType.APPROVED, projectInDB.getOtherDocumentsApproved());
        verify(projectGrantOfferServiceMock).generateGrantOfferLetterIfReady(1L);

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsRejectSuccess() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId).withOtherDocumentsSubmittedDate(ZonedDateTime.now()).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(projectGrantOfferServiceMock.generateGrantOfferLetterIfReady(1L)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, false);

        assertTrue(result.isSuccess());

        assertEquals(ApprovalType.REJECTED, projectInDB.getOtherDocumentsApproved());
        assertEquals(null, projectInDB.getDocumentsSubmittedDate());
        verify(projectGrantOfferServiceMock).generateGrantOfferLetterIfReady(1L);

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsFailureGenerateGolFails() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(projectGrantOfferServiceMock.generateGrantOfferLetterIfReady(1L)).thenReturn(serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE));

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, true);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE));

        assertEquals(ApprovalType.APPROVED, projectInDB.getOtherDocumentsApproved());
        verify(projectGrantOfferServiceMock).generateGrantOfferLetterIfReady(1L);

    }

    @Test
    public void testUpdateDocumentsResetApproval() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId).withOtherDocumentsApproved(ApprovalType.REJECTED).build();
        FileEntry entry = newFileEntry().build();
        FileEntryResource entryResource = newFileEntryResource().build();
        Supplier<InputStream> input = () -> null;

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(projectWorkflowHandlerMock.getState(projectInDB)).thenReturn(ProjectState.SETUP);

        ServiceResult<Pair<File, FileEntry>> successfulFileUpdateResult = serviceSuccess(Pair.of(new File("updatedfile"), entry));
        when(fileServiceMock.updateFile(any(), any())).thenReturn(successfulFileUpdateResult);

        ServiceResult<Void> result = service.updateCollaborationAgreementFileEntry(projectId, entryResource, input);

        assertTrue(result.isSuccess());

        assertEquals(ApprovalType.UNSET, projectInDB.getOtherDocumentsApproved());
        verify(fileServiceMock).updateFile(entryResource, input);

    }

    @Test
    public void testCreateCollaborationAgreementFileEntry() {
        assertCreateFile(
                project::getCollaborationAgreement,
                (fileToCreate, inputStreamSupplier) ->
                        service.createCollaborationAgreementFileEntry(123L, fileToCreate, inputStreamSupplier));
    }

    @Test
    public void testUpdateCollaborationAgreementFileEntry() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        assertUpdateFile(
                project::getCollaborationAgreement,
                (fileToUpdate, inputStreamSupplier) ->
                        service.updateCollaborationAgreementFileEntry(123L, fileToUpdate, inputStreamSupplier));
    }

    @Test
    public void testFailureUpdateCollaborationAgreementFileEntryProjectLive() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);

        FileEntryResource fileToUpdate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        ServiceResult<Void> result = service.updateCollaborationAgreementFileEntry(123L, fileToUpdate, inputStreamSupplier);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));

    }

    @Test
    public void testGetCollaborationAgreementFileEntryDetails() {
        assertGetFileDetails(
                project::setCollaborationAgreement,
                () -> service.getCollaborationAgreementFileEntryDetails(123L));
    }

    @Test
    public void testGetCollaborationAgreementFileContents() {
        assertGetFileContents(
                project::setCollaborationAgreement,
                () -> service.getCollaborationAgreementFileContents(123L));
    }

    @Test
    public void testDeleteCollaborationAgreementFile() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        assertDeleteFile(
                project::getCollaborationAgreement,
                project::setCollaborationAgreement,
                () -> service.deleteCollaborationAgreementFile(123L));
    }

    @Test
    public void testFailureDeleteCollaborationAgreementFileEntryProjectLive() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);

        ServiceResult<Void> result = service.deleteCollaborationAgreementFile(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }

    @Test
    public void testCreateExploitationPlanFileEntry() {
        assertCreateFile(
                project::getExploitationPlan,
                (fileToCreate, inputStreamSupplier) ->
                        service.createExploitationPlanFileEntry(123L, fileToCreate, inputStreamSupplier));
    }

    @Test
    public void testUpdateExploitationPlanFileEntry() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);

        assertUpdateFile(
                project::getExploitationPlan,
                (fileToUpdate, inputStreamSupplier) ->
                        service.updateExploitationPlanFileEntry(123L, fileToUpdate, inputStreamSupplier));
    }

    @Test
    public void testFailureUpdateExploitationPlanFileProjectLive() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);

        FileEntryResource fileToUpdate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        ServiceResult<Void> result = service.updateExploitationPlanFileEntry(project.getId(), fileToUpdate, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));

    }

    @Test
    public void testGetExploitationPlanFileEntryDetails() {
        assertGetFileDetails(
                project::setExploitationPlan,
                () -> service.getExploitationPlanFileEntryDetails(123L));
    }

    @Test
    public void testGetExploitationPlanFileContents() {
        assertGetFileContents(
                project::setExploitationPlan,
                () -> service.getExploitationPlanFileContents(123L));
    }

    @Test
    public void testDeleteExploitationPlanFile() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);

        assertDeleteFile(
                project::getExploitationPlan,
                project::setExploitationPlan,
                () -> service.deleteExploitationPlanFile(123L));
    }

    @Test
    public void testFailureDeleteExploitationPlanFileProjectLive() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);

        ServiceResult<Void> result = service.deleteCollaborationAgreementFile(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }

    @Test
    public void testFilesCanBeSubmitted() {
        assertFilesCanBeSubmittedByProjectManagerAndFilesExist(
                project::setCollaborationAgreement,
                project::setExploitationPlan,
                () -> service.isOtherDocumentsSubmitAllowed(123L, 1L));

    }

    @Test
    public void testFilesCannotBeSubmittedIfUserNotProjectManager() {
        assertFilesCannotBeSubmittedIfNotByProjectManager(
                project::setCollaborationAgreement,
                project::setExploitationPlan,
                () -> service.isOtherDocumentsSubmitAllowed(123L, 1L));

    }

    @Test
    public void testSaveDocumentsSubmitDateTimeIsSuccessfulWhenUploadsComplete() {
        ProjectUser projectUserToSet = newProjectUser()
                .withId(1L)
                .withUser(newUser().withId(1L).build())
                .withRole(ProjectParticipantRole.PROJECT_MANAGER)
                .build();
        List<ProjectUser> projectUsers = new ArrayList<>();
        projectUsers.add(projectUserToSet);
        Project project = newProject().build();
        project.setProjectUsers(projectUsers);

        when(projectUserRepositoryMock.findByProjectId(project.getId())).thenReturn(projectUsers);
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);

        assertSetDocumentsDateTimeIfProjectManagerAndFilesExist(
                project::setCollaborationAgreement,
                project::setExploitationPlan,
                () -> service.saveDocumentsSubmitDateTime(project.getId(), ZonedDateTime.now()));

        assertNotNull(project.getCollaborationAgreement());
        assertNotNull(project.getExploitationPlan());
        assertTrue(project.getProjectUsers().get(0).getRole().getName()
                .equals(UserRoleType.PROJECT_MANAGER.getName()));
        assertNotNull(project.getDocumentsSubmittedDate());
    }

    @Test
    public void testSaveDocumentsSubmitDateTimeFailsWhenUploadsIncomplete() {
        ProjectUser projectUserToSet = newProjectUser()
                .withId(1L)
                .withUser(newUser().withId(1L).build())
                .withRole(ProjectParticipantRole.PROJECT_MANAGER)
                .build();
        List<ProjectUser> projectUsers = new ArrayList<>();
        projectUsers.add(projectUserToSet);
        Project project = newProject().build();
        project.setProjectUsers(projectUsers);

        when(projectUserRepositoryMock.findByProjectId(project.getId())).thenReturn(projectUsers);
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);

        ServiceResult<Void> result = service.saveDocumentsSubmitDateTime(project.getId(), ZonedDateTime.now());

        assertTrue(result.isFailure());
        assertNull(project.getCollaborationAgreement());
        assertNull(project.getExploitationPlan());
        assertTrue(project.getProjectUsers().get(0).getRole().getName()
                .equals(UserRoleType.PROJECT_MANAGER.getName()));
        assertNull(project.getDocumentsSubmittedDate());
    }


    private void assertSetDocumentsDateTimeIfProjectManagerAndFilesExist(Consumer<FileEntry> fileSetter1,
                                                                       Consumer<FileEntry> fileSetter2,
                                                                       Supplier<ServiceResult<Void>> getConditionFn) {
        Supplier<InputStream> inputStreamSupplier1 = () -> null;
        Supplier<InputStream> inputStreamSupplier2 = () -> null;

        getFileEntryResources(fileSetter1, fileSetter2, inputStreamSupplier1, inputStreamSupplier2);
        ServiceResult<Void> result = getConditionFn.get();

        assertTrue(result.isSuccess());

    }

    @Test
    public void testInviteProjectFinanceUser(){
        InviteProjectResource invite = newInviteProjectResource().withInviteOrganisationName("Invite Organisation 1").build();
        ProcessRole[] roles = newProcessRole()
                .withOrganisationId(o.getId())
                .withRole(LEADAPPLICANT)
                .build(1)
                .toArray(new ProcessRole[0]);
        Application a = newApplication().withProcessRoles(roles).build();

        Project project = newProject().withId(projectId).withName("Project 1").withApplication(a).build();

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");

        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("projectName", "Project 1");
        globalArgs.put("leadOrganisation", organisation.getName());
        globalArgs.put("inviteOrganisationName", "Invite Organisation 1");
        globalArgs.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + invite.getHash());
        when(projectEmailService.sendEmail(singletonList(to), globalArgs, ProjectServiceImpl.Notifications.INVITE_FINANCE_CONTACT)).thenReturn(serviceSuccess());
        when(inviteProjectMapperMock.mapToDomain(invite)).thenReturn(newInvite().withEmailAddress("a@b.com").withName("A B").build());

        ServiceResult<Void> success = service.inviteFinanceContact(project.getId(), invite);

        assertTrue(success.isSuccess());
    }

    @Test
    public void testAddPartnerOrganisationNotOnProject(){
        Organisation organisationNotOnProject = newOrganisation().build();
        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(organisationRepositoryMock.findOne(organisationNotOnProject.getId())).thenReturn(organisationNotOnProject);
        when(userRepositoryMock.findOne(u.getId())).thenReturn(u);
        // Method under test
        ServiceResult<ProjectUser> shouldFail = service.addPartner(p.getId(), u.getId(), organisationNotOnProject.getId());
        // Expectations
        assertTrue(shouldFail.isFailure());
        assertTrue(shouldFail.getFailure().is(badRequestError("project does not contain organisation")));
    }

    @Test
    public void testAddPartnerPartnerAlreadyExists(){
        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(userRepositoryMock.findOne(u.getId())).thenReturn(u);

        setLoggedInUser(newUserResource().withId(u.getId()).build());

        // Method under test
        ServiceResult<ProjectUser> shouldFail = service.addPartner(p.getId(), u.getId(), o.getId());
        // Expectations
        verifyZeroInteractions(projectUserRepositoryMock);
        assertTrue(shouldFail.isSuccess());
    }

    @Test
    public void testAddPartner(){
        User newUser = newUser().build();
        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(userRepositoryMock.findOne(u.getId())).thenReturn(u);
        when(userRepositoryMock.findOne(newUser.getId())).thenReturn(u);
        List<ProjectInvite> projectInvites = newInvite().withUser(user).build(1);
        projectInvites.get(0).open();
        when(inviteProjectRepositoryMock.findByProjectId(p.getId())).thenReturn(projectInvites);

        // Method under test
        ServiceResult<ProjectUser> shouldSucceed = service.addPartner(p.getId(), newUser.getId(), o.getId());
        // Expectations
        assertTrue(shouldSucceed.isSuccess());
    }

    @Test
    public void testGetProjectTeamStatus(){
        Role partnerRole = newRole().withType(PARTNER).build();

        /**
         * Create 3 organisations:
         * 2 Business, 1 Academic
         * **/
        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        OrganisationType academicOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        List<Organisation> organisations = new ArrayList<>();
        Organisation leadOrganisation = organisationRepositoryMock.findOne(application.getLeadOrganisationId());
        leadOrganisation.setOrganisationType(businessOrganisationType);
        organisations.add(leadOrganisation);
        leadOrganisation.setOrganisationType(businessOrganisationType);
        organisations.add(newOrganisation().withOrganisationType(businessOrganisationType).build());
        organisations.add(newOrganisation().withOrganisationType(academicOrganisationType).build());

        /**
         * Create 3 users project partner roles for each of the 3 organisations above
         */
        List<User> users = newUser().build(3);
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_PARTNER).withUser(users.get(0), users.get(1), users.get(2)).withOrganisation(organisations.get(0), organisations.get(1), organisations.get(2)).build(3);

        /**
         * Create a project with 3 Project Users from 3 different organisations with an associated application
         */
        Project p = newProject().withProjectUsers(pu).withApplication(application).build();

        /**
         * Create 3 bank detail records, one for each organisation
         */
        List<BankDetails> bankDetails = newBankDetails().withOrganisation(organisations.get(0), organisations.get(1), organisations.get(2)).build(3);

        /**
         * Build spend profile object for use with one of the partners
         */
        SpendProfile spendProfile = newSpendProfile().build();

        /**
         * Create Finance Check information for each Organisation
         */
        List<PartnerOrganisation> partnerOrganisations = simpleMap(organisations, org ->
                newPartnerOrganisation().withProject(p).withOrganisation(org).build());

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);

        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), organisations.get(0).getId())).thenReturn(bankDetails.get(0));

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), organisations.get(0).getId())).thenReturn(Optional.of(spendProfile));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), organisations.get(1).getId())).thenReturn(Optional.of(spendProfile));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), organisations.get(2).getId())).thenReturn(Optional.of(spendProfile));

        MonitoringOfficer monitoringOfficerInDB = MonitoringOfficerBuilder.newMonitoringOfficer().build();
        when(monitoringOfficerRepositoryMock.findOneByProjectId(p.getId())).thenReturn(monitoringOfficerInDB);

        when(organisationRepositoryMock.findOne(organisations.get(0).getId())).thenReturn(organisations.get(0));
        when(organisationRepositoryMock.findOne(organisations.get(1).getId())).thenReturn(organisations.get(1));
        when(organisationRepositoryMock.findOne(organisations.get(2).getId())).thenReturn(organisations.get(2));

        List<ApplicationFinance> applicationFinances = newApplicationFinance().build(3);
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(p.getApplication().getId(), organisations.get(0).getId())).thenReturn(applicationFinances.get(0));
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(p.getApplication().getId(), organisations.get(1).getId())).thenReturn(applicationFinances.get(1));
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(p.getApplication().getId(), organisations.get(2).getId())).thenReturn(applicationFinances.get(2));

        ApplicationFinanceResource applicationFinanceResource0 = newApplicationFinanceResource().withGrantClaimPercentage(20).withOrganisation(organisations.get(0).getId()).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinances.get(0))).thenReturn(applicationFinanceResource0);

        ApplicationFinanceResource applicationFinanceResource1 = newApplicationFinanceResource().withGrantClaimPercentage(20).withOrganisation(organisations.get(1).getId()).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinances.get(1))).thenReturn(applicationFinanceResource1);

        ApplicationFinanceResource applicationFinanceResource2 = newApplicationFinanceResource().withGrantClaimPercentage(20).withOrganisation(organisations.get(2).getId()).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinances.get(2))).thenReturn(applicationFinanceResource2);

        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(3);

        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(projectUserMapperMock.mapToResource(pu.get(1))).thenReturn(puResource.get(1));
        when(projectUserMapperMock.mapToResource(pu.get(2))).thenReturn(puResource.get(2));

        when(financeRowServiceMock.organisationSeeksFunding(p.getId(), p.getApplication().getId(), organisations.get(0).getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(financeRowServiceMock.organisationSeeksFunding(p.getId(), p.getApplication().getId(), organisations.get(1).getId())).thenReturn(serviceSuccess(Boolean.FALSE));
        when(financeRowServiceMock.organisationSeeksFunding(p.getId(), p.getApplication().getId(), organisations.get(2).getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        partnerOrganisations.forEach(org ->
            when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(org.getProject().getId(),
                    org.getOrganisation().getId())).thenReturn(org));

        when(financeCheckWorkflowHandlerMock.isApproved(partnerOrganisations.get(0))).thenReturn(false);
        when(financeCheckWorkflowHandlerMock.isApproved(partnerOrganisations.get(1))).thenReturn(false);
        when(financeCheckWorkflowHandlerMock.isApproved(partnerOrganisations.get(2))).thenReturn(true);

        when(financeCheckServiceMock.isQueryActionRequired(partnerOrganisations.get(0).getProject().getId(), partnerOrganisations.get(0).getOrganisation().getId())).thenReturn(serviceSuccess(Boolean.FALSE));
        when(financeCheckServiceMock.isQueryActionRequired(partnerOrganisations.get(1).getProject().getId(), partnerOrganisations.get(1).getOrganisation().getId())).thenReturn(serviceSuccess(Boolean.FALSE));
        when(financeCheckServiceMock.isQueryActionRequired(partnerOrganisations.get(2).getProject().getId(), partnerOrganisations.get(2).getOrganisation().getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        ProjectLeadStatusResource expectedLeadPartnerOrganisationStatus = newProjectLeadStatusResource().
                withName(organisations.get(0).getName()).
                withOrganisationType(
                        OrganisationTypeEnum.getFromId(organisations.get(0).getOrganisationType().getId())).
                withOrganisationId(organisations.get(0).getId()).
                withProjectDetailsStatus(ACTION_REQUIRED).
                withMonitoringOfficerStatus(NOT_STARTED).
                withBankDetailsStatus(PENDING).
                withFinanceChecksStatus(PENDING).
                withSpendProfileStatus(NOT_STARTED).
                withOtherDocumentsStatus(ACTION_REQUIRED).
                withGrantOfferStatus(NOT_REQUIRED).
                build();

        List<ProjectPartnerStatusResource> expectedFullPartnerStatuses = newProjectPartnerStatusResource().
                withName(organisations.get(1).getName(), organisations.get(2).getName()).
                withOrganisationType(
                        OrganisationTypeEnum.getFromId(organisations.get(1).getOrganisationType().getId()),
                        OrganisationTypeEnum.getFromId(organisations.get(2).getOrganisationType().getId())).
                withOrganisationId(organisations.get(1).getId(), organisations.get(2).getId()).
                withProjectDetailsStatus(ACTION_REQUIRED, ACTION_REQUIRED).
                withMonitoringOfficerStatus(NOT_REQUIRED, NOT_REQUIRED).
                withBankDetailsStatus(NOT_REQUIRED, NOT_STARTED).
                withFinanceChecksStatus(PENDING, ACTION_REQUIRED).
                withSpendProfileStatus(NOT_STARTED, NOT_STARTED).
                withOtherDocumentsStatus(NOT_REQUIRED, NOT_REQUIRED).
                withGrantOfferStatus(NOT_REQUIRED, NOT_REQUIRED).
                build(2);

        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().
                withProjectLeadStatus(expectedLeadPartnerOrganisationStatus).
                withPartnerStatuses(expectedFullPartnerStatuses).
                build();

        // try without filtering
        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.empty());
        assertTrue(result.isSuccess());
        assertEquals(expectedProjectTeamStatusResource, result.getSuccessObject());

        List<ProjectPartnerStatusResource> expectedPartnerStatusesFilteredOnNonLead = newProjectPartnerStatusResource().
                withName(organisations.get(2).getName()).
                withOrganisationType(
                        OrganisationTypeEnum.getFromId(organisations.get(2).getOrganisationType().getId())).
                withOrganisationId(organisations.get(2).getId()).
                withProjectDetailsStatus(ACTION_REQUIRED).
                withMonitoringOfficerStatus(NOT_REQUIRED).
                withBankDetailsStatus(NOT_STARTED).
                withFinanceChecksStatus(ACTION_REQUIRED).
                withSpendProfileStatus(NOT_STARTED).
                withOtherDocumentsStatus(NOT_REQUIRED).
                withGrantOfferStatus(NOT_REQUIRED).
                build(1);

        // try with filtering on a non-lead partner organisation
        ProjectTeamStatusResource expectedProjectTeamStatusResourceFilteredOnNonLead = newProjectTeamStatusResource().
                withProjectLeadStatus(expectedLeadPartnerOrganisationStatus).
                withPartnerStatuses(expectedPartnerStatusesFilteredOnNonLead).
                build();

        ServiceResult<ProjectTeamStatusResource> resultWithNonLeadFilter = service.getProjectTeamStatus(p.getId(), Optional.of(users.get(2).getId()));
        assertTrue(resultWithNonLeadFilter.isSuccess());
        assertEquals(expectedProjectTeamStatusResourceFilteredOnNonLead, resultWithNonLeadFilter.getSuccessObject());

        // try with filtering on a lead partner organisation
        ProjectTeamStatusResource expectedProjectTeamStatusResourceFilteredOnLead = newProjectTeamStatusResource().
                withProjectLeadStatus(expectedLeadPartnerOrganisationStatus).
                build();

        ServiceResult<ProjectTeamStatusResource> resultWithLeadFilter = service.getProjectTeamStatus(p.getId(), Optional.of(users.get(0).getId()));
        assertTrue(resultWithLeadFilter.isSuccess());
        assertEquals(expectedProjectTeamStatusResourceFilteredOnLead, resultWithLeadFilter.getSuccessObject());


        // test MO status is pending and not action required when project details submitted
        when(projectDetailsWorkflowHandlerMock.isSubmitted(any(Project.class))).thenReturn(true);
        when(monitoringOfficerRepositoryMock.findOneByProjectId(p.getId())).thenReturn(null);

        ProjectLeadStatusResource expectedLeadPartnerOrganisationStatusWhenPDSubmitted = newProjectLeadStatusResource().
                withName(organisations.get(0).getName()).
                withOrganisationType(
                        OrganisationTypeEnum.getFromId(organisations.get(0).getOrganisationType().getId())).
                withOrganisationId(organisations.get(0).getId()).
                withProjectDetailsStatus(COMPLETE).
                withMonitoringOfficerStatus(PENDING).
                withBankDetailsStatus(PENDING).
                withFinanceChecksStatus(PENDING).
                withSpendProfileStatus(NOT_STARTED).
                withOtherDocumentsStatus(ACTION_REQUIRED).
                withGrantOfferStatus(NOT_REQUIRED).
                build();

        ProjectTeamStatusResource expectedProjectTeamStatusResourceWhenPSSubmitted = newProjectTeamStatusResource().
                withProjectLeadStatus(expectedLeadPartnerOrganisationStatusWhenPDSubmitted).
                withPartnerStatuses(expectedFullPartnerStatuses).
                build();

        ServiceResult<ProjectTeamStatusResource> resultForPSSubmmited = service.getProjectTeamStatus(p.getId(), Optional.empty());
        assertTrue(resultForPSSubmmited.isSuccess());
        assertEquals(expectedProjectTeamStatusResourceWhenPSSubmitted, resultForPSSubmmited.getSuccessObject());
    }

    @Test
    public void testSendGrantOfferLetterNoGol(){

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(u).withOrganisation(o).withInvite(newInvite().build()).build(1);
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).withGrantOfferLetter(null).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(notificationServiceMock.sendNotification(any(), eq(EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isFailure());
    }

    @Test
    public void testSendGrantOfferLetterSendFails(){

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(u).withOrganisation(o).withInvite(newInvite().build()).build(1);
        FileEntry golFile = newFileEntry().withMediaType("application/pdf").withFilesizeBytes(10).build();
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).withGrantOfferLetter(golFile).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/dashboard"
        );

        when(projectEmailService.sendEmail(singletonList(to), expectedNotificationArguments, ProjectServiceImpl.Notifications.GRANT_OFFER_LETTER_PROJECT_MANAGER)).thenReturn(serviceFailure(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE));

        ServiceResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isFailure());
    }

    @Test
    public void testSendGrantOfferLetterNoProject(){

        when(projectRepositoryMock.findOne(projectId)).thenReturn(null);

        ServiceResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isFailure());
    }
    @Test
    public void testSendGrantOfferLetterSuccess(){

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(u).withOrganisation(o).withInvite(newInvite().build()).build(1);
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).withGrantOfferLetter(golFile).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/dashboard"
        );

        when(projectEmailService.sendEmail(singletonList(to), expectedNotificationArguments, ProjectServiceImpl.Notifications.GRANT_OFFER_LETTER_PROJECT_MANAGER)).thenReturn(serviceSuccess());

        User user = UserBuilder.newUser().build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        when(golWorkflowHandlerMock.grantOfferLetterSent(p, user)).thenReturn(Boolean.TRUE);

        ServiceResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testSendGrantOfferLetterFailure(){

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(u).withOrganisation(o).withInvite(newInvite().build()).build(1);
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).withGrantOfferLetter(golFile).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/dashboard"
        );

        when(projectEmailService.sendEmail(singletonList(to), expectedNotificationArguments, ProjectServiceImpl.Notifications.GRANT_OFFER_LETTER_PROJECT_MANAGER)).thenReturn(serviceSuccess());

        User user = UserBuilder.newUser().build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        when(golWorkflowHandlerMock.grantOfferLetterSent(p, user)).thenReturn(Boolean.FALSE);

        ServiceResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isFailure());
    }


    @Test
    public void testIsSendGrantOfferLetterAllowed() {

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(golWorkflowHandlerMock.isSendAllowed(p)).thenReturn(Boolean.TRUE);

        ServiceResult<Boolean> result = service.isSendGrantOfferLetterAllowed(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testIsSendGrantOfferLetterAllowedFails() {

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(golWorkflowHandlerMock.isSendAllowed(p)).thenReturn(Boolean.FALSE);

        ServiceResult<Boolean> result = service.isSendGrantOfferLetterAllowed(projectId);

        assertTrue(result.isSuccess() && Boolean.FALSE == result.getSuccessObject());
    }

    @Test
    public void testIsSendGrantOfferLetterAllowedNoProject() {

        when(projectRepositoryMock.findOne(projectId)).thenReturn(null);

        ServiceResult<Boolean> result = service.isSendGrantOfferLetterAllowed(projectId);

        assertTrue(result.isFailure());
    }

    @Test
    public void testIsGrantOfferLetterAlreadySent() {

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(golWorkflowHandlerMock.isAlreadySent(p)).thenReturn(Boolean.TRUE);

        ServiceResult<Boolean> result = service.isGrantOfferLetterAlreadySent(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testIsGrantOfferLetterAlreadySentFails() {

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(golWorkflowHandlerMock.isAlreadySent(p)).thenReturn(Boolean.FALSE);

        ServiceResult<Boolean> result = service.isGrantOfferLetterAlreadySent(projectId);

        assertTrue(result.isSuccess() && Boolean.FALSE == result.getSuccessObject());
    }

    @Test
    public void testIsGrantOfferLetterActionRequired() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(o).withInvite(newInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(Boolean.TRUE).build(1);
        Project p = newProject().withProjectUsers(pu).withApplication(application).withPartnerOrganisations(po).withDateSubmitted(ZonedDateTime.now()).withOtherDocumentsApproved(ApprovalType.APPROVED).withGrantOfferLetter(golFile).build();
        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(o.getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(1);

        BankDetails bankDetails = newBankDetails().withOrganisation(o).withApproval(Boolean.TRUE).build();
        SpendProfile spendProfile = newSpendProfile().withOrganisation(o).withMarkedComplete(Boolean.TRUE).withApproval(ApprovalType.APPROVED).build();

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(Boolean.FALSE));
        when(golWorkflowHandlerMock.isAlreadySent(p)).thenReturn(Boolean.TRUE);

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && ACTION_REQUIRED.equals(result.getSuccessObject().getLeadPartnerStatus().getGrantOfferLetterStatus()));
    }

    @Test
    public void testIsGrantOfferLetterIsPendingLeadPartner() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(o).withInvite(newInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(Boolean.TRUE).build(1);
        Project p = newProject().withProjectUsers(pu).withApplication(application).withPartnerOrganisations(po).withDateSubmitted(ZonedDateTime.now()).withOtherDocumentsApproved(ApprovalType.APPROVED).withGrantOfferLetter(golFile).withSignedGrantOfferLetter(golFile).build();
        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(o.getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(1);

        BankDetails bankDetails = newBankDetails().withOrganisation(o).withApproval(Boolean.TRUE).build();
        SpendProfile spendProfile = newSpendProfile().withOrganisation(o).withMarkedComplete(Boolean.TRUE).withApproval(ApprovalType.APPROVED).build();

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(Boolean.FALSE));
        when(golWorkflowHandlerMock.isAlreadySent(p)).thenReturn(Boolean.FALSE);

        ServiceResult<ProjectTeamStatusResource> resultWhenGolIsNotSent = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(resultWhenGolIsNotSent.isSuccess() && PENDING.equals(resultWhenGolIsNotSent.getSuccessObject().getLeadPartnerStatus().getGrantOfferLetterStatus()));

        // Same flow but when GOL is in Ready To Approve state.
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(Boolean.TRUE);

        // Call the service again
        ServiceResult<ProjectTeamStatusResource> resultWhenGolIsReadyToApprove = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(resultWhenGolIsReadyToApprove.isSuccess() && PENDING.equals(resultWhenGolIsReadyToApprove.getSuccessObject().getLeadPartnerStatus().getGrantOfferLetterStatus()));
    }

    @Test
    public void testIsGrantOfferLetterIsPendingNonLeadPartner() {

        Role partnerRole = newRole().withType(FINANCE_CONTACT).build();
        User u = newUser().withEmailAddress("a@b.com").build();

        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation o = organisationRepositoryMock.findOne(application.getLeadOrganisationId());
        o.setOrganisationType(businessOrganisationType);

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        Organisation nonLeadOrg = newOrganisation().build();
        nonLeadOrg.setOrganisationType(businessOrganisationType);

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(nonLeadOrg).withInvite(newInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(nonLeadOrg).withLeadOrganisation(Boolean.FALSE).build(1);
        Project p = newProject().withProjectUsers(pu).withApplication(application).withPartnerOrganisations(po).withOtherDocumentsApproved(ApprovalType.APPROVED).withGrantOfferLetter(golFile).withSignedGrantOfferLetter(golFile).withDateSubmitted(ZonedDateTime.now()).build();
        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(nonLeadOrg.getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(1);

        BankDetails bankDetails = newBankDetails().withOrganisation(o).withApproval(Boolean.TRUE).build();
        SpendProfile spendProfile = newSpendProfile().withOrganisation(o).withMarkedComplete(Boolean.TRUE).withApproval(ApprovalType.APPROVED).build();

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), nonLeadOrg.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(anyLong(), anyLong())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), nonLeadOrg.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(golWorkflowHandlerMock.isAlreadySent(p)).thenReturn(Boolean.TRUE);

        // Same flow but when GOL is in Ready To Approve state.
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(Boolean.TRUE);

        when(financeRowServiceMock.organisationSeeksFunding(p.getId(), p.getApplication().getId(), o.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(financeRowServiceMock.organisationSeeksFunding(p.getId(), p.getApplication().getId(), nonLeadOrg.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(financeCheckServiceMock.isQueryActionRequired(anyLong(),anyLong())).thenReturn(serviceSuccess(Boolean.FALSE));


        // Call the service again
        ServiceResult<ProjectTeamStatusResource> resultWhenGolIsReadyToApprove = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(resultWhenGolIsReadyToApprove.isSuccess() && PENDING.equals(resultWhenGolIsReadyToApprove.getSuccessObject().getPartnerStatuses().get(0).getGrantOfferLetterStatus()));

    }

    @Test
    public void testIsGrantOfferLetterComplete() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(o).withInvite(newInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(Boolean.TRUE).build(1);
        Project p = newProject().withProjectUsers(pu).withApplication(application).withPartnerOrganisations(po).withDateSubmitted(ZonedDateTime.now()).withOtherDocumentsApproved(ApprovalType.APPROVED).withGrantOfferLetter(golFile).withSignedGrantOfferLetter(golFile).withOfferSubmittedDate(ZonedDateTime.now()).build();
        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(o.getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(1);

        spendProfile.setApproval(ApprovalType.APPROVED);

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(golWorkflowHandlerMock.isAlreadySent(p)).thenReturn(Boolean.TRUE);
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(Boolean.TRUE);
        when(golWorkflowHandlerMock.isApproved(p)).thenReturn(Boolean.TRUE);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && COMPLETE.equals(result.getSuccessObject().getLeadPartnerStatus().getGrantOfferLetterStatus()));
    }

    @Test
    public void testIsGrantOfferLetterAlreadySentNoProject() {

        when(projectRepositoryMock.findOne(projectId)).thenReturn(null);

        ServiceResult<Boolean> result = service.isGrantOfferLetterAlreadySent(projectId);

        assertTrue(result.isFailure());
    }

    @Test
    public void testApproveSignedGrantOfferLetterSuccess(){

        User u = newUser().withFirstName("A").withLastName("B").withEmailAddress("a@b.com").build();
        setLoggedInUser(newUserResource().withId(u.getId()).build());
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(u).withOrganisation(o).withInvite(newInvite().build()).build(1);
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).build();

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.findOne(u.getId())).thenReturn(u);
        when(golWorkflowHandlerMock.grantOfferLetterApproved(p, u)).thenReturn(Boolean.TRUE);
        when(projectWorkflowHandlerMock.grantOfferLetterApproved(p, p.getProjectUsersWithRole(PROJECT_MANAGER).get(0))).thenReturn(Boolean.TRUE);
        when(projectEmailService.sendEmail(singletonList(to), emptyMap(), ProjectServiceImpl.Notifications.PROJECT_LIVE)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, ApprovalType.APPROVED);

        verify(projectRepositoryMock, atLeast(2)).findOne(projectId);
        verify(golWorkflowHandlerMock).isReadyToApprove(p);
        verify(golWorkflowHandlerMock).grantOfferLetterApproved(p, u);
        verify(projectWorkflowHandlerMock).grantOfferLetterApproved(p, p.getProjectUsersWithRole(PROJECT_MANAGER).get(0));
        verify(projectEmailService).sendEmail(singletonList(to), emptyMap(), ProjectServiceImpl.Notifications.PROJECT_LIVE);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testDuplicateEmailsAreNotSent(){

        User u = newUser().withFirstName("A").withLastName("B").withEmailAddress("a@b.com").build();
        setLoggedInUser(newUserResource().withId(u.getId()).build());
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(u).withOrganisation(o).withInvite(newInvite().build()).build(1);
        List<ProjectUser> fc = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(o).withInvite(newInvite().build()).build(1);
        pu.addAll(fc);
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).build();

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.findOne(u.getId())).thenReturn(u);
        when(golWorkflowHandlerMock.grantOfferLetterApproved(p, u)).thenReturn(Boolean.TRUE);
        when(projectWorkflowHandlerMock.grantOfferLetterApproved(p, p.getProjectUsersWithRole(PROJECT_MANAGER).get(0))).thenReturn(Boolean.TRUE);
        when(projectEmailService.sendEmail(singletonList(to), emptyMap(), ProjectServiceImpl.Notifications.PROJECT_LIVE)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, ApprovalType.APPROVED);

        verify(projectRepositoryMock, atLeast(2)).findOne(projectId);
        verify(golWorkflowHandlerMock).isReadyToApprove(p);
        verify(golWorkflowHandlerMock).grantOfferLetterApproved(p, u);
        verify(projectWorkflowHandlerMock).grantOfferLetterApproved(p, p.getProjectUsersWithRole(PROJECT_MANAGER).get(0));
        when(projectEmailService.sendEmail(singletonList(to), emptyMap(), ProjectServiceImpl.Notifications.PROJECT_LIVE)).thenReturn(serviceSuccess());

        assertTrue(result.isSuccess());
    }

    @Test
    public void testApproveSignedGrantOfferLetterFailure(){
        User u = newUser().withFirstName("A").withLastName("B").withEmailAddress("a@b.com").build();
        setLoggedInUser(newUserResource().withId(u.getId()).build());

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        p.setGrantOfferLetter(golFile);

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.findOne(u.getId())).thenReturn(u);
        when(golWorkflowHandlerMock.grantOfferLetterApproved(p, u)).thenReturn(Boolean.FALSE);

        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, ApprovalType.APPROVED);

        verify(projectRepositoryMock).findOne(projectId);
        verify(golWorkflowHandlerMock).isReadyToApprove(p);
        verify(golWorkflowHandlerMock).grantOfferLetterApproved(p, u);
        verify(projectWorkflowHandlerMock, never()).grantOfferLetterApproved(any(), any());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR));
    }

    @Test
    public void testApproveSignedGrantOfferLetterFailureNotReadyToApprove(){

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        p.setGrantOfferLetter(golFile);

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(Boolean.FALSE);

        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, ApprovalType.APPROVED);

        verify(projectRepositoryMock).findOne(projectId);
        verify(golWorkflowHandlerMock).isReadyToApprove(p);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GRANT_OFFER_LETTER_NOT_READY_TO_APPROVE));
    }

    @Test
    public void testGetSignedGrantOfferLetterApprovalStatusSuccess(){

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(golWorkflowHandlerMock.isApproved(p)).thenReturn(Boolean.TRUE);

        ServiceResult<Boolean> result = service.isSignedGrantOfferLetterApproved(projectId);

        verify(projectRepositoryMock).findOne(projectId);
        verify(golWorkflowHandlerMock).isApproved(p);

        assertTrue(result.isSuccess() && Boolean.TRUE == result.getSuccessObject());
    }

    @Test
    public void testGetSignedGrantOfferLetterApprovalStatusFailure(){

        when(projectRepositoryMock.findOne(projectId)).thenReturn(p);
        when(golWorkflowHandlerMock.isApproved(p)).thenReturn(Boolean.FALSE);

        ServiceResult<Boolean> result = service.isSignedGrantOfferLetterApproved(projectId);

        verify(projectRepositoryMock).findOne(projectId);
        verify(golWorkflowHandlerMock).isApproved(p);

        assertTrue(result.isSuccess() && Boolean.FALSE == result.getSuccessObject());
    }

    @Test
    public void testGetGrantOfferLetterWorkflowStateWhenProjectDoesNotExist() {

        when(projectRepositoryMock.findOne(projectId)).thenReturn(null);

        ServiceResult<GOLState> result = service.getGrantOfferLetterWorkflowState(projectId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonErrors.notFoundError(Project.class, projectId)));
    }

    @Test
    public void testGetGrantOfferLetterWorkflowState() {

        Project projectInDB = newProject().build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(golWorkflowHandlerMock.getState(projectInDB)).thenReturn(GOLState.APPROVED);

        ServiceResult<GOLState> result = service.getGrantOfferLetterWorkflowState(projectId);

        assertTrue(result.isSuccess());
        assertEquals(GOLState.APPROVED, result.getSuccessObject());

    }

    private void assertFilesCannotBeSubmittedIfNotByProjectManager(Consumer<FileEntry> fileSetter1,
                                                                   Consumer<FileEntry> fileSetter2,
                                                                   Supplier<ServiceResult<Boolean>> getConditionFn) {
        List<ProjectUser> projectUsers = new ArrayList<>();
        Arrays.stream(ProjectParticipantRole.values())
                .filter(roleType -> roleType != PROJECT_MANAGER)
                .forEach(roleType -> {
                    ProjectUser projectUser = newProjectUser()
                            .withId(3L)
                            .withRole(roleType)
                            .build();
                    projectUsers.add(projectUser);

                });

        when(projectUserRepositoryMock.findByProjectId(123L)).thenReturn(projectUsers);

        Supplier<InputStream> inputStreamSupplier1 = () -> null;
        Supplier<InputStream> inputStreamSupplier2 = () -> null;

        getFileEntryResources(fileSetter1, fileSetter2, inputStreamSupplier1, inputStreamSupplier2);
        ServiceResult<Boolean> result = getConditionFn.get();

        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());

    }


    private void assertFilesCanBeSubmittedByProjectManagerAndFilesExist(Consumer<FileEntry> fileSetter1,
                                                                        Consumer<FileEntry> fileSetter2,
                                                                        Supplier<ServiceResult<Boolean>> getConditionFn) {
        ProjectUser projectUserToSet = newProjectUser()
                .withId(1L)
                .withUser(newUser().withId(1L).build())
                .withRole(PROJECT_MANAGER)
                .build();

        project.addProjectUser(projectUserToSet);

        Supplier<InputStream> inputStreamSupplier1 = () -> null;
        Supplier<InputStream> inputStreamSupplier2 = () -> null;

        getFileEntryResources(fileSetter1, fileSetter2, inputStreamSupplier1, inputStreamSupplier2);
        ServiceResult<Boolean> result = getConditionFn.get();

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccessObject());

    }

    private List<FileEntryResource> getFileEntryResources(Consumer<FileEntry> fileSetter1, Consumer<FileEntry> fileSetter2,
                                                          Supplier<InputStream> inputStreamSupplier1,
                                                          Supplier<InputStream> inputStreamSupplier2) {
        FileEntry fileEntry1ToGet = newFileEntry().build();
        FileEntry fileEntry2ToGet = newFileEntry().build();

        List<FileEntryResource> fileEntryResourcesToGet = newFileEntryResource().withFilesizeBytes(100).build(2);

        fileSetter1.accept(fileEntry1ToGet);
        fileSetter2.accept(fileEntry2ToGet);

        when(fileServiceMock.getFileByFileEntryId(fileEntry1ToGet.getId())).thenReturn(serviceSuccess(inputStreamSupplier1));
        when(fileServiceMock.getFileByFileEntryId(fileEntry2ToGet.getId())).thenReturn(serviceSuccess(inputStreamSupplier2));

        when(fileEntryMapperMock.mapToResource(fileEntry1ToGet)).thenReturn(fileEntryResourcesToGet.get(0));
        when(fileEntryMapperMock.mapToResource(fileEntry2ToGet)).thenReturn(fileEntryResourcesToGet.get(1));
        return fileEntryResourcesToGet;
    }

    private void assertDeleteFile(Supplier<FileEntry> fileGetter, Consumer<FileEntry> fileSetter, Supplier<ServiceResult<Void>> deleteFileFn) {
        FileEntry fileToDelete = newFileEntry().build();

        fileSetter.accept(fileToDelete);
        when(fileServiceMock.deleteFile(fileToDelete.getId())).thenReturn(serviceSuccess(fileToDelete));

        ServiceResult<Void> result = deleteFileFn.get();
        assertTrue(result.isSuccess());
        assertNull(fileGetter.get());

        verify(fileServiceMock).deleteFile(fileToDelete.getId());
    }

    private Project createProjectExpectationsFromOriginalApplication() {

        assertFalse(application.getProcessRoles().isEmpty());

        return createLambdaMatcher(project -> {
            assertEquals(application.getName(), project.getName());
            assertEquals(application.getDurationInMonths(), project.getDurationInMonths());
            assertEquals(application.getStartDate(), project.getTargetStartDate());
            assertFalse(project.getProjectUsers().isEmpty());
            assertNull(project.getAddress());

            List<ProcessRole> collaborativeRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isLeadApplicantOrCollaborator);

            assertEquals(collaborativeRoles.size(), project.getProjectUsers().size());

            collaborativeRoles.forEach(processRole -> {

                List<ProjectUser> matchingProjectUser = simpleFilter(project.getProjectUsers(), projectUser ->
                        projectUser.getOrganisation().getId().equals(processRole.getOrganisationId()) &&
                                projectUser.getUser().equals(processRole.getUser()));

                assertEquals(1, matchingProjectUser.size());
                assertEquals(PARTNER.getName(), matchingProjectUser.get(0).getRole().getName());
                assertEquals(project, matchingProjectUser.get(0).getProcess());
            });

            List<PartnerOrganisation> partnerOrganisations = project.getPartnerOrganisations();
            assertEquals(1, partnerOrganisations.size());
            assertEquals(project, partnerOrganisations.get(0).getProject());
            assertEquals(organisation, partnerOrganisations.get(0).getOrganisation());
            assertTrue(partnerOrganisations.get(0).isLeadOrganisation());
        });
    }

    @Test
    public void testSpendProfileNotComplete() {

        spendProfile.setMarkedAsComplete(false);

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && ACTION_REQUIRED.equals(result.getSuccessObject().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void testSpendProfileRequiresEligibility() {

        p.setSpendProfileSubmittedDate(null);

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.REVIEW);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && NOT_STARTED.equals(result.getSuccessObject().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void testSpendProfileRequiresViability() {

        p.setSpendProfileSubmittedDate(null);

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.REVIEW);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && NOT_STARTED.equals(result.getSuccessObject().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void testSpendProfileNotSubmittedViabilityNotApplicable() {

        p.setSpendProfileSubmittedDate(null);

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.NOT_APPLICABLE);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && ACTION_REQUIRED.equals(result.getSuccessObject().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void testSpendProfileCompleteNotSubmitted() {

        p.setSpendProfileSubmittedDate(null);

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && ACTION_REQUIRED.equals(result.getSuccessObject().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void testSpendProfileCompleteSubmitted() {

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && PENDING.equals(result.getSuccessObject().getLeadPartnerStatus().getSpendProfileStatus()));
    }


    @Test
    public void testSpendProfileCompleteRejected() {
        spendProfile.setApproval(ApprovalType.REJECTED);

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && ACTION_REQUIRED.equals(result.getSuccessObject().getLeadPartnerStatus().getSpendProfileStatus()));
        assertTrue(project.getSpendProfileSubmittedDate() == null);
    }

    @Test
    public void testGetProjectManager() {
        final Long projectId = 123L;
        final Project project = newProject().withId(projectId).build();
        final ProjectUser projectManager = newProjectUser().withProject(project).withRole(PROJECT_MANAGER).build();
        final ProjectUserResource projectManagerResource = newProjectUserResource().withProject(projectId).withRoleName(PROJECT_MANAGER.getName()).build();

        when(projectUserMapperMock.mapToResource(projectManager)).thenReturn(projectManagerResource);
        when(projectUserRepositoryMock.findByProjectIdAndRole(projectId, PROJECT_MANAGER)).thenReturn(projectManager);

        ServiceResult<ProjectUserResource> foundProjectManager = service.getProjectManager(projectId);
        assertTrue(foundProjectManager.isSuccess());
        assertTrue(foundProjectManager.getSuccessObject().getRoleName().equals(PROJECT_MANAGER.getName()));
        assertTrue(foundProjectManager.getSuccessObject().getProject().equals(projectId));
    }

    @Override
    protected ProjectService supplyServiceUnderTest() {

        ProjectServiceImpl projectService =  new ProjectServiceImpl();
        ReflectionTestUtils.setField(projectService, "webBaseUrl", webBaseUrl);
        return projectService;
    }
}
