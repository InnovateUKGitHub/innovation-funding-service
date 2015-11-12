package com.worth.ifs.application.finance;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.finance.cost.CostItem;
import com.worth.ifs.application.finance.cost.LabourCost;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LabourCostCategoryTest {

    LabourCostCategory labourCostCategory;
    List<CostItem> costs;
    BigDecimal total;
    Cost cost1;
    Cost cost2;
    CostItem cost3;

    @Before
    public void setUp() throws Exception {
        ApplicationFinance f = new ApplicationFinance();
        Question q = new Question();

        CostItemFactory costItemFactory = new CostItemFactory();

        cost1 = new Cost("a","b", 1, new BigDecimal(20), f, q);
        cost2 = new Cost("a","b", 1, new BigDecimal(20), f, q);
        cost3 = costItemFactory.createCostItem(CostType.LABOUR, new Cost("a","Working days per year", 1, new BigDecimal(20), f, q));

        costs = new ArrayList<>();

        costs.add(costItemFactory.createCostItem(CostType.LABOUR, cost1));
        costs.add(costItemFactory.createCostItem(CostType.LABOUR, cost2));

        total = new BigDecimal(0);

        labourCostCategory = new LabourCostCategory();
    }

    @Test
    public void getCostsShouldReturnCostList() throws Exception{
        costs.stream().forEach(labourCostCategory::addCost);
        Assert.assertEquals(costs, labourCostCategory.getCosts());
    }

    @Test
    public void getCostsShouldReturnCostListWithoutNullValues() throws Exception{
        costs.add(null);
        costs.stream().forEach(labourCostCategory::addCost);
        costs.remove(null);
        Assert.assertEquals(costs, labourCostCategory.getCosts());
    }

    @Test
    public void getTotalShouldReturnTotal() throws Exception{
        costs.stream().forEach(labourCostCategory::addCost);
        labourCostCategory.addCost(cost3);
        Assert.assertEquals(new BigDecimal(40).setScale(20), labourCostCategory.getTotal());
    }
}