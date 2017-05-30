package org.innovateuk.ifs.project.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.transactional.FinanceChecksGenerator;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.spendprofile.transactional.CostCategoryTypeStrategy;
import org.innovateuk.ifs.user.domain.*;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectInviteBuilder.newProjectInvite;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectServiceImplTest extends BaseServiceUnitTest<ProjectService> {

    @Mock
    private CostCategoryTypeStrategy costCategoryTypeStrategyMock;

    @Mock
    private FinanceChecksGenerator financeChecksGeneratorMock;

    private Long applicationId = 456L;
    private Long userId = 7L;

    private Application application;
    private Organisation organisation;
    private Role leadApplicantRole;
    private User user;
    private User u;
    private ProcessRole leadApplicantProcessRole;
    private ProjectUser leadPartnerProjectUser;
    private List<PartnerOrganisation> po;
    private List<ProjectUser> pu;
    private Organisation o;
    private Project p;

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

        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        o = organisation;
        o.setOrganisationType(businessOrganisationType);

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

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
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
