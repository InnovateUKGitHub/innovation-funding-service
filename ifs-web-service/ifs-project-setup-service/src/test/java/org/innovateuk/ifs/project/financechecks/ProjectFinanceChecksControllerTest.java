package org.innovateuk.ifs.project.financechecks;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.finance.view.ProjectFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.OpenProjectFinanceSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.finance.resource.Eligibility;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.view.ProjectFinanceFormHandler;
import org.innovateuk.ifs.project.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceChecksController;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.noErrors;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectFinanceChecksControllerTest extends BaseControllerMockMVCTest<ProjectFinanceChecksController> {
    @Spy
    @InjectMocks
    private ProjectFinanceOverviewModelManager projectFinanceOverviewModelManager;

    @Spy
    @InjectMocks
    private OpenProjectFinanceSectionModelPopulator openFinanceSectionModel;

    @Mock
    private ApplicationModelPopulator applicationModelPopulator;

    @Mock
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Mock
    private Model model;

    @Mock
    private ProjectFinanceFormHandler projectFinanceFormHandler;

    private OrganisationResource industrialOrganisation;

    private ApplicationResource application = newApplicationResource().withId(123L).build();

    private ProjectResource project = newProjectResource().withId(1L).withName("Project1").withApplication(application).build();

    private FinanceCheckEligibilityResource eligibilityOverview = newFinanceCheckEligibilityResource().build();

    @Before
    public void setUp() {

        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupUserRoles();
        this.setupFinances();
        this.setupInvites();
        this.setupQuestionStatus(applications.get(0));

        application = applications.get(0);
        project.setApplication(application.getId());

        industrialOrganisation = newOrganisationResource()
                .withId(2L)
                .withName("Industrial Org")
                .withCompanyHouseNumber("123456789")
                .withOrganisationTypeName(OrganisationTypeEnum.BUSINESS.name())
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId())
                .build();

        // save actions should always succeed.
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), eq(""), anyBoolean())).thenReturn(restSuccess(new ValidationMessages(fieldError("value", "", "Please enter some text 123"))));
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), anyString(), anyBoolean())).thenReturn(restSuccess(noErrors()));

        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        when(applicationService.getById(application.getId())).thenReturn(application);

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(organisationService.getOrganisationById(industrialOrganisation.getId())).thenReturn(industrialOrganisation);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), industrialOrganisation.getId())).thenReturn(eligibilityOverview);
        when(financeHandler.getProjectFinanceModelManager(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId())).thenReturn(defaultProjectFinanceModelManager);
        when(financeHandler.getProjectFinanceFormHandler(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId())).thenReturn(projectFinanceFormHandler);

        FinanceViewModel financeViewModel = new FinanceViewModel();
        financeViewModel.setOrganisationGrantClaimPercentage(74);

        when(defaultProjectFinanceModelManager.getFinanceViewModel(anyLong(), anyList(), anyLong(), any(Form.class), anyLong())).thenReturn(financeViewModel);
    }


    @Test
    public void testViewFinanceChecksLandingPage() throws Exception {

        long projectId = 123L;
        long organisationId = 234L;
        String projectName = "Name";

        ProjectPartnerStatusResource statusResource = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE)
                .withFinanceContactStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).build();
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(statusResource)).build();
        ProjectResource project = newProjectResource().withId(projectId).withName(projectName).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().withId(organisationId).build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(any())).thenReturn(ServiceResult.serviceSuccess(Collections.emptyList()));
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(organisationService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(organisationId);

        MvcResult result = mockMvc.perform(get("/project/123/finance-checks")).
                andExpect(view().name("project/finance-checks")).
                andReturn();

        ProjectFinanceChecksViewModel model = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(model.getProjectId(), project.getId());
        assertEquals(model.getOrganisationId(), partnerOrganisation.getId());
        assertEquals(model.getProjectName(), projectName);
        assertFalse(model.isApproved());
    }

    private QueryResource sampleQuery() {
        return new QueryResource(null, null, Collections.emptyList(), null, null, false, ZonedDateTime.now());
    }

    @Test
    public void testViewFinanceChecksLandingPageApproved() throws Exception {

        long projectId = 123L;
        long organisationId = 234L;
        String projectName = "Name";

        ProjectPartnerStatusResource statusResource = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE)
                .withFinanceContactStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).build();
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(statusResource)).build();
        ProjectResource project = newProjectResource().withId(projectId).withName(projectName).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().withId(organisationId).build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().build();

        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceResource.getId())).thenReturn(ServiceResult.serviceSuccess(Collections.singletonList(sampleQuery())));
        when(organisationService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(organisationId);

        MvcResult result = mockMvc.perform(get("/project/123/finance-checks")).
                andExpect(view().name("project/finance-checks")).
                andReturn();

        ProjectFinanceChecksViewModel model = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(model.getProjectId(), project.getId());
        assertEquals(model.getOrganisationId(), partnerOrganisation.getId());
        assertEquals(model.getProjectName(), projectName);
        assertTrue(model.isApproved());
    }

    @Test
    public void testViewExternalEligibilityPage() throws Exception {
        EligibilityResource eligibility = new EligibilityResource(Eligibility.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(industrialOrganisation.getId()).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(organisationService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(industrialOrganisation.getId());

        MvcResult result = mockMvc.perform(get("/project/" + project.getId() + "/finance-checks/eligibility")).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertReadOnlyViewEligibilityDetails(result);
    }

    private void assertReadOnlyViewEligibilityDetails(MvcResult result) {

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksEligibilityViewModel viewModel = (FinanceChecksEligibilityViewModel) model.get("summaryModel");

        assertTrue(viewModel.isExternalView());
    }

    private void setUpViewEligibilityMocking(EligibilityResource eligibility) {

        eligibility.setEligibilityApprovalDate(LocalDate.now());
        eligibility.setEligibilityApprovalUserFirstName("Lee");
        eligibility.setEligibilityApprovalUserLastName("Bowman");

        when(projectFinanceService.getEligibility(project.getId(), industrialOrganisation.getId())).thenReturn(eligibility);
    }

    @Test
    public void testEligibilityChanges() throws Exception {
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(industrialOrganisation.getId()).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(organisationService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(industrialOrganisation.getId());
        mockMvc.perform(get("/project/" + project.getId() + "/finance-checks/eligibility/changes"))
                .andExpect(status().isOk())
                .andExpect(view().name("project/financecheck/eligibility-changes"))
                .andReturn();
    }

    @Override
    protected ProjectFinanceChecksController supplyControllerUnderTest() {
        return new ProjectFinanceChecksController();
    }
}
