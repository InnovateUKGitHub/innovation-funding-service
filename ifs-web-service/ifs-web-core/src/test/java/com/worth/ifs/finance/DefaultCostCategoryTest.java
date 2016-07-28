package com.worth.ifs.finance;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.CostResource;
import com.worth.ifs.finance.resource.category.DefaultCostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
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
    List<CostItem> costs;
    BigDecimal total;
    CostResource cost1;
    CostResource cost2;
    CostResource cost3;

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
