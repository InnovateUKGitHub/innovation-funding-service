package org.innovateuk.ifs.project.viability.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.ViabilityResource;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.viability.form.FinanceChecksViabilityForm;
import org.innovateuk.ifs.project.viability.viewmodel.FinanceChecksViabilityViewModel;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.EmployeesAndTurnoverResourceBuilder.newEmployeesAndTurnoverResource;
import static org.innovateuk.ifs.finance.builder.ExcludedCostCategoryBuilder.newExcludedCostCategory;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaimPercentage;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.OtherCostBuilder.newOtherCost;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceChecksViabilityControllerTest extends BaseControllerMockMVCTest<FinanceChecksViabilityController> {
    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private ApplicationService applicationService;

    private OrganisationResource industrialOrganisation = newOrganisationResource()
            .withName("Industrial Org")
            .withCompaniesHouseNumber("123456789")
            .withId(1L)
            .build();

    private OrganisationResource academicOrganisation = newOrganisationResource()
            .withName("Academic Org")
            .withCompaniesHouseNumber("987654321")
            .withId(2L)
            .build();

    private CompetitionResource competitionResource = newCompetitionResource()
            .withName("Competition")
            .withFinanceRowTypes(Collections.singletonList(FinanceRowType.FINANCE))
            .build();

    private ApplicationResource app = newApplicationResource()
            .withId(456L)
            .withCompetition(competitionResource.getId())
            .build();

    private ProjectResource project = newProjectResource()
            .withApplication(app)
            .withProjectState(SETUP)
            .build();

    private Map<FinanceRowType, FinanceRowCostCategory> industrialOrganisationFinances = asMap(
            FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                    newLabourCost().
                            withGrossEmployeeCost(new BigDecimal("10000.23"), new BigDecimal("5100.11"), BigDecimal.ZERO).
                            withDescription("Developers", "Testers", WORKING_DAYS_PER_YEAR).
                            withLabourDays(100, 120, 250).
                            build(3)).
                    build(),
            FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                    newMaterials().
                            withCost(new BigDecimal("33.33"), new BigDecimal("98.51")).
                            withQuantity(1, 2).
                            build(2)).
                    build(),
            FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                    newGrantClaimPercentage().
                            withGrantClaimPercentage(BigDecimal.valueOf(30)).
                            build(1)).
                    build(),
            FinanceRowType.OTHER_FUNDING, newOtherFundingCostCategory().withCosts(
                    newOtherFunding().
                            withOtherPublicFunding("Yes", "").
                            withFundingSource(OTHER_FUNDING, "Some source of funding").
                            withFundingAmount(null, BigDecimal.valueOf(1000)).
                            build(2))
            .build());

    private Map<FinanceRowType, FinanceRowCostCategory> academicOrganisationFinances = asMap(
            FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                    newLabourCost().
                            withGrossEmployeeCost(new BigDecimal("10000.23"), new BigDecimal("5100.11"), new BigDecimal("600.11"), BigDecimal.ZERO).
                            withDescription("Developers", "Testers", "Something else", WORKING_DAYS_PER_YEAR).
                            withLabourDays(100, 120, 120, 250).
                            withName("direct_staff", "direct_staff", "exceptions_staff").
                            build(4)).
                    build(),
            FinanceRowType.OTHER_COSTS, newDefaultCostCategory().withCosts(
                    newOtherCost().
                            withCost(new BigDecimal("33.33"), new BigDecimal("98.51")).
                            withDescription("direct_costs", "exceptions_costs").
                            build(2)).
                    build(),
            FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                    newGrantClaimPercentage().
                            withGrantClaimPercentage(BigDecimal.valueOf(100)).
                            build(1)).
                    build(),
            FinanceRowType.OTHER_FUNDING, newOtherFundingCostCategory().withCosts(
                    newOtherFunding().
                            withOtherPublicFunding("Yes", "").
                            withFundingSource(OTHER_FUNDING, "Some source of funding").
                            withFundingAmount(null, BigDecimal.valueOf(1000)).
                            build(2)).
                    build());

    private List<ProjectFinanceResource> projectFinances = newProjectFinanceResource().
            withProject(project.getId()).
            withOrganisation(academicOrganisation.getId(), industrialOrganisation.getId()).
            withFinanceOrganisationDetails(academicOrganisationFinances, industrialOrganisationFinances).
            withFinancialYearAccounts(null, newEmployeesAndTurnoverResource().withEmployees(1L).withTurnover(BigDecimal.valueOf(2)).build()).
            build(2);

    @Before
    public void setupFinanceTotals() {
        industrialOrganisationFinances.forEach((type, category) -> category.calculateTotal());
        academicOrganisationFinances.forEach((type, category) -> category.calculateTotal());
    }

    @Test
    public void viewViabilityIndustrial() throws Exception {

        ViabilityResource viability = new ViabilityResource(ViabilityState.APPROVED, ViabilityRagStatus.GREEN);

        when(organisationRestService.getOrganisationById(industrialOrganisation.getId())).thenReturn(restSuccess(industrialOrganisation));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competitionResource));
        when(projectFinanceService.getProjectFinances(project.getId())).thenReturn(projectFinances);
        when(projectFinanceService.getViability(project.getId(), industrialOrganisation.getId())).thenReturn(viability);
        when(projectFinanceService.isCreditReportConfirmed(project.getId(), industrialOrganisation.getId())).thenReturn(false);

        when(projectService.getById(project.getId())).thenReturn(project);
        when(applicationService.getById(456L)).thenReturn(app);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/viability",
                project.getId(), industrialOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/viability")).
                andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksViabilityViewModel viewModel = (FinanceChecksViabilityViewModel) model.get("model");

        assertTrue(viewModel.isLeadPartnerOrganisation());
        assertTrue(viewModel.isShowApprovalMessage());
        assertTrue(viewModel.isShowBackToFinanceCheckButton());
        assertFalse(viewModel.isShowSaveAndContinueButton());

        assertOrganisationDetails(industrialOrganisation, viewModel);

        assertEquals(Integer.valueOf(6678), viewModel.getTotalCosts());
        assertEquals(BigDecimal.valueOf(30), viewModel.getPercentageGrant());
        assertEquals(Integer.valueOf(1004), viewModel.getFundingSought());
        assertEquals(Integer.valueOf(1000), viewModel.getOtherPublicSectorFunding());
        assertEquals(Integer.valueOf(4675), viewModel.getContributionToProject());
        assertTrue(viewModel.isReadOnly());

        FinanceChecksViabilityForm form = (FinanceChecksViabilityForm) model.get("form");
        assertEquals(viability.getViabilityRagStatus(), form.getRagStatus());
        assertFalse(form.isCreditReportConfirmed());
        assertTrue(form.isConfirmViabilityChecked());

        assertEquals((Long) 2L, viewModel.getTurnover());
        assertEquals((Long) 1L, viewModel.getHeadCount());
    }

    @Test
    public void viewViabilityAcademic() throws Exception {

        ViabilityResource viability = new ViabilityResource(ViabilityState.REVIEW, ViabilityRagStatus.UNSET);

        when(organisationRestService.getOrganisationById(academicOrganisation.getId())).thenReturn(restSuccess(academicOrganisation));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competitionResource));
        when(projectFinanceService.getProjectFinances(project.getId())).thenReturn(projectFinances);
        when(projectFinanceService.getViability(project.getId(), academicOrganisation.getId())).thenReturn(viability);
        when(projectFinanceService.isCreditReportConfirmed(project.getId(), academicOrganisation.getId())).thenReturn(true);
        when(projectService.getById(project.getId())).thenReturn(project);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/viability",
                project.getId(), academicOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/viability")).
                andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();
        FinanceChecksViabilityViewModel viewModel = (FinanceChecksViabilityViewModel) model.get("model");

        assertFalse(viewModel.isLeadPartnerOrganisation());
        assertFalse(viewModel.isShowApprovalMessage());
        assertFalse(viewModel.isShowBackToFinanceCheckButton());
        assertTrue(viewModel.isShowSaveAndContinueButton());

        assertOrganisationDetails(academicOrganisation, viewModel);

        assertEquals(Integer.valueOf(6868), viewModel.getTotalCosts());
        assertEquals(BigDecimal.valueOf(100), viewModel.getPercentageGrant());
        assertEquals(Integer.valueOf(5868), viewModel.getFundingSought());
        assertEquals(Integer.valueOf(1000), viewModel.getOtherPublicSectorFunding());
        assertEquals(Integer.valueOf(0), viewModel.getContributionToProject());
        assertFalse(viewModel.isReadOnly());

        FinanceChecksViabilityForm form = (FinanceChecksViabilityForm) model.get("form");

        assertEquals(viability.getViabilityRagStatus(), form.getRagStatus());
        assertTrue(form.isCreditReportConfirmed());
        assertFalse(form.isConfirmViabilityChecked());

        assertNull(viewModel.getTurnover());
        assertNull(viewModel.getHeadCount());
    }

    @Test
    public void confirmViability() throws Exception {

        Long projectId = 123L;
        Long organisationId = 456L;

        when(projectFinanceService.saveCreditReportConfirmed(projectId, organisationId, true)).
                thenReturn(serviceSuccess());

        when(projectFinanceService.saveViability(projectId, organisationId, ViabilityState.APPROVED, ViabilityRagStatus.RED)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
            post("/project/{projectId}/finance-check/organisation/{organisationId}/viability", projectId, organisationId).
                param("confirm-viability", "").
                param("confirmViabilityChecked", "true").
                param("creditReportConfirmed", "true").
                param("ragStatus", "RED")).
            andExpect(status().is3xxRedirection()).
            andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/viability"));

        verify(projectFinanceService).saveCreditReportConfirmed(projectId, organisationId, true);
        verify(projectFinanceService).saveViability(projectId, organisationId, ViabilityState.APPROVED, ViabilityRagStatus.RED);
    }

    @Test
    public void saveAndContinue() throws Exception {

        Long projectId = 123L;
        Long organisationId = 456L;

        when(projectFinanceService.saveCreditReportConfirmed(projectId, organisationId, false)).
                thenReturn(serviceSuccess());

        when(projectFinanceService.saveViability(projectId, organisationId, ViabilityState.REVIEW, ViabilityRagStatus.UNSET)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/viability", projectId, organisationId).
                        param("save-and-continue", "").
                        param("creditReportConfirmed", "false").
                        param("confirmViabilityChecked", "false").
                        param("ragStatus", "UNSET")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check"));

        verify(projectFinanceService).saveCreditReportConfirmed(projectId, organisationId, false);
        verify(projectFinanceService).saveViability(projectId, organisationId, ViabilityState.REVIEW, ViabilityRagStatus.UNSET);
    }

    @Test
    public void saveAndContinueWhenConfirmViabilityHasBeenUnselected() throws Exception {

        Long projectId = 123L;
        Long organisationId = 456L;

        when(projectFinanceService.saveCreditReportConfirmed(projectId, organisationId, true)).
                thenReturn(serviceSuccess());

        when(projectFinanceService.saveViability(projectId, organisationId, ViabilityState.REVIEW, ViabilityRagStatus.UNSET)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/viability", projectId, organisationId).
                        param("save-and-continue", "").
                        param("confirmViabilityChecked", "false").
                        param("creditReportConfirmed", "true").
                        param("ragStatus", "RED")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check"));

        verify(projectFinanceService).saveCreditReportConfirmed(projectId, organisationId, true);
        verify(projectFinanceService).saveViability(projectId, organisationId, ViabilityState.REVIEW, ViabilityRagStatus.UNSET);
    }

    private void assertOrganisationDetails(OrganisationResource organisation, FinanceChecksViabilityViewModel viewModel) {
        assertEquals(organisation.getName(), viewModel.getOrganisationName());
        assertEquals(organisation.getCompaniesHouseNumber(), viewModel.getCompanyRegistrationNumber());
        assertEquals(organisation.getId(), viewModel.getOrganisationId());
        assertEquals(organisation.getCompaniesHouseNumber(), viewModel.getCompanyRegistrationNumber());
    }

    @Override
    protected FinanceChecksViabilityController supplyControllerUnderTest() {
        return new FinanceChecksViabilityController();
    }
}
