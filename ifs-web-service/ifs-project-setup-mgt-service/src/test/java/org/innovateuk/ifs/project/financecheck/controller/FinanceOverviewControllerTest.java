package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.financecheck.viewmodel.FinanceCheckOverviewViewModel;
import org.innovateuk.ifs.project.financechecks.controller.FinanceOverviewController;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckOverviewResourceBuilder.newFinanceCheckOverviewResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class FinanceOverviewControllerTest extends BaseControllerMockMVCTest<FinanceOverviewController> {

    @Test
    public void testView() throws Exception {
        Long projectId = 123L;
        Long organisationId = 456L;

        List<PartnerOrganisationResource> partnerOrganisationResources = newPartnerOrganisationResource()
                .withOrganisationName("EGGS", "Ludlow", "Empire").withLeadOrganisation(false, false, true).withProject(projectId).build(3);
        FinanceCheckEligibilityResource financeCheckEligibilityResource = newFinanceCheckEligibilityResource().withTotalCost(BigDecimal.valueOf(280009)).build();
        when(partnerOrganisationServiceMock.getPartnerOrganisations(projectId)).thenReturn(serviceSuccess(partnerOrganisationResources));
        when(financeCheckServiceMock.getFinanceCheckOverview(projectId)).thenReturn(serviceSuccess(mockFinanceOverview()));
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(projectId, organisationId)).thenReturn(financeCheckEligibilityResource);
        when(projectFinanceService.getProjectFinances(projectId)).thenReturn(Collections.emptyList());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check-overview")).
                andExpect(view().name("project/financecheck/overview")).
                andReturn();
    
        FinanceCheckOverviewViewModel financeCheckOverviewViewModel = (FinanceCheckOverviewViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(LocalDate.of(2016, 01, 01), financeCheckOverviewViewModel.getOverview().getProjectStartDate());
        assertEquals("test-project", financeCheckOverviewViewModel.getOverview().getProjectName());

        verify(financeCheckServiceMock).getFinanceCheckOverview(projectId);
        verify(financeCheckServiceMock, Mockito.times(3)).getFinanceCheckEligibilityDetails(anyLong(), anyLong());
        verify(projectFinanceService).getProjectFinances(projectId);
    }

    @Override
    protected FinanceOverviewController supplyControllerUnderTest() {
        return new FinanceOverviewController();
    }

    private FinanceCheckOverviewResource mockFinanceOverview(){
        Long projectId = 123L;
        String projectName = "test-project";
        Integer durationInMonths = 22;
        BigDecimal grantApplied = BigDecimal.valueOf(650000);
        BigDecimal otherPublicSectorFunding = BigDecimal.valueOf(4500);
        BigDecimal totalPercentageGrant = BigDecimal.valueOf(22);
        BigDecimal totalProjectCost = BigDecimal.valueOf(750000);
        LocalDate startDate = LocalDate.of(2016, 01, 01);

        return newFinanceCheckOverviewResource().withProjectId().
                withProjectId(projectId).
                withProjectName(projectName).
                withGrantAppliedFor(grantApplied).
                withDurationInMonths(durationInMonths).
                withGrantAppliedFor(totalPercentageGrant).
                withOtherPublicSectorFunding(otherPublicSectorFunding).
                withProjectStartDate(startDate).
                withTotalProjectCost(totalProjectCost).build();
    }

}