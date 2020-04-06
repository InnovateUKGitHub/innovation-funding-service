package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
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

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
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
    private ProjectFinanceService projectFinanceService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private StatusService statusService;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private FinanceChecksEligibilityProjectCostsFormPopulator formPopulator;

    @Mock
    private ProjectFinanceChangesViewModelPopulator projectFinanceChangesViewModelPopulator;

    private OrganisationResource industrialOrganisation;

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

        this.setupCompetition();
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
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(any())).thenReturn(ServiceResult.serviceSuccess(emptyList()));
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(organisationId);

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
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceResource.getId())).thenReturn(ServiceResult.serviceSuccess(Collections.singletonList(sampleQuery())));
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(organisationId);

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
    public void viewExternalEligibilityPage() throws Exception {
        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(industrialOrganisation.getId());
        when(projectFinanceRestService.getFinanceTotals(project.getId())).thenReturn(restSuccess(emptyList()));
        when(formPopulator.populateForm(project.getId(), industrialOrganisation.getId())).thenReturn(new YourProjectCostsForm());

        MvcResult result = mockMvc.perform(get("/project/" + project.getId() + "/finance-checks/eligibility")).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility")).
                andExpect(model().attribute("model", instanceOf(FinanceChecksProjectCostsViewModel.class))).
                andReturn();

        assertReadOnlyViewEligibilityDetails(result);
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

        when(projectFinanceService.getEligibility(project.getId(), industrialOrganisation.getId())).thenReturn(eligibility);
    }

    @Test
    public void eligibilityChanges() throws Exception {
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(industrialOrganisation.getId());
        ProjectFinanceChangesViewModel viewModel = mock(ProjectFinanceChangesViewModel.class);
        when(projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(false, project, industrialOrganisation, getLoggedInUser().getId())).thenReturn(viewModel);
        mockMvc.perform(get("/project/" + project.getId() + "/finance-checks/eligibility/changes"))
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
