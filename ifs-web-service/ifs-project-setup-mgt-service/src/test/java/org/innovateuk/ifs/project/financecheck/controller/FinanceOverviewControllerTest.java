package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.builder.FileEntryResourceBuilder;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.PartnerOrganisationService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckState;
import org.innovateuk.ifs.project.financecheck.form.FinanceCheckForm;
import org.innovateuk.ifs.project.financecheck.viewmodel.FinanceCheckOverviewViewModel;
import org.innovateuk.ifs.project.financecheck.viewmodel.FinanceCheckViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckOverviewResourceBuilder.newFinanceCheckOverviewResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckProcessResourceBuilder.newFinanceCheckProcessResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.times;
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