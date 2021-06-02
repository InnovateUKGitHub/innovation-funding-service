package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.builder.OverheadBuilder.newOverhead;
import static org.innovateuk.ifs.finance.builder.OverheadCostCategoryBuilder.newOverheadCostCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OverheadCostCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();
    private OverheadCostCategory overheadCostCategory;

    @Before
    public void setUp() throws Exception {

        FinanceRowItem overHead = newOverhead().withRateType(OverheadRateType.DEFAULT_PERCENTAGE).withRate(20).build();
        costs.add(overHead);

        overheadCostCategory = newOverheadCostCategory()
                .withCosts(singletonList(overHead))
                .build();
    }

    @Test
    public void getCosts() {

        assertEquals(costs, overheadCostCategory.getCosts());
    }

    @Test
    public void getTotal() {
        BigDecimal labourCostTotal = new BigDecimal(1000);
        overheadCostCategory.setLabourCostTotal(labourCostTotal);
        overheadCostCategory.calculateTotal();

        assertEquals(200, overheadCostCategory.getTotal().intValue());
    }

    @Test
    public void addCost() {

        FinanceRowItem overHead2 = newOverhead().withRate(20).build();
        costs.add(overHead2);
        overheadCostCategory.addCost(overHead2);

        assertEquals(costs, overheadCostCategory.getCosts());
    }

    @Test
    public void checkCostCategorySetToExcludeFromTotalCosts() {

        assertFalse(overheadCostCategory.excludeFromTotalCost());
    }
}