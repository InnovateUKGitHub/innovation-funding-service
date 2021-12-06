package org.innovateuk.ifs.project.financecheck.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.controller.FinanceCheckController;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.LABOUR;
import static org.innovateuk.ifs.project.builder.CostCategoryResourceBuilder.newCostCategoryResource;
import static org.innovateuk.ifs.project.builder.CostGroupResourceBuilder.newCostGroupResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckOverviewResourceBuilder.newFinanceCheckOverviewResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static org.innovateuk.ifs.project.financecheck.builder.CostResourceBuilder.newCostResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceCheckControllerDocumentation extends BaseControllerMockMVCTest<FinanceCheckController> {

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Test
    public void getByProjectAndOrganisation() throws Exception {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(123L, 456L);

        CostCategoryResource costCategoryResource = newCostCategoryResource().withName(LABOUR.getDisplayName()).build();

        List<CostResource> costs = newCostResource().withCostCategory(costCategoryResource).withValue(new BigDecimal(500.00)).build(7);

        CostGroupResource costGroupResource = newCostGroupResource().withDescription("Finance check cost group").withCosts(costs).build();

        FinanceCheckResource financeCheckResource = newFinanceCheckResource().withProject(123L).withOrganisation(456L).withCostGroup(costGroupResource).build();

        when(financeCheckServiceMock.getByProjectAndOrganisation(projectOrganisationCompositeId)).thenReturn(serviceSuccess(financeCheckResource));

        String url = FinanceCheckURIs.BASE_URL + "/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" +
                FinanceCheckURIs.PATH;

        mockMvc.perform(get(url, 123L, 456L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(financeCheckServiceMock).getByProjectAndOrganisation(projectOrganisationCompositeId);
    }
    
    @Test
    public void getFinanceCheckOverview() throws Exception {
        Long projectId = 123L;

        FinanceCheckOverviewResource expected = newFinanceCheckOverviewResource().
                withProjectId(projectId).
                withProjectStartDate(LocalDate.now()).
                withDurationInMonths(6).
                withTotalProjectCost(new BigDecimal(10000.00)).
                withGrantAppliedFor(new BigDecimal(5000.00)).
                withOtherPublicSectorFunding(new BigDecimal(0.00)).
                withTotalPercentageGrants(new BigDecimal(30.00)).
                build();

        when(financeCheckServiceMock.getFinanceCheckOverview(123L)).thenReturn(serviceSuccess(expected));

        String url = FinanceCheckURIs.BASE_URL + "/{projectId}" + FinanceCheckURIs.PATH + "/overview";

        mockMvc.perform(get(url, 123L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(financeCheckServiceMock).getFinanceCheckOverview(123L);
    }

    @Test
    public void getFinanceCheckEligibilityDetails() throws Exception {
        Long projectId = 123L;
        Long organisationId = 456L;

        FinanceCheckEligibilityResource expected = newFinanceCheckEligibilityResource().
                withProjectId(projectId).
                withOrganisationId(organisationId).
                withDurationInMonths(6L).
                withTotalCost(new BigDecimal(10000.00)).
                withPercentageGrant(new BigDecimal(50.00)).
                withFundingSought(new BigDecimal(5000.00)).
                withOtherPublicSectorFunding(new BigDecimal(0.00)).
                withContributionToProject(new BigDecimal(50.00)).
                withContributionPercentage(new BigDecimal(50.00)).
                build();

        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(123L, 456L)).thenReturn(serviceSuccess(expected));

        String url = FinanceCheckURIs.BASE_URL + "/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH + "/eligibility";

        mockMvc.perform(get(url, 123L, 456L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(financeCheckServiceMock).getFinanceCheckEligibilityDetails(123L, 456L);
    }

    @Test
    public void getViability() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        ViabilityResource expectedViabilityResource = new ViabilityResource(ViabilityState.APPROVED, ViabilityRagStatus.GREEN);
        expectedViabilityResource.setViabilityApprovalDate(LocalDate.now());
        expectedViabilityResource.setViabilityApprovalUserFirstName("Lee");
        expectedViabilityResource.setViabilityApprovalUserLastName("Bowman");

        when(financeCheckServiceMock.getViability(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedViabilityResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/viability", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedViabilityResource)));
    }

    @Test
    public void saveViability() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;
        ViabilityState viability = ViabilityState.APPROVED;
        ViabilityRagStatus viabilityRagStatus = ViabilityRagStatus.GREEN;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckServiceMock.saveViability(projectOrganisationCompositeId, viability, viabilityRagStatus)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/viability/{viability}/{viabilityRagStatus}", projectId, organisationId, viability, viabilityRagStatus)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getEligibility() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        EligibilityResource expectedEligibilityResource = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        expectedEligibilityResource.setEligibilityApprovalDate(LocalDate.now());
        expectedEligibilityResource.setEligibilityApprovalUserFirstName("Lee");
        expectedEligibilityResource.setEligibilityApprovalUserLastName("Bowman");

        when(financeCheckServiceMock.getEligibility(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedEligibilityResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/eligibility", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedEligibilityResource)));
    }

    @Test
    public void saveEligibility() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        EligibilityState eligibility = EligibilityState.APPROVED;
        EligibilityRagStatus eligibilityRagStatus = EligibilityRagStatus.GREEN;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckServiceMock.saveEligibility(projectOrganisationCompositeId, eligibility, eligibilityRagStatus)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/eligibility/{eligibility}/{eligibilityRagStatus}", projectId, organisationId, eligibility, eligibilityRagStatus)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCreditReport() throws Exception {
        String url = "/project/{projectId}/partner-organisation/{organisationId}/credit-report";
        when(financeCheckServiceMock.getCreditReport(123L, 234L)).thenReturn(serviceSuccess(Boolean.TRUE));
        mockMvc.perform(get(url, 123L, 234L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void setCreditReport() throws Exception {
        String url = "/project/{projectId}/partner-organisation/{organisationId}/credit-report/{reportPresent}";
        when(financeCheckServiceMock.saveCreditReport(123L, 234L, Boolean.TRUE)).thenReturn(serviceSuccess());
        mockMvc.perform(post(url, 123L, 234L, Boolean.TRUE)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }


    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}
