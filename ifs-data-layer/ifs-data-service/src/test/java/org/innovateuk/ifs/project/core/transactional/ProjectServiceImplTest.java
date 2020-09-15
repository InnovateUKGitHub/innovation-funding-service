package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.builder.ProjectDocumentBuilder;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectServiceImplTest extends BaseServiceUnitTest<ProjectService> {

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandlerMock;

    @Mock
    private ViabilityWorkflowHandler viabilityWorkflowHandlerMock;

    @Mock
    private EligibilityWorkflowHandler eligibilityWorkflowHandlerMock;

    @Mock
    private GrantOfferLetterWorkflowHandler golWorkflowHandlerMock;

    @Mock
    private ProjectWorkflowHandler projectWorkflowHandlerMock;

    @Mock
    private ProjectMapper projectMapperMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ProjectUserRepository projectUserRepositoryMock;

    @Mock
    private ProjectUserInviteRepository projectUserInviteRepositoryMock;

    @Mock
    private SpendProfileWorkflowHandler spendProfileWorkflowHandlerMock;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private OrganisationMapper organisationMapperMock;

    private long applicationId = 456;

    private Competition competition;
    private Application application;
    private Organisation organisation;
    private User user;
    private User u;
    private ProjectUser leadPartnerProjectUser;
    private Organisation o;
    private Project project;

    @Before
    public void setUp() {

        organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        long userId = 7;
        user = newUser().
                withId(userId).
                build();

        ProcessRole leadApplicantProcessRole = newProcessRole().
                withOrganisationId(organisation.getId()).
                withRole(Role.LEADAPPLICANT).
                withUser(user).
                build();

        leadPartnerProjectUser = newProjectUser().
                withOrganisation(organisation).
                withRole(PROJECT_PARTNER).
                withUser(user).
                build();

        ApplicationFinance applicationFinance = ApplicationFinanceBuilder.newApplicationFinance()
                .withApplication(application)
                .withOrganisation(organisation)
                .withWorkPostcode("UB7 8QF")
                .build();
        List<ApplicationFinance> applicationFinances = singletonList(applicationFinance);

        competition = newCompetition()
                .withSections(newSection().withSectionType(SectionType.FINANCE).build(1))
                .build();

        application = newApplication().
                withId(applicationId).
                withCompetition(competition).
                withProcessRoles(leadApplicantProcessRole).
                withName("My Application").
                withDurationInMonths(5L).
                withStartDate(LocalDate.of(2017, 3, 2)).
                withFundingDecision(FundingDecisionStatus.FUNDED).
                withApplicationFinancesList(applicationFinances).
                build();

        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        o = organisation;
        o.setOrganisationType(businessOrganisationType);

        List<PartnerOrganisation> partnerOrganisations = newPartnerOrganisation().
                withOrganisation(o).
                withLeadOrganisation(TRUE).
                build(1);

        u = newUser().
                withEmailAddress("a@b.com").
                withFirstName("A").
                withLastName("B").
                build();

        List<ProjectUser> projectUsers = newProjectUser().
                withRole(PROJECT_FINANCE_CONTACT).
                withUser(u).
                withOrganisation(o).
                withInvite(newProjectUserInvite().
                        build()).
                build(1);

        project = newProject().
                withProjectUsers(projectUsers).
                withApplication(application).
                withPartnerOrganisations(partnerOrganisations).
                withDateSubmitted(ZonedDateTime.now()).
                withSpendProfileSubmittedDate(ZonedDateTime.now()).
                build();

        ProjectDocument projectDocument = ProjectDocumentBuilder
                .newProjectDocument()
                .withProject(project)
                .withStatus(DocumentStatus.APPROVED)
                .build();
        project.setProjectDocuments(singletonList(projectDocument));

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());
    }

    @Test
    public void createProjectFromApplication() {

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

        Project newProjectExpectations = createProjectExpectationsFromOriginalApplication();
        when(projectRepositoryMock.save(newProjectExpectations)).thenReturn(savedProject);

        CostCategoryType costCategoryTypeForOrganisation = newCostCategoryType().
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(newCostCategory().withName("Cat1", "Cat2").build(2)).
                        build()).
                build();

        when(projectDetailsWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);
        when(viabilityWorkflowHandlerMock.projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser)).thenReturn(true);
        when(eligibilityWorkflowHandlerMock.projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser)).thenReturn(true);
        when(golWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);
        when(projectWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);
        when(spendProfileWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);

        when(projectMapperMock.mapToResource(savedProject)).thenReturn(newProjectResource);

        ServiceResult<ProjectResource> project = service.createProjectFromApplication(applicationId);
        assertTrue(project.isSuccess());
        assertEquals(newProjectResource, project.getSuccess());
        assertNotNull(competition.getProjectSetupStarted());

        verify(projectDetailsWorkflowHandlerMock).projectCreated(savedProject, leadPartnerProjectUser);
        verify(viabilityWorkflowHandlerMock).projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser);
        verify(eligibilityWorkflowHandlerMock).projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser);
        verify(golWorkflowHandlerMock).projectCreated(savedProject, leadPartnerProjectUser);
        verify(projectWorkflowHandlerMock).projectCreated(savedProject, leadPartnerProjectUser);
        verify(projectMapperMock).mapToResource(savedProject);
        verify(activityLogService).recordActivityByApplicationId(applicationId, ActivityType.APPLICATION_INTO_PROJECT_SETUP);
    }

    @Test
    public void createProjectFromApplication_alreadyExists() {

        ProjectResource existingProjectResource = newProjectResource().build();
        Project existingProject = newProject().withApplication(application).build();

        when(projectRepositoryMock.findOneByApplicationId(applicationId)).thenReturn(existingProject);
        when(projectMapperMock.mapToResource(existingProject)).thenReturn(existingProjectResource);

        ServiceResult<ProjectResource> project = service.createProjectFromApplication(applicationId);
        assertTrue(project.isSuccess());
        assertEquals(existingProjectResource, project.getSuccess());

        verify(projectRepositoryMock).findOneByApplicationId(applicationId);
        verify(projectMapperMock).mapToResource(existingProject);

        verify(projectDetailsWorkflowHandlerMock, never()).projectCreated(any(Project.class), any(ProjectUser.class));
        verify(golWorkflowHandlerMock, never()).projectCreated(any(Project.class), any(ProjectUser.class));
        verify(projectWorkflowHandlerMock, never()).projectCreated(any(Project.class), any(ProjectUser.class));
    }

    @Test
    public void findByUserId_returnsOnlyDistinctProjects() {
        Project project = newProject().build();
        User user = newUser().build();
        List<ProjectUser> projectUserRecords = newProjectUser()
                .withProject(project)
                .withRole(PROJECT_PARTNER, PROJECT_FINANCE_CONTACT)
                .build(2);

        ProjectResource projectResource = newProjectResource().build();

        when(projectUserRepositoryMock.findByUserId(user.getId())).thenReturn(projectUserRecords);
        when(projectMapperMock.mapToResource(project)).thenReturn(projectResource);

        List<ProjectResource> result = service.findByUserId(user.getId()).getSuccess();

        assertEquals(1L, result.size());

        InOrder inOrder = inOrder(projectUserRepositoryMock, projectMapperMock);
        inOrder.verify(projectUserRepositoryMock).findByUserId(user.getId());
        inOrder.verify(projectMapperMock).mapToResource(project);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addPartner_organisationNotOnProject(){
        Organisation organisationNotOnProject = newOrganisation().build();
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(organisationRepositoryMock.findById(organisationNotOnProject.getId())).thenReturn(Optional.of(organisationNotOnProject));
        when(userRepositoryMock.findById(u.getId())).thenReturn(Optional.of(u));
        // Method under test
        ServiceResult<ProjectUser> shouldFail = service.addPartner(project.getId(), u.getId(), organisationNotOnProject.getId());
        // Expectations
        assertTrue(shouldFail.isFailure());
        assertTrue(shouldFail.getFailure().is(badRequestError("project does not contain organisation")));
    }

    @Test
    public void addPartner_partnerAlreadyExists(){
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(userRepositoryMock.findById(u.getId())).thenReturn(Optional.of(u));

        setLoggedInUser(newUserResource().withId(u.getId()).build());

        // Method under test
        ServiceResult<ProjectUser> shouldFail = service.addPartner(project.getId(), u.getId(), o.getId());
        // Expectations
        verifyZeroInteractions(projectUserRepositoryMock);
        assertTrue(shouldFail.isSuccess());
    }


    @Test
    public void existsOnApplication(){
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.ofNullable(project));

        // Method under test
        ServiceResult<Boolean> shouldSucceed = service.existsOnApplication(project.getId(), organisation.getId()); // Organisation on the application
        // Expectations
        assertTrue(shouldSucceed.isSuccess());
        assertTrue(shouldSucceed.getSuccess());
    }

    @Test
    public void doesNotExistsOnApplication(){
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.ofNullable(project));

        // Method under test
        ServiceResult<Boolean> shouldSucceed = service.existsOnApplication(project.getId(), newOrganisation().build().getId()); // Organisation on the application
        // Expectations
        assertTrue(shouldSucceed.isSuccess());
        assertFalse(shouldSucceed.getSuccess());
    }

    @Test
    public void addPartner(){
        User newUser = newUser().build();
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.ofNullable(o));
        when(userRepositoryMock.findById(u.getId())).thenReturn(Optional.ofNullable(u));
        when(userRepositoryMock.findById(newUser.getId())).thenReturn(Optional.ofNullable(u));
        List<ProjectUserInvite> projectInvites = newProjectUserInvite().withUser(user).build(1);
        projectInvites.get(0).open();
        when(projectUserInviteRepositoryMock.findByProjectId(project.getId())).thenReturn(projectInvites);

        // Method under test
        ServiceResult<ProjectUser> shouldSucceed = service.addPartner(project.getId(), newUser.getId(), o.getId());
        // Expectations
        assertTrue(shouldSucceed.isSuccess());
    }

    @Test
    public void createProjectsFromFundingDecisions() {

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

        Project newProjectExpectations = createProjectExpectationsFromOriginalApplication();
        when(projectRepositoryMock.save(newProjectExpectations)).thenReturn(savedProject);

        CostCategoryType costCategoryTypeForOrganisation = newCostCategoryType().
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(newCostCategory().withName("Cat1", "Cat2").build(2)).
                        build()).
                build();

        when(projectDetailsWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);
        when(viabilityWorkflowHandlerMock.projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser)).thenReturn(true);
        when(eligibilityWorkflowHandlerMock.projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser)).thenReturn(true);
        when(golWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);
        when(projectWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);
        when(spendProfileWorkflowHandlerMock.projectCreated(savedProject, leadPartnerProjectUser)).thenReturn(true);

        when(projectMapperMock.mapToResource(savedProject)).thenReturn(newProjectResource);

        Map<Long, FundingDecision> fundingDecisions = new HashMap<>();
        fundingDecisions.put(applicationId, FundingDecision.FUNDED);
        ServiceResult<Void> project = service.createProjectsFromFundingDecisions(fundingDecisions);
        assertTrue(project.isSuccess());
        assertNotNull(competition.getProjectSetupStarted());

        verify(projectDetailsWorkflowHandlerMock).projectCreated(savedProject, leadPartnerProjectUser);
        verify(viabilityWorkflowHandlerMock).projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser);
        verify(eligibilityWorkflowHandlerMock).projectCreated(savedProjectPartnerOrganisation, leadPartnerProjectUser);
        verify(golWorkflowHandlerMock).projectCreated(savedProject, leadPartnerProjectUser);
        verify(projectWorkflowHandlerMock).projectCreated(savedProject, leadPartnerProjectUser);
        verify(projectMapperMock).mapToResource(savedProject);
        verify(activityLogService).recordActivityByApplicationId(applicationId, ActivityType.APPLICATION_INTO_PROJECT_SETUP);

    }

    @Test
    public void createProjectsFromFundingDecisions_saveFails() throws Exception {
        Project newProjectExpectations = createProjectExpectationsFromOriginalApplication();
        when(projectRepositoryMock.save(newProjectExpectations)).thenThrow(new DataIntegrityViolationException("dummy constraint violation"));

        Map<Long, FundingDecision> fundingDecisions = new HashMap<>();
        fundingDecisions.put(applicationId, FundingDecision.FUNDED);
        try {
            service.createProjectsFromFundingDecisions(fundingDecisions);
            assertThat("Service failed to throw expected exception.", false);
        } catch (Exception e) {
            assertEquals(e.getCause().getCause().getMessage(),"dummy constraint violation");
        }
    }

    @Test
    public void createProjectsFromFundingDecisions_noFinanceFails() throws Exception {
        createProjectExpectationsFromOriginalApplication();
        List<Section> oldSections = competition.getSections();
        competition.setSections(emptyList());

        Map<Long, FundingDecision> fundingDecisions = new HashMap<>();
        fundingDecisions.put(applicationId, FundingDecision.FUNDED);
        ServiceResult<Void> result = service.createProjectsFromFundingDecisions(fundingDecisions);
        assertTrue(result.isFailure());
        assertEquals(result.getFailure().getErrors().get(0).getErrorKey(), "CREATE_PROJECT_FROM_APPLICATION_FAILS");
        competition.setSections(oldSections);
    }

    @Test
    public void getLeadOrganisation() {
        OrganisationResource expectedOrganisationResource = newOrganisationResource().build();

        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(organisationMapperMock.mapToResource(organisation)).thenReturn(expectedOrganisationResource);

        OrganisationResource organisationResource = service.getLeadOrganisation(project.getId()).getSuccess();

        assertEquals(expectedOrganisationResource, organisationResource);

        InOrder inOrder = inOrder(projectRepositoryMock, organisationRepositoryMock, organisationMapperMock);
        inOrder.verify(projectRepositoryMock).findById(project.getId());
        inOrder.verify(organisationRepositoryMock).findById(project.getApplication().getLeadOrganisationId());
        inOrder.verify(organisationMapperMock).mapToResource(organisation);
        inOrder.verifyNoMoreInteractions();
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
                assertEquals(Role.PARTNER.getName(), matchingProjectUser.get(0).getRole().getName());
                assertEquals(project, matchingProjectUser.get(0).getProcess());
            });

            List<PartnerOrganisation> partnerOrganisations = project.getPartnerOrganisations();
            assertEquals(1, partnerOrganisations.size());

            PartnerOrganisation partnerOrganisation = partnerOrganisations.get(0);
            assertEquals(project, partnerOrganisation.getProject());
            assertEquals(organisation, partnerOrganisation.getOrganisation());
            assertEquals("UB7 8QF", partnerOrganisation.getPostcode());
            assertTrue(partnerOrganisation.isLeadOrganisation());
        });
    }

    @Override
    protected ProjectService supplyServiceUnderTest() {
        return new ProjectServiceImpl();
    }
}