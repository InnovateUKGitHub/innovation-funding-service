package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.builder.HecpIndirectCostsBuilder.newHecpIndirectCosts;
import static org.innovateuk.ifs.finance.builder.HecpIndirectCostsCategoryBuilder.newHecpIndirectCostsCostCategory;
import static org.innovateuk.ifs.finance.builder.OverheadBuilder.newOverhead;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HecpIndirectCostsCostCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();
    private HecpIndirectCostsCostCategory hecpIndirectCostsCostCategory;

    @Before
    public void setUp() throws Exception {

        FinanceRowItem hecpIndirectCosts = newHecpIndirectCosts().withRateType(OverheadRateType.HORIZON_EUROPE_GUARANTEE_TOTAL).withRate(150).build();
        costs.add(hecpIndirectCosts);

        hecpIndirectCostsCostCategory = newHecpIndirectCostsCostCategory()
                .withCosts(singletonList(hecpIndirectCosts))
                .build();
    }

    @Test
    public void getCosts() {

        assertEquals(costs, hecpIndirectCostsCostCategory.getCosts());
    }

    @Test
    public void getTotal() {
        BigDecimal labourCostTotal = new BigDecimal(1000);
        hecpIndirectCostsCostCategory.setLabourCostTotal(labourCostTotal);
        hecpIndirectCostsCostCategory.calculateTotal();

        assertEquals(150, hecpIndirectCostsCostCategory.getTotal().intValue());
    }

    @Test
    public void addCost() {

        FinanceRowItem overHead2 = newOverhead().withRate(20).build();
        costs.add(overHead2);
        hecpIndirectCostsCostCategory.addCost(overHead2);

        assertEquals(costs, hecpIndirectCostsCostCategory.getCosts());
    }

    @Test
    public void checkCostCategorySetToExcludeFromTotalCosts() {

        assertFalse(hecpIndirectCostsCostCategory.excludeFromTotalCost());
    }
}