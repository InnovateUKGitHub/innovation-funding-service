package org.innovateuk.ifs.project.finance.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.finance.domain.CostCategory;
import org.innovateuk.ifs.project.finance.domain.CostCategoryGroup;
import org.innovateuk.ifs.project.finance.domain.CostCategoryType;
import org.innovateuk.ifs.project.finance.resource.CostCategoryResource;
import org.innovateuk.ifs.project.finance.resource.CostResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.builder.CostCategoryResourceBuilder.newCostCategoryResource;
import static org.innovateuk.ifs.project.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.project.builder.CostGroupResourceBuilder.newCostGroupResource;
import static org.innovateuk.ifs.project.builder.CostResourceBuilder.newCostResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId.id;
import static org.innovateuk.ifs.util.CollectionFunctions.containsAll;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ByFinanceCheckCostCategorySummaryStrategyTest extends BaseServiceUnitTest<ByProjectFinanceCostCategorySummaryStrategy> {

    @Test
    public void testGetCostCategorySummaries(){
        // Setup
        Long organisationId = 2L;
        Long durationInMonths = 4l;
        BigDecimal costInCat1 = new BigDecimal("100");
        BigDecimal costInCat2 = new BigDecimal("200");
        ProjectResource project = newProjectResource().withDuration(durationInMonths).build();
        CostCategoryGroup costCategoryGroup = newCostCategoryGroup().build();
        CostCategoryType costCategoryType = newCostCategoryType().build();

        List<CostCategory> costCategories = newCostCategory().
                withName("cat 1", "cat 2").
                withCostCategoryGroup(costCategoryGroup).
                build(2);

        CostCategory costCategory1 = costCategories.get(0);
        CostCategory costCategory2 = costCategories.get(1);

        List<CostCategoryResource> costCategoryResources = newCostCategoryResource().
                withId(costCategory1.getId(), costCategory2.getId()).
                withName(costCategory1.getName(), costCategory2.getName()).
                build(2);

        List<CostResource> costResources = newCostResource().
                withValue(costInCat1, costInCat2).
                withCostCategory(costCategoryResources.get(0), costCategoryResources.get(1)).
                build(2);

        FinanceCheckResource financeCheck = newFinanceCheckResource().
                withProject(project.getId()).
                withOrganisation(organisationId).
                withCostGroup(newCostGroupResource().
                        withCosts(costResources).
                        build()
        ).build();

        // Mocks
        when(financeCheckServiceMock.getByProjectAndOrganisation(id(financeCheck.getProject(), financeCheck.getOrganisation()))).thenReturn(serviceSuccess(financeCheck));
        when(projectServiceMock.getProjectById(project.getId())).thenReturn(serviceSuccess(project));
        when(costCategoryTypeRepositoryMock.findByCostCategoryGroupId(costCategoryGroup.getId())).thenReturn(costCategoryType);
        costCategories.forEach(cat -> when(costCategoryRepositoryMock.findOne(cat.getId())).thenReturn(cat));

        // Method under test
        ServiceResult<SpendProfileCostCategorySummaries> result = service.getCostCategorySummaries(financeCheck.getProject(), financeCheck.getOrganisation());

        // Assertions
        assertTrue(result.isSuccess());
        SpendProfileCostCategorySummaries summaries = result.getSuccessObject();

        assertEquals(costCategoryType, summaries.getCostCategoryType());
        assertEquals(2, summaries.getCosts().size());
        assertTrue(containsAll(summaries.getCosts(), costCategories, (costCategorySummary, category) -> costCategorySummary.getCategory().equals(category)));

        assertEquals(costResources.get(0).getValue(), summaries.getCosts().get(0).getTotal());
        assertEquals(new BigDecimal("25"), summaries.getCosts().get(0).getFirstMonthSpend());
        assertEquals(new BigDecimal("25"), summaries.getCosts().get(0).getOtherMonthsSpend());
        assertEquals(costResources.get(1).getValue(), summaries.getCosts().get(1).getTotal());
        assertEquals(new BigDecimal("50"), summaries.getCosts().get(1).getOtherMonthsSpend());
        assertEquals(new BigDecimal("50"), summaries.getCosts().get(1).getOtherMonthsSpend());
    }

    @Override
    protected ByProjectFinanceCostCategorySummaryStrategy supplyServiceUnderTest() {
        return new ByProjectFinanceCostCategorySummaryStrategy();
    }
}
