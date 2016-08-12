package com.worth.ifs.finance;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
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
    List<FinanceRowItem> costs;
    BigDecimal total;
    FinanceRowItem cost3;

    @Before
    public void setUp() throws Exception {
        ApplicationFinanceResource f = new ApplicationFinanceResource();
        QuestionResource q = new QuestionResource();
        costs = new ArrayList<>();
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

    //@Test
    public void getTotalShouldReturnTotal() throws Exception{
        costs.stream().forEach(labourCostCategory::addCost);
        labourCostCategory.addCost(cost3);
        Assert.assertEquals(new BigDecimal(40).setScale(5, RoundingMode.HALF_EVEN), labourCostCategory.getTotal());
    }

    //@Test
    public void getTotalWithNegativeValueShouldReturnTotal() throws Exception{
        //costs.add(costItemFactory.createCostItem(CostType.LABOUR, negativeCost));
        costs.stream().forEach(labourCostCategory::addCost);
        labourCostCategory.addCost(cost3);
        Assert.assertEquals(new BigDecimal(20).setScale(5, RoundingMode.HALF_EVEN), labourCostCategory.getTotal());
    }

    //@Test
    public void getWorkingDaysPerYearShouldReturnWorkingDaysPerYear() throws Exception{
        Integer result = 2000;

        //((LabourCost) cost3).setLabourDays(result);
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