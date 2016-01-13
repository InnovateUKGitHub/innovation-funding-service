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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code LabourCostCategoryTest} test for {@Link LabourCostCategory}
 */

public class LabourCostCategoryTest {

    LabourCostCategory labourCostCategory;
    List<CostItem> costs;
    BigDecimal total;
    Cost cost1;
    Cost cost2;
    Cost negativeCost;
    CostItem cost3;
    CostItemFactory costItemFactory;

    @Before
    public void setUp() throws Exception {
        ApplicationFinance f = new ApplicationFinance();
        Question q = new Question();
        costItemFactory = new CostItemFactory();

        cost1 = new Cost("a","b", 1, new BigDecimal(20), f, q);
        cost2 = new Cost("a","b", 1, new BigDecimal(20), f, q);
        negativeCost = new Cost("a","b", 1, new BigDecimal(-20), f, q);
        cost3 = costItemFactory.createCostItem(CostType.LABOUR, new Cost("a",LabourCostCategory.WORKING_DAYS_PER_YEAR, 1, new BigDecimal(20), f, q));

        costs = new ArrayList<>();

        costs.add(costItemFactory.createCostItem(CostType.LABOUR, cost1));
        costs.add(costItemFactory.createCostItem(CostType.LABOUR, cost2));

        total = BigDecimal.ZERO;

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
        Assert.assertEquals(new BigDecimal(40).setScale(5, RoundingMode.HALF_EVEN), labourCostCategory.getTotal());
    }

    @Test
    public void getTotalWithNegativeValueShouldReturnTotal() throws Exception{
        costs.add(costItemFactory.createCostItem(CostType.LABOUR, negativeCost));
        costs.stream().forEach(labourCostCategory::addCost);
        labourCostCategory.addCost(cost3);
        Assert.assertEquals(new BigDecimal(20).setScale(5, RoundingMode.HALF_EVEN), labourCostCategory.getTotal());
    }

    @Test
    public void getWorkingDaysPerYearShouldReturnWorkingDaysPerYear() throws Exception{
        Integer result = 2000;

        ((LabourCost) cost3).setLabourDays(result);
        labourCostCategory.addCost(cost3);

        Assert.assertEquals(result, labourCostCategory.getWorkingDaysPerYear());
    }

    @Test
    public void getWorkingDaysPerYearShouldReturnZeroWhenWorkingDaysIsNull() throws Exception{
        Integer result = 0;

        Assert.assertEquals(result, labourCostCategory.getWorkingDaysPerYear());
    }

    @Test
    public void getWorkingDaysPerYearCostItemShouldReturnWorkingDaysPerYearCostItem() throws Exception{
        labourCostCategory.addCost(cost3);

        Assert.assertEquals(cost3, labourCostCategory.getWorkingDaysPerYearCostItem());
    }

    @Test
    public void getWorkingDaysPerYearCostItemShouldReturnNullWhenWorkingDaysPerYearCostItemIsNull() throws Exception{
        Assert.assertEquals(null, labourCostCategory.getWorkingDaysPerYearCostItem());
    }

    @Test
    public void excludeFromTotalCostShouldReturnBoolean() {
        Assert.assertEquals(labourCostCategory.excludeFromTotalCost(), false);
    }
}