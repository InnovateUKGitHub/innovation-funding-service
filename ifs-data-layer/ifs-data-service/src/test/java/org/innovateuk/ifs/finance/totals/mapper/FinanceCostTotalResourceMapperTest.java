package org.innovateuk.ifs.finance.totals.mapper;

import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.Materials;
import org.innovateuk.ifs.finance.resource.cost.OtherCost;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.totals.FinanceType;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.OtherCostBuilder.newOtherCost;
import static org.innovateuk.ifs.finance.builder.sync.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;

public class FinanceCostTotalResourceMapperTest {

    private FinanceCostTotalResourceMapper financeCostTotalResourceMapper;

    @Before
    public void init() {
        financeCostTotalResourceMapper = new FinanceCostTotalResourceMapper();
    }

    @Test
    public void mapFromApplicationFinanceResourceList() {
        Long financeId = 1L;

        OtherCost otherCost = newOtherCost()
                .withCost(BigDecimal.valueOf(1000)).build();
        DefaultCostCategory otherCostCategory = newDefaultCostCategory().withCosts(Arrays.asList(otherCost)).build();
        otherCostCategory.calculateTotal();

        Materials materialCost = newMaterials()
                .withCost(BigDecimal.valueOf(500))
                .withQuantity(10).build();
        DefaultCostCategory materialCostCategory = newDefaultCostCategory().withCosts(Arrays.asList(materialCost)).build();
        materialCostCategory.calculateTotal();

        Map<FinanceRowType, FinanceRowCostCategory> costs = MapFunctions.asMap(
                FinanceRowType.OTHER_COSTS, otherCostCategory,
                FinanceRowType.MATERIALS, materialCostCategory
        );

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withId(financeId)
                .withFinanceOrganisationDetails(costs).build();

        List<FinanceCostTotalResource> actualResult =
                financeCostTotalResourceMapper.mapFromApplicationFinanceResourceToList(applicationFinanceResource);

        FinanceCostTotalResource expectedOtherCostTotalResource = newFinanceCostTotalResource()
                .withFinanceType(FinanceType.APPLICATION)
                .withFinanceRowType(FinanceRowType.OTHER_COSTS)
                .withTotal(BigDecimal.valueOf(1000))
                .withFinanceId(financeId).build();

        FinanceCostTotalResource expectedMaterialCostTotalResource = newFinanceCostTotalResource()
                .withFinanceType(FinanceType.APPLICATION)
                .withFinanceRowType(FinanceRowType.MATERIALS)
                .withTotal(BigDecimal.valueOf(5000))
                .withFinanceId(financeId).build();

        assertThat(actualResult)
                .usingFieldByFieldElementComparator()
                .contains(expectedOtherCostTotalResource, expectedMaterialCostTotalResource);

    }

    @Test
    public void mapFromApplicationFinanceResourceListToList() {
        Long financeId = 1L;

        OtherCost otherCost = newOtherCost()
                .withCost(BigDecimal.valueOf(1000)).build();
        DefaultCostCategory otherCostCategory = newDefaultCostCategory().withCosts(Arrays.asList(otherCost)).build();
        otherCostCategory.calculateTotal();

        Materials materialCost = newMaterials()
                .withCost(BigDecimal.valueOf(500))
                .withQuantity(10).build();
        DefaultCostCategory materialCostCategory = newDefaultCostCategory().withCosts(Arrays.asList(materialCost)).build();
        materialCostCategory.calculateTotal();

        Map<FinanceRowType, FinanceRowCostCategory> costs = MapFunctions.asMap(
                FinanceRowType.OTHER_COSTS, otherCostCategory,
                FinanceRowType.MATERIALS, materialCostCategory
        );

        List<ApplicationFinanceResource> applicationFinanceResources = newApplicationFinanceResource()
                .withId(financeId)
                .withFinanceOrganisationDetails(costs).build(2);

        List<FinanceCostTotalResource> actualResult =
                financeCostTotalResourceMapper.mapFromApplicationFinanceResourceListToList(applicationFinanceResources);

        FinanceCostTotalResource expectedOtherCostTotalResource = newFinanceCostTotalResource()
                .withFinanceType(FinanceType.APPLICATION)
                .withFinanceRowType(FinanceRowType.OTHER_COSTS)
                .withTotal(BigDecimal.valueOf(1000))
                .withFinanceId(financeId).build();

        FinanceCostTotalResource expectedMaterialCostTotalResource = newFinanceCostTotalResource()
                .withFinanceType(FinanceType.APPLICATION)
                .withFinanceRowType(FinanceRowType.MATERIALS)
                .withTotal(BigDecimal.valueOf(5000))
                .withFinanceId(financeId).build();

        assertThat(actualResult).usingFieldByFieldElementComparator().contains(expectedOtherCostTotalResource,
                expectedMaterialCostTotalResource,
                expectedOtherCostTotalResource,
                expectedMaterialCostTotalResource);
    }
}