package org.innovateuk.ifs.project.eligibility.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.LabourRowForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.validator.YourProjectCostsFormValidator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.populator.FinanceChecksEligibilityProjectCostsFormPopulator;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.eligibility.saver.FinanceChecksEligibilityProjectCostsSaver;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.FinanceUtil;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceChecksEligibilityControllerTest extends AbstractAsyncWaitMockMVCTest<FinanceChecksEligibilityController> {
    @Mock
    private Model model;

    @Mock
    private ProjectService projectService;

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Mock
    private FinanceUtil financeUtilMock;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private ProjectFinanceRowRestService projectFinanceRowRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private FinanceService financeService;

    @Mock
    private FinanceChecksEligibilityProjectCostsFormPopulator formPopulator;

    @Mock
    private YourProjectCostsFormValidator yourProjectCostsFormValidator;

    @Mock
    private FinanceChecksEligibilityProjectCostsSaver yourProjectCostsSaver;

    @Mock
    private ProjectFinanceChangesViewModelPopulator projectFinanceChangesViewModelPopulator;

    private OrganisationResource industrialOrganisation;

    private OrganisationResource academicOrganisation;

    private CompetitionResource competitionResource = newCompetitionResource().build();

    private ApplicationResource application = newApplicationResource().withId(123L).build();

    private ProjectResource project = newProjectResource().withId(1L).withName("Project1").withApplication(application).withCompetition(competitionResource.getId()).build();

    private FinanceCheckEligibilityResource eligibilityOverview = newFinanceCheckEligibilityResource().build();

    @Before
    public void setupData() {

        industrialOrganisation = newOrganisationResource()
                .withId(2L)
                .withName("Industrial Org")
                .withCompaniesHouseNumber("123456789")
                .withOrganisationTypeName(OrganisationTypeEnum.BUSINESS.name())
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();

        academicOrganisation = newOrganisationResource()
                .withId(1L)
                .withName("Academic Org")
                .withOrganisationTypeName(OrganisationTypeEnum.RESEARCH.name())
                .withOrganisationType(OrganisationTypeEnum.RESEARCH.getId())
                .build();


        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(organisationRestService.getOrganisationById(industrialOrganisation.getId())).thenReturn(restSuccess(industrialOrganisation));
        when(organisationRestService.getOrganisationById(academicOrganisation.getId())).thenReturn(restSuccess(academicOrganisation));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), industrialOrganisation.getId())).thenReturn(eligibilityOverview);
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(financeUtilMock.isUsingJesFinances(competitionResource, OrganisationTypeEnum.BUSINESS.getId())).thenReturn(Boolean.FALSE);
        when(financeUtilMock.isUsingJesFinances(competitionResource, OrganisationTypeEnum.RESEARCH.getId())).thenReturn(Boolean.TRUE);

        when(projectFinanceRestService.getFinanceTotals(project.getId())).thenReturn(restSuccess(Collections.emptyList()));
    }

    @Test
    public void testViewEligibilityLeadOrg() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                project.getId(), industrialOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, true, industrialOrganisation.getName(), false, "Jes1", false);

    }

    @Test
    public void testViewEligibilityLeadOrgH2020() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        CompetitionResource h2020Comp = newCompetitionResource().withCompetitionTypeName("Horizon 2020").build();

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(competitionRestService.getCompetitionById(anyLong())).thenReturn(restSuccess(h2020Comp));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                                               project.getId(), industrialOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, true, industrialOrganisation.getName(), false, "Jes1", true);

    }

    @Test
    public void testViewEligibilityNonLeadOrg() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(academicOrganisation);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                project.getId(), industrialOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, false, industrialOrganisation.getName(), false, "Jes1", false);

    }

    @Test
    public void testViewEligibilityLeadOrgIsAcademic() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);


        when(projectService.getLeadOrganisation(project.getId())).thenReturn(academicOrganisation);

        ApplicationFinanceResource appFinanceResource = newApplicationFinanceResource().withFinanceFileEntry(123L).build();
        FileEntryResource jesFile = newFileEntryResource().withId(987L).withName("Jes1").build();
        when(financeService.getApplicationFinanceByApplicationIdAndOrganisationId(project.getApplication(), academicOrganisation.getId())).thenReturn(appFinanceResource);
        when(financeService.getFinanceEntry(123L)).thenReturn(restSuccess(jesFile));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                project.getId(), academicOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("summaryModel")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, true, academicOrganisation.getName(), true, "Jes1", false);

    }

    @Test
    public void testViewEligibilityLeadOrgIsAcademicNoJesFileEntry() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(academicOrganisation);

        ApplicationFinanceResource appFinanceResource = newApplicationFinanceResource().build();
        when(financeService.getApplicationFinanceByApplicationIdAndOrganisationId(application.getId(), 1L)).thenReturn(appFinanceResource);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                project.getId(), academicOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("summaryModel")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, true, academicOrganisation.getName(), true, null, false);

    }


    private void setUpViewEligibilityMocking(EligibilityResource eligibility) {

        eligibility.setEligibilityApprovalDate(LocalDate.now());
        eligibility.setEligibilityApprovalUserFirstName("Lee");
        eligibility.setEligibilityApprovalUserLastName("Bowman");

        when(projectFinanceService.getEligibility(project.getId(), industrialOrganisation.getId())).thenReturn(eligibility);
        when(projectFinanceService.getEligibility(project.getId(), academicOrganisation.getId())).thenReturn(eligibility);
    }

    private void assertViewEligibilityDetails(EligibilityResource eligibility, MvcResult result, boolean expectedIsLeadPartnerOrganisation, String organisationName, boolean expectedIsUsingJesFinances, String expectedJesFilename, boolean isH2020) {

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksEligibilityViewModel viewModel = (FinanceChecksEligibilityViewModel) model.get("summaryModel");

        assertEquals(expectedIsLeadPartnerOrganisation, viewModel.isLeadPartnerOrganisation());
        assertEquals(viewModel.isH2020(), isH2020);
        assertTrue(viewModel.getOrganisationName().equals(organisationName));
        assertTrue(viewModel.getProjectName().equals(project.getName()));

        assertTrue(viewModel.isEligibilityApproved());
        assertEquals(eligibility.getEligibilityRagStatus(), viewModel.getEligibilityRagStatus());
        assertEquals(eligibility.getEligibilityApprovalDate(), viewModel.getApprovalDate());
        assertEquals(eligibility.getEligibilityApprovalUserFirstName(), viewModel.getApproverFirstName());
        assertEquals(eligibility.getEligibilityApprovalUserLastName(), viewModel.getApproverLastName());

        FinanceChecksEligibilityForm form = (FinanceChecksEligibilityForm) model.get("eligibilityForm");
        assertTrue(form.isConfirmEligibilityChecked());
        assertEquals(eligibility.getEligibilityRagStatus(), form.getEligibilityRagStatus());

        assertFalse(viewModel.isExternalView());
        assertEquals(expectedIsUsingJesFinances, viewModel.isUsingJesFinances());
        if (null != viewModel.getJesFileDetails()) {
            assertEquals(expectedJesFilename, viewModel.getJesFileDetails().getFilename());
        }
    }

    @Test
    public void testConfirmEligibilityWhenConfirmEligibilityNotChecked() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(projectFinanceService.saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.UNSET)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("confirm-eligibility", "").
                        param("confirmEligibilityChecked", "false").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility"));

        verify(projectFinanceService).saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.UNSET);

    }

    @Test
    public void testConfirmEligibilityWhenConfirmEligibilityChecked() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(projectFinanceService.saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.RED)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("confirm-eligibility", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility"));

        verify(projectFinanceService).saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.RED);

    }

    @Test
    public void testConfirmEligibilityWhenSaveEligibilityReturnsFailure() throws Exception {

        when(projectFinanceService.saveEligibility(project.getId(), industrialOrganisation.getId(), EligibilityState.APPROVED, EligibilityRagStatus.RED)).
                thenReturn(serviceFailure(ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED));

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", project.getId(), industrialOrganisation.getId()).
                        param("confirm-eligibility", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility"));

        verify(projectFinanceService).saveEligibility(project.getId(), industrialOrganisation.getId(), EligibilityState.APPROVED, EligibilityRagStatus.RED);

    }

    @Test
    public void testConfirmEligibilitySuccess() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(projectFinanceService.saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.GREEN)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("confirm-eligibility", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "GREEN")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility"));

        verify(projectFinanceService).saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.GREEN);

    }

    @Test
    public void testSaveAndContinueWhenConfirmEligibilityNotChecked() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(projectFinanceService.saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.UNSET)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("save-and-continue", "").
                        param("confirmEligibilityChecked", "false").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check"));

        verify(projectFinanceService).saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.UNSET);

    }

    @Test
    public void testSaveAndContinueWhenConfirmEligibilityChecked() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(projectFinanceService.saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.RED)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("save-and-continue", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check"));

        verify(projectFinanceService).saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.RED);

    }

    @Test
    public void testSaveAndContinueWhenSaveEligibilityReturnsFailure() throws Exception {

        when(projectFinanceService.saveEligibility(project.getId(), industrialOrganisation.getId(), EligibilityState.REVIEW, EligibilityRagStatus.RED)).
                thenReturn(serviceFailure(ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED));

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility);

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", project.getId(), industrialOrganisation.getId()).
                        param("save-and-continue", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility"));

        verify(projectFinanceService).saveEligibility(project.getId(), industrialOrganisation.getId(), EligibilityState.REVIEW, EligibilityRagStatus.RED);

    }

    @Test
    public void testSaveAndContinueSuccess() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(projectFinanceService.saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.GREEN)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("save-and-continue", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "GREEN")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check"));

        verify(projectFinanceService).saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.GREEN);

    }

    @Test
    public void ajaxRemoveRow() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;
        Long costId = 3L;

        mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility/remove-row/{rowId}",
                projectId, organisationId, costId))
                .andExpect(status().isOk());

        verify(yourProjectCostsSaver).removeFinanceRow(String.valueOf(costId));
    }

    @Test
    public void ajaxAddRow() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;
        String rowId = "123";
        LabourRowForm row = new LabourRowForm();
        row.setCostId(Long.valueOf(rowId));
        FinanceRowType type = FinanceRowType.LABOUR;

        doAnswer((invocation) -> {
            YourProjectCostsForm form = (YourProjectCostsForm) invocation.getArguments()[0];
            form.getLabour().getRows().put(rowId, row);
            return form.getLabour().getRows().entrySet().iterator().next();
        }).when(yourProjectCostsSaver).addRowForm(any(YourProjectCostsForm.class), eq(type));

        mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility/add-row/{type}",
                projectId, organisationId, type))
                .andExpect(view().name("application/your-project-costs-fragments :: ajax_labour_row"))
                .andExpect(model().attribute("row", row))
                .andExpect(model().attribute("id", rowId))
                .andExpect(status().isOk());

        verify(yourProjectCostsSaver).addRowForm(any(YourProjectCostsForm.class), eq(type));
    }

    @Test
    public void testProjectFinanceFormSubmit() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;
        FinanceRowType rowType = FinanceRowType.LABOUR;
        when(yourProjectCostsSaver.saveType(isA(YourProjectCostsForm.class), eq(rowType), eq(projectId), eq(organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                param("save-eligibility", rowType.name())).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + 2 +"/eligibility"));
    }

    @Test
    public void testEligibiltiyChanges() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;
        ProjectFinanceChangesViewModel viewModel = mock(ProjectFinanceChangesViewModel.class);
        when(projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(true, project, industrialOrganisation, getLoggedInUser().getId())).thenReturn(viewModel);

        mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility/changes", projectId, organisationId)).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility-changes")).
                andExpect(model().attribute("model", viewModel)).
                andReturn();
    }

    @Override
    protected FinanceChecksEligibilityController supplyControllerUnderTest() {
        return new FinanceChecksEligibilityController();
    }
}