package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DefaultCostCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();
    private DefaultCostCategory defaultCostCategory;

    @Before
    public void setUp() throws Exception {

        FinanceRowItem cost1 = newMaterials()
                .withCost(new BigDecimal(1000))
                .withQuantity(1)
                .build();
        FinanceRowItem cost2 = newMaterials()
                .withCost(new BigDecimal(2000))
                .withQuantity(1)
                .build();

        costs.add(cost1);
        costs.add(cost2);

        defaultCostCategory = newDefaultCostCategory()
                .withCosts(asList(cost1, cost2))
                .build();
    }

    @Test
    public void getCosts() {

        assertEquals(costs, defaultCostCategory.getCosts());
    }

    @Test
    public void getTotal() {

        defaultCostCategory.calculateTotal();
        assertEquals(new BigDecimal(3000), defaultCostCategory.getTotal());
    }

    @Test
    public void addCost() {

        FinanceRowItem cost3 = newMaterials()
                .withCost(new BigDecimal(1000))
                .withQuantity(1)
                .build();

        costs.add(cost3);
        defaultCostCategory.addCost(cost3);

        assertEquals(costs, defaultCostCategory.getCosts());
    }

    @Test
    public void checkCostCategorySetToExcludeFromTotalCosts() {

        assertFalse(defaultCostCategory.excludeFromTotalCost());
    }
}