package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.financechecks.populator.FinanceChecksEligibilityProjectCostsFormPopulator;
import org.innovateuk.ifs.project.financechecks.viewmodel.FinanceChecksProjectCostsViewModel;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModelPopulator;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.SectionType.PROJECT_COST_FINANCES;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectFinanceChecksControllerTest extends AbstractApplicationMockMVCTest<ProjectFinanceChecksController> {

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private ProjectService projectService;

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private FinanceCheckRestService financeCheckRestService;

    @Mock
    private StatusService statusService;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private FinanceChecksEligibilityProjectCostsFormPopulator formPopulator;

    @Mock
    private ProjectFinanceChangesViewModelPopulator projectFinanceChangesViewModelPopulator;

    private OrganisationResource industrialOrganisation;

    private OrganisationResource kbOrganisation;

    private ApplicationResource application = newApplicationResource().withId(123L).build();

    private ProjectResource project = newProjectResource()
            .withId(1L)
            .withName("Project1")
            .withApplication(application)
            .withProjectState(SETUP)
            .build();

    private FinanceCheckEligibilityResource eligibilityOverview = newFinanceCheckEligibilityResource().build();

    @Before
    public void setUpData() {

        ThreadViewModelPopulator threadViewModelPopulator = new ThreadViewModelPopulator(organisationRestService);
        spy(threadViewModelPopulator);
        controller.setThreadViewModelPopulator(threadViewModelPopulator);

        this.setupCompetition(FundingType.GRANT, AssessorFinanceView.OVERVIEW);
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();
        this.setupInvites();
        this.setupQuestionStatus(applications.get(0));

        application = applications.get(0);
        project.setApplication(application.getId());
        project.setCompetition(competitionId);

        industrialOrganisation = newOrganisationResource()
                .withId(2L)
                .withName("Industrial Org")
                .withCompaniesHouseNumber("123456789")
                .withOrganisationTypeName(OrganisationTypeEnum.BUSINESS.name())
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();

        kbOrganisation = newOrganisationResource()
                .withId(11L)
                .withName("Kb Org")
                .withOrganisationTypeName(OrganisationTypeEnum.KNOWLEDGE_BASE.name())
                .withOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId())
                .build();

        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        when(applicationService.getById(application.getId())).thenReturn(application);

        ApplicantResource applicant = newApplicantResource().withProcessRole(processRoles.get(0)).withOrganisation(industrialOrganisation).build();
        when(applicantRestService.getSection(loggedInUser.getId(), application.getId(),
                simpleFilter(sectionResources, s -> s.getType().equals(PROJECT_COST_FINANCES)).get(0).getId()))
                .thenReturn(newApplicantSectionResource()
                        .withApplication(application)
                        .withCompetition(competitionResource)
                        .withCurrentApplicant(applicant).withApplicants(asList(applicant))
                        .withSection(newSectionResource()
                                .withType(SectionType.FINANCE)
                                .build()).withCurrentUser(loggedInUser)
                        .build());
        when(userRestService.retrieveUserById(loggedInUser.getId())).thenReturn(restSuccess(loggedInUser));

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(organisationRestService.getOrganisationById(industrialOrganisation.getId())).thenReturn(restSuccess(industrialOrganisation));
        when(organisationRestService.getOrganisationById(kbOrganisation.getId())).thenReturn(restSuccess(kbOrganisation));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), industrialOrganisation.getId())).thenReturn(eligibilityOverview);
        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(loggedInUser);
    }

    @Test
    public void viewFinanceChecksLandingPage() throws Exception {

        long projectId = 123L;
        long organisationId = 234L;
        String projectName = "Name";

        ProjectPartnerStatusResource statusResource = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE)
                .withFinanceContactStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).build();
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(statusResource)).build();
        ProjectResource project = newProjectResource().withId(projectId).withName(projectName).withApplication(application).withCompetition(competitionId).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().withId(organisationId).withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(partnerOrganisation));
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceRestService.getProjectFinance(projectId, organisationId)).thenReturn(restSuccess(projectFinanceResource));
        when(financeCheckServiceMock.getQueries(any())).thenReturn(ServiceResult.serviceSuccess(emptyList()));
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(organisationId);
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/project/123/finance-check")).
                andExpect(view().name("project/finance-checks")).
                andReturn();

        ProjectFinanceChecksViewModel model = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(model.getProjectId(), project.getId());
        assertEquals(model.getOrganisationId(), partnerOrganisation.getId());
        assertEquals(model.getProjectName(), projectName);
        assertFalse(model.isApproved());
    }

    private QueryResource sampleQuery() {
        return new QueryResource(null, null, emptyList(), null, null, false, ZonedDateTime.now(), null, null);
    }

    @Test
    public void viewFinanceChecksLandingPageApproved() throws Exception {

        long projectId = 123L;
        long organisationId = 234L;
        String projectName = "Name";

        ProjectPartnerStatusResource statusResource = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE)
                .withFinanceContactStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).build();
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(statusResource)).build();
        ProjectResource project = newProjectResource().withId(projectId).withName(projectName).withApplication(application).withCompetition(competitionId).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().withId(organisationId).withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(partnerOrganisation));
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceRestService.getProjectFinance(projectId, organisationId)).thenReturn(restSuccess(projectFinanceResource));
        when(financeCheckServiceMock.getQueries(projectFinanceResource.getId())).thenReturn(ServiceResult.serviceSuccess(Collections.singletonList(sampleQuery())));
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(organisationId);
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/project/123/finance-check")).
                andExpect(view().name("project/finance-checks")).
                andReturn();

        ProjectFinanceChecksViewModel model = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(model.getProjectId(), project.getId());
        assertEquals(model.getOrganisationId(), partnerOrganisation.getId());
        assertEquals(model.getProjectName(), projectName);
        assertTrue(model.isApproved());
    }

    @Test
    public void viewExternalEligibilityPage() throws Exception {
        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(industrialOrganisation.getId());
        when(projectFinanceRestService.getFinanceTotals(project.getId())).thenReturn(restSuccess(emptyList()));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));
        when(formPopulator.populateForm(project.getId(), industrialOrganisation.getId())).thenReturn(new YourProjectCostsForm());
        when(projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(anyBoolean(), any(), any())).thenReturn(new ProjectFinanceChangesViewModel());

        MvcResult result = mockMvc.perform(get("/project/" + project.getId() + "/finance-check/eligibility")).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility")).
                andExpect(model().attribute("model", instanceOf(FinanceChecksProjectCostsViewModel.class))).
                andReturn();

        assertReadOnlyViewEligibilityDetails(result);

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksEligibilityViewModel viewModel = (FinanceChecksEligibilityViewModel) model.get("summaryModel");

        assertNull(viewModel.getFecModelEnabled());
        assertFalse(viewModel.isCanEditProjectCosts());
    }

    @Test
    public void viewKtpFecExternalEligibilityPage() throws Exception {
        List<FinanceRowType> expectedFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> financeRowType.isCost()
                        && !FinanceRowType.getNonFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competitionResource = newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .build();

        project.setCompetition(competitionResource.getId());

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        ProjectFinanceResource projectFinance = newProjectFinanceResource()
                .withFecEnabled(true)
                .withOrganisation(kbOrganisation.getId())
                .withGrantClaimPercentage(BigDecimal.valueOf(100))
                .withFinanceOrganisationDetails(asMap(
                        FinanceRowType.OTHER_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_SALARY_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.CONSUMABLES, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_SUPPORT, newDefaultCostCategory().build(),
                        FinanceRowType.KNOWLEDGE_BASE, newDefaultCostCategory().build(),
                        FinanceRowType.ESTATE_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.KTP_TRAVEL, newDefaultCostCategory().build()))
                .build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), kbOrganisation.getId())).thenReturn(eligibilityOverview);
        when(competitionRestService.getCompetitionById(anyLong())).thenReturn(restSuccess(competitionResource));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(kbOrganisation);
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(kbOrganisation.getId());
        when(projectFinanceRestService.getFinanceTotals(project.getId())).thenReturn(restSuccess(emptyList()));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(Collections.singletonList(projectFinance)));
        when(formPopulator.populateForm(project.getId(), kbOrganisation.getId())).thenReturn(new YourProjectCostsForm());
        when(projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(anyBoolean(), any(), any())).thenReturn(new ProjectFinanceChangesViewModel());

        MvcResult result = mockMvc.perform(get("/project/" + project.getId() + "/finance-check/eligibility")).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility")).
                andExpect(model().attribute("model", instanceOf(FinanceChecksProjectCostsViewModel.class))).
                andReturn();

        assertReadOnlyViewEligibilityDetails(result);

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksEligibilityViewModel viewModel = (FinanceChecksEligibilityViewModel) model.get("summaryModel");

        assertTrue(viewModel.getFecModelEnabled());

        FinanceChecksProjectCostsViewModel projectCostViewModel = (FinanceChecksProjectCostsViewModel) model.get("model");

        assertNotNull(projectCostViewModel);
        assertFalse(projectCostViewModel.isCanEditProjectCosts());

        assertThat(projectCostViewModel.getOrderedAccordionFinanceRowTypes(), containsInAnyOrder(expectedFinanceRowTypes.toArray()));
    }

    @Test
    public void viewKtpNonFecExternalEligibilityPage() throws Exception {
        List<FinanceRowType> expectedFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> financeRowType.isCost()
                        && !FinanceRowType.getFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competitionResource = newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .build();

        project.setCompetition(competitionResource.getId());

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        ProjectFinanceResource projectFinance = newProjectFinanceResource()
                .withFecEnabled(false)
                .withOrganisation(kbOrganisation.getId())
                .withGrantClaimPercentage(BigDecimal.valueOf(100))
                .withFinanceOrganisationDetails(asMap(
                        FinanceRowType.OTHER_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_SALARY_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.CONSUMABLES, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_SUPPORT, newDefaultCostCategory().build(),
                        FinanceRowType.KNOWLEDGE_BASE, newDefaultCostCategory().build(),
                        FinanceRowType.ESTATE_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.KTP_TRAVEL, newDefaultCostCategory().build(),
                        FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, newDefaultCostCategory().build(),
                        FinanceRowType.INDIRECT_COSTS, newDefaultCostCategory().build()))
                .build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), kbOrganisation.getId())).thenReturn(eligibilityOverview);
        when(competitionRestService.getCompetitionById(anyLong())).thenReturn(restSuccess(competitionResource));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(kbOrganisation);
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(kbOrganisation.getId());
        when(projectFinanceRestService.getFinanceTotals(project.getId())).thenReturn(restSuccess(emptyList()));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(Collections.singletonList(projectFinance)));
        when(formPopulator.populateForm(project.getId(), kbOrganisation.getId())).thenReturn(new YourProjectCostsForm());
        when(projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(anyBoolean(), any(), any())).thenReturn(new ProjectFinanceChangesViewModel());

        MvcResult result = mockMvc.perform(get("/project/" + project.getId() + "/finance-check/eligibility")).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility")).
                andExpect(model().attribute("model", instanceOf(FinanceChecksProjectCostsViewModel.class))).
                andReturn();

        assertReadOnlyViewEligibilityDetails(result);

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksEligibilityViewModel viewModel = (FinanceChecksEligibilityViewModel) model.get("summaryModel");

        assertFalse(viewModel.getFecModelEnabled());

        FinanceChecksProjectCostsViewModel projectCostViewModel = (FinanceChecksProjectCostsViewModel) model.get("model");

        assertNotNull(projectCostViewModel);

        assertThat(projectCostViewModel.getOrderedAccordionFinanceRowTypes(), containsInAnyOrder(expectedFinanceRowTypes.toArray()));
    }

    private void assertReadOnlyViewEligibilityDetails(MvcResult result) {

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksEligibilityViewModel viewModel = (FinanceChecksEligibilityViewModel) model.get("summaryModel");

        assertTrue(viewModel.isExternalView());
        assertFalse(viewModel.isH2020());
    }

    private void setUpViewEligibilityMocking(EligibilityResource eligibility) {

        eligibility.setEligibilityApprovalDate(LocalDate.now());
        eligibility.setEligibilityApprovalUserFirstName("Lee");
        eligibility.setEligibilityApprovalUserLastName("Bowman");

        when(financeCheckRestService.getEligibility(project.getId(), industrialOrganisation.getId())).thenReturn(restSuccess(eligibility));
        when(financeCheckRestService.getEligibility(project.getId(), kbOrganisation.getId())).thenReturn(restSuccess(eligibility));
    }

    @Test
    public void eligibilityChanges() throws Exception {
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(industrialOrganisation.getId());
        ProjectFinanceChangesViewModel viewModel = mock(ProjectFinanceChangesViewModel.class);
        when(projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(false, project, industrialOrganisation)).thenReturn(viewModel);
        mockMvc.perform(get("/project/" + project.getId() + "/finance-check/eligibility/changes"))
                .andExpect(status().isOk())
                .andExpect(view().name("project/financecheck/eligibility-changes"))
                .andExpect(model().attribute("model", viewModel))
                .andReturn();
    }

    @Override
    protected ProjectFinanceChecksController supplyControllerUnderTest() {
        return new ProjectFinanceChecksController();
    }
}
