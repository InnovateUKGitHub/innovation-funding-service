package com.worth.ifs.application.finance;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.category.DefaultCostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/**
 * {@code DefaultCostCategoryTest} test for {@Link DefaultCostCategory}
 */
public class DefaultCostCategoryTest {

    DefaultCostCategory defaultCostCategory;
    List<CostItem> costs;
    BigDecimal total;
    Cost cost1;
    Cost cost2;
    Cost cost3;

    @Before
    public void setUp() throws Exception {
        ApplicationFinance f = new ApplicationFinance();
        Question q = new Question();

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
