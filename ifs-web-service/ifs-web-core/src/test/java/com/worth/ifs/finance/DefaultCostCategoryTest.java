package com.worth.ifs.finance;

import com.worth.ifs.finance.resource.FinanceRowResource;
import com.worth.ifs.finance.resource.category.DefaultCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
/**
 * {@code DefaultCostCategoryTest} test for {@Link DefaultCostCategory}
 */
public class DefaultCostCategoryTest {

    DefaultCostCategory defaultCostCategory;
    List<FinanceRowItem> costs;
    BigDecimal total;
    FinanceRowResource cost1;
    FinanceRowResource cost2;
    FinanceRowResource cost3;

    @Before
    public void setUp() throws Exception {
        total = BigDecimal.ZERO;

        defaultCostCategory = new DefaultCostCategory();
    }

    //@Test
    public void getCostsShouldReturnCostList(){
        costs.stream().forEach(defaultCostCategory::addCost);
        costs.remove(null);
        Assert.assertEquals(costs, defaultCostCategory.getCosts());
    }

    //@Test
    public void getTotalShouldReturnTotal(){
        costs.stream().forEach(defaultCostCategory::addCost);
        costs.remove(null);
        Assert.assertEquals(new BigDecimal(60), defaultCostCategory.getTotal());
    }

    @Test
    public void excludeFromTotalCostShouldReturnBoolean() {
        Assert.assertEquals(defaultCostCategory.excludeFromTotalCost(), false);
    }
}
