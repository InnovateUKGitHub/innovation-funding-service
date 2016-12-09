package com.worth.ifs.project.viability.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.finance.resource.ProjectFinanceResource;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viability.viewmodel.FinanceChecksViabilityViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationSize;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static com.worth.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaim;
import static com.worth.ifs.finance.builder.GrantClaimCostCategoryBuilder.newGrantClaimCostCategory;
import static com.worth.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static com.worth.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static com.worth.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static com.worth.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static com.worth.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static com.worth.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static com.worth.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static com.worth.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class FinanceChecksViabilityControllerTest extends BaseControllerMockMVCTest<FinanceChecksViabilityController> {

    @Test
    public void testViewViability() throws Exception {

        OrganisationResource organisation = newOrganisationResource().
                withName("My Org name").
                withOrganisationSize(OrganisationSize.MEDIUM).
                withCompanyHouseNumber("123456789").
                build();

        OrganisationResource anotherOrganisation = newOrganisationResource().build();
        ProjectResource project = newProjectResource().build();

        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossAnnualSalary(new BigDecimal("10000.23"), new BigDecimal("5100.11"), BigDecimal.ZERO).
                                withDescription("Developers", "Testers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(100, 120, 250).
                                build(3)).
                        build(),
                FinanceRowType.FINANCE, newGrantClaimCostCategory().withCosts(
                        newGrantClaim().
                                withGrantClaimPercentage(30).
                                build(1)).
                        build(),
                FinanceRowType.OTHER_FUNDING, newOtherFundingCostCategory().withCosts(
                        newOtherFunding().
                                withOtherPublicFunding("Yes", "").
                                withFundingSource(OTHER_FUNDING, "Some source of funding").
                                withFundingAmount(null, BigDecimal.valueOf(1000)).
                                build(2)).
                        build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newMaterials().
                                withCost(new BigDecimal("33.33"), new BigDecimal("98.51")).
                                withQuantity(1, 2).
                                build(2)).
                        build());

        Map<FinanceRowType, FinanceRowCostCategory> anotherOrganisationFinances = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().
                        withCosts(
                                newLabourCost().
                                        withGrossAnnualSalary(new BigDecimal("10000.23"), new BigDecimal("5100.11"), BigDecimal.ZERO).
                                        withDescription("Developers", "Testers", WORKING_DAYS_PER_YEAR).
                                        withLabourDays(100, 120, 250).
                                        build(3)).
                        build());

        organisationFinances.forEach((type, category) -> category.calculateTotal());
        anotherOrganisationFinances.forEach((type, category) -> category.calculateTotal());

        List<ProjectFinanceResource> projectFinances = newProjectFinanceResource().
                withProject(project.getId()).
                withOrganisation(anotherOrganisation.getId(), organisation.getId()).
                withFinanceOrganisationDetails(anotherOrganisationFinances, organisationFinances).
                build(2);

        when(organisationService.getOrganisationById(organisation.getId())).thenReturn(organisation);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(organisation);
        when(projectFinanceService.getFinanceTotals(project.getId())).thenReturn(projectFinances);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/viability",
                project.getId(), organisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/viability")).
                andReturn();

        FinanceChecksViabilityViewModel viewModel =
                (FinanceChecksViabilityViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(organisation.getName(), viewModel.getOrganisationName());
        assertTrue(viewModel.isLeadPartnerOrganisation());

        assertEquals(Integer.valueOf(6678), viewModel.getTotalCosts());
        assertEquals(Integer.valueOf(30), viewModel.getPercentageGrant());
        assertEquals(Integer.valueOf(1004), viewModel.getFundingSought());
        assertEquals(Integer.valueOf(1000), viewModel.getOtherPublicSectorFunding());
        assertEquals(Integer.valueOf(4675), viewModel.getContributionToProject());

        assertEquals(organisation.getOrganisationSize(), viewModel.getOrganisationSize());
        assertEquals(organisation.getCompanyHouseNumber(), viewModel.getCompanyRegistrationNumber());
        assertNull(viewModel.getHeadCount());
        assertNull(viewModel.getTurnover());
        assertEquals(organisation.getCompanyHouseNumber(), viewModel.getCompanyRegistrationNumber());
        assertFalse(viewModel.isCreditReportVerified());
        assertFalse(viewModel.isViabilityApproved());
    }

    @Override
    protected FinanceChecksViabilityController supplyControllerUnderTest() {
        return new FinanceChecksViabilityController();
    }
}
