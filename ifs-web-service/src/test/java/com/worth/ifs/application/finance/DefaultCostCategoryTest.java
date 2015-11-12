package com.worth.ifs.application.finance;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.finance.cost.CostItem;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

        CostItemFactory costItemFactory = new CostItemFactory();

        cost1 = new Cost("a","b", 1, new BigDecimal(20), f, q);
        cost2 = new Cost("a","b", 1, new BigDecimal(20), f, q);
        cost3 = new Cost("a","b", 1, new BigDecimal(20), f, q);

        costs = new ArrayList<>();

        costs.add(costItemFactory.createCostItem(CostType.SUBCONTRACTING_COSTS, cost1));
        costs.add(costItemFactory.createCostItem(CostType.SUBCONTRACTING_COSTS, cost2));
        costs.add(costItemFactory.createCostItem(CostType.SUBCONTRACTING_COSTS, cost3));
        costs.add(null);

        total = new BigDecimal(0);

        defaultCostCategory = new DefaultCostCategory();
    }

    @Test
    public void getCostsShouldReturnCostList(){
        costs.stream().forEach(defaultCostCategory::addCost);
        costs.remove(null);
        Assert.assertEquals(costs, defaultCostCategory.getCosts());
    }

    @Test
    public void getTotalShouldReturnTotal(){
        costs.stream().forEach(defaultCostCategory::addCost);
        costs.remove(null);
        Assert.assertEquals(new BigDecimal(60), defaultCostCategory.getTotal());
    }
}
