package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.builder.CostResourceBuilder;
import com.worth.ifs.project.finance.resource.*;
import com.worth.ifs.project.resource.ProjectResource;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.builder.CostCategoryGroupResourceBuilder.newCostCategoryGroupResource;
import static com.worth.ifs.project.builder.CostCategoryResourceBuilder.newCostCategoryResource;
import static com.worth.ifs.project.builder.CostCategoryTypeResourceBuilder.newCostCategoryTypeResource;
import static com.worth.ifs.project.builder.CostGroupResourceBuilder.newCostGroupResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static com.worth.ifs.project.resource.ProjectOrganisationCompositeId.id;
import static com.worth.ifs.util.CollectionFunctions.containsAll;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ByFinanceCheckCostCategorySummaryStrategyTest extends BaseServiceUnitTest<ByFinanceCheckCostCategorySummaryStrategy> {

    @Test
    public void testGetCostCategorySummaries(){
        // Setup
        Long organisationId = 2L;
        Long durationInMonths = 4l;
        BigDecimal costInCat1 = new BigDecimal("100");
        BigDecimal costInCat2 = new BigDecimal("200");
        ProjectResource p = newProjectResource().withDuration(durationInMonths).build();
        CostCategoryGroupResource  ccgr = newCostCategoryGroupResource().build();
        CostCategoryTypeResource cctr = newCostCategoryTypeResource().build();
        CostCategoryResource[] costCategoryResources = newCostCategoryResource().withName("cat 1", "cat 2").withCostCategoryGroup(ccgr).buildArray(2, CostCategoryResource.class);
        List<CostResource> crs = CostResourceBuilder.newCostResource().withValue(costInCat1, costInCat2).withCostCategory(costCategoryResources).build(2);
        FinanceCheckResource fcr = newFinanceCheckResource().
                withProject(p.getId()).
                withOrganisation(organisationId).
                withCostGroup(newCostGroupResource().
                        withCosts(crs).
                        build()
        ).build();
        // Mocks
        when(financeCheckServiceMock.getByProjectAndOrganisation(id(fcr.getProject(), fcr.getOrganisation()))).thenReturn(serviceSuccess(fcr));
        when(projectServiceMock.getProjectById(p.getId())).thenReturn(serviceSuccess(p));
        when(projectFinanceServiceMock.findByCostCategoryGroupId(ccgr.getId())).thenReturn(serviceSuccess(cctr));
        // Method under test
        ServiceResult<SpendProfileCostCategorySummaries> costCategorySummaries = service.getCostCategorySummaries(fcr.getProject(), fcr.getOrganisation());
        // Assertions
        assertTrue(costCategorySummaries.isSuccess());
        assertEquals(cctr, costCategorySummaries.getSuccessObject().getCostCategoryType());
        assertEquals(2, costCategorySummaries.getSuccessObject().getCosts().size());
        assertTrue(containsAll(costCategorySummaries.getSuccessObject().getCosts(), asList(costCategoryResources), (apccs, ccr) -> apccs.getCategory().equals(ccr)));
        assertEquals(crs.get(0).getValue(), costCategorySummaries.getSuccessObject().getCosts().get(0).getTotal());
        assertEquals(new BigDecimal("25"), costCategorySummaries.getSuccessObject().getCosts().get(0).getFirstMonthSpend());
        assertEquals(new BigDecimal("25"), costCategorySummaries.getSuccessObject().getCosts().get(0).getOtherMonthsSpend());
        assertEquals(crs.get(1).getValue(), costCategorySummaries.getSuccessObject().getCosts().get(1).getTotal());
        assertEquals(new BigDecimal("50"), costCategorySummaries.getSuccessObject().getCosts().get(1).getOtherMonthsSpend());
        assertEquals(new BigDecimal("50"), costCategorySummaries.getSuccessObject().getCosts().get(1).getOtherMonthsSpend());
    }


    @Override
    protected ByFinanceCheckCostCategorySummaryStrategy supplyServiceUnderTest() {
        return new ByFinanceCheckCostCategorySummaryStrategy();
    }
}
