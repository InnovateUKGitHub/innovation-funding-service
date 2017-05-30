package org.innovateuk.ifs.project.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.builder.MonitoringOfficerBuilder;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.transactional.FinanceChecksGenerator;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.transactional.CostCategoryTypeStrategy;
import org.innovateuk.ifs.user.domain.*;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.invite.builder.ProjectInviteBuilder.newProjectInvite;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsBuilder.newBankDetails;
import static org.innovateuk.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectServiceImplTest extends BaseServiceUnitTest<ProjectService> {

    @Mock
    private CostCategoryTypeStrategy costCategoryTypeStrategyMock;

    @Mock
    private FinanceChecksGenerator financeChecksGeneratorMock;

    private Long projectId = 123L;
    private Long applicationId = 456L;
    private Long userId = 7L;

    private Application application;
    private Organisation organisation;
    private Role leadApplicantRole;
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
    private BankDetails bankDetails;
    private SpendProfile spendProfile;

    @Before
    public void setUp() {

        organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        leadApplicantRole = newRole(LEADAPPLICANT).build();

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

        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        o = organisation;
        o.setOrganisationType(businessOrganisationType);

        partnerRole = newRole().
                withType(FINANCE_CONTACT).
                build();

        po = newPartnerOrganisation().
                withOrganisation(o).
                withLeadOrganisation(TRUE).
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
                withInvite(newProjectInvite().
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

        bankDetails = newBankDetails().withOrganisation(o).withApproval(TRUE).build();
        spendProfile = newSpendProfile().withOrganisation(o).withGeneratedDate(Calendar.getInstance()).withMarkedComplete(TRUE).withApproval(ApprovalType.EMPTY).build();

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
        verify(golWorkflowHandlerMock, never()).projectCreated(any(Project.class), any(ProjectUser.class));
        verify(projectWorkflowHandlerMock, never()).projectCreated(any(Project.class), any(ProjectUser.class));

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
        List<ProjectInvite> projectInvites = newProjectInvite().withUser(user).build(1);
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

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), organisations.get(0).getId())).thenReturn(Optional.empty());
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), organisations.get(1).getId())).thenReturn(Optional.empty());
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), organisations.get(2).getId())).thenReturn(Optional.empty());

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

        when(financeRowServiceMock.organisationSeeksFunding(p.getId(), p.getApplication().getId(), organisations.get(0).getId())).thenReturn(serviceSuccess(TRUE));
        when(financeRowServiceMock.organisationSeeksFunding(p.getId(), p.getApplication().getId(), organisations.get(1).getId())).thenReturn(serviceSuccess(FALSE));
        when(financeRowServiceMock.organisationSeeksFunding(p.getId(), p.getApplication().getId(), organisations.get(2).getId())).thenReturn(serviceSuccess(TRUE));

        partnerOrganisations.forEach(org ->
                when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(org.getProject().getId(),
                        org.getOrganisation().getId())).thenReturn(org));

        when(financeCheckServiceMock.isQueryActionRequired(partnerOrganisations.get(0).getProject().getId(), partnerOrganisations.get(0).getOrganisation().getId())).thenReturn(serviceSuccess(FALSE));
        when(financeCheckServiceMock.isQueryActionRequired(partnerOrganisations.get(1).getProject().getId(), partnerOrganisations.get(1).getOrganisation().getId())).thenReturn(serviceSuccess(FALSE));
        when(financeCheckServiceMock.isQueryActionRequired(partnerOrganisations.get(2).getProject().getId(), partnerOrganisations.get(2).getOrganisation().getId())).thenReturn(serviceSuccess(TRUE));

        when(golWorkflowHandlerMock.getState(p)).thenReturn(GrantOfferLetterState.PENDING);

        ProjectPartnerStatusResource expectedLeadPartnerOrganisationStatus = newProjectPartnerStatusResource().
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
                withIsLeadPartner(true).
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

        ProjectPartnerStatusResource expectedLeadPartnerOrganisationStatusWhenPDSubmitted = newProjectPartnerStatusResource().
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
                withIsLeadPartner(true).
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
    public void testIsGrantOfferLetterActionRequired() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(o).withInvite(newProjectInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(TRUE).build(1);
        Project p = newProject().withProjectUsers(pu).withApplication(application).withPartnerOrganisations(po).withDateSubmitted(ZonedDateTime.now()).withOtherDocumentsApproved(ApprovalType.APPROVED).withGrantOfferLetter(golFile).build();
        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(o.getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(1);

        BankDetails bankDetails = newBankDetails().withOrganisation(o).withApproval(TRUE).build();
        SpendProfile spendProfile = newSpendProfile().withOrganisation(o).withMarkedComplete(TRUE).withApproval(ApprovalType.APPROVED).build();

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(FALSE));
        when(golWorkflowHandlerMock.getState(p)).thenReturn(GrantOfferLetterState.SENT);

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && ACTION_REQUIRED.equals(result.getSuccessObject().getLeadPartnerStatus().getGrantOfferLetterStatus()));
    }

    @Test
    public void testIsGrantOfferLetterIsPendingLeadPartner() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(o).withInvite(newProjectInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(TRUE).build(1);
        Project p = newProject().withProjectUsers(pu).withApplication(application).withPartnerOrganisations(po).withDateSubmitted(ZonedDateTime.now()).withOtherDocumentsApproved(ApprovalType.APPROVED).withGrantOfferLetter(golFile).withSignedGrantOfferLetter(golFile).build();
        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(o.getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(1);

        BankDetails bankDetails = newBankDetails().withOrganisation(o).withApproval(TRUE).build();
        SpendProfile spendProfile = newSpendProfile().withOrganisation(o).withMarkedComplete(TRUE).withApproval(ApprovalType.APPROVED).build();

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(FALSE));
        when(golWorkflowHandlerMock.isAlreadySent(p)).thenReturn(FALSE);

        ServiceResult<ProjectTeamStatusResource> resultWhenGolIsNotSent = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(resultWhenGolIsNotSent.isSuccess() && PENDING.equals(resultWhenGolIsNotSent.getSuccessObject().getLeadPartnerStatus().getGrantOfferLetterStatus()));

        // Same flow but when GOL is in Ready To Approve state.
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(TRUE);

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

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(nonLeadOrg).withInvite(newProjectInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(nonLeadOrg).withLeadOrganisation(FALSE).build(1);
        Project p = newProject().withProjectUsers(pu).withApplication(application).withPartnerOrganisations(po).withOtherDocumentsApproved(ApprovalType.APPROVED).withGrantOfferLetter(golFile).withSignedGrantOfferLetter(golFile).withDateSubmitted(ZonedDateTime.now()).build();
        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(nonLeadOrg.getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(1);

        BankDetails bankDetails = newBankDetails().withOrganisation(o).withApproval(TRUE).build();
        SpendProfile spendProfile = newSpendProfile().withOrganisation(o).withMarkedComplete(TRUE).withApproval(ApprovalType.APPROVED).build();

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), nonLeadOrg.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(anyLong(), anyLong())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), nonLeadOrg.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(golWorkflowHandlerMock.isAlreadySent(p)).thenReturn(TRUE);

        // Same flow but when GOL is in Ready To Approve state.
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(TRUE);

        when(financeRowServiceMock.organisationSeeksFunding(p.getId(), p.getApplication().getId(), o.getId())).thenReturn(serviceSuccess(TRUE));
        when(financeRowServiceMock.organisationSeeksFunding(p.getId(), p.getApplication().getId(), nonLeadOrg.getId())).thenReturn(serviceSuccess(TRUE));
        when(financeCheckServiceMock.isQueryActionRequired(anyLong(),anyLong())).thenReturn(serviceSuccess(FALSE));


        // Call the service again
        ServiceResult<ProjectTeamStatusResource> resultWhenGolIsReadyToApprove = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(resultWhenGolIsReadyToApprove.isSuccess() && PENDING.equals(resultWhenGolIsReadyToApprove.getSuccessObject().getPartnerStatuses().get(0).getGrantOfferLetterStatus()));

    }

    @Test
    public void testIsGrantOfferLetterComplete() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(o).withInvite(newProjectInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(TRUE).build(1);
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
        when(golWorkflowHandlerMock.getState(p)).thenReturn(GrantOfferLetterState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && COMPLETE.equals(result.getSuccessObject().getLeadPartnerStatus().getGrantOfferLetterStatus()));
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
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && ACTION_REQUIRED.equals(result.getSuccessObject().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void testSpendProfileRequiresEligibility() {
        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.empty());
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.REVIEW);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && NOT_STARTED.equals(result.getSuccessObject().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void testSpendProfileRequiresViability() {

        when(projectRepositoryMock.findOne(p.getId())).thenReturn(p);
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(bankDetails);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.empty());
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.REVIEW);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(FALSE));

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
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(FALSE));

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
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(FALSE));

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
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(FALSE));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && PENDING.equals(result.getSuccessObject().getLeadPartnerStatus().getSpendProfileStatus()));
    }


    @Test
    public void testSpendProfileCompleteRejected() {
        spendProfile.setApproval(ApprovalType.REJECTED);
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
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(),o.getId())).thenReturn(serviceSuccess(FALSE));

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
        return new ProjectServiceImpl();
    }
}
