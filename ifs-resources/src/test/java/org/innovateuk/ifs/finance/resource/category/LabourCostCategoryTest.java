package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LabourCostCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();

    private LabourCost labourCost;
    private LabourCostCategory labourCostCategory;

    @Before
    public void setUp() throws Exception {

        labourCost = newLabourCost()
                .withLabourDays(100)
                .withGrossEmployeeCost(BigDecimal.valueOf(20000))
                .withRole("Developers")
                .build();

        costs.add(labourCost);

        labourCostCategory = new LabourCostCategory();
        labourCostCategory.setCosts(costs);
        labourCostCategory.setWorkingDaysPerYearCostItem(labourCost);
    }

    @Test
    public void getCosts() {

        assertEquals(costs, labourCostCategory.getCosts());
    }

    @Test
    public void getTotalWithNotNullWorkingDays() {

        labourCostCategory.calculateTotal();
        BigDecimal result = labourCost.getGrossEmployeeCost();

        assertEquals(result.setScale(5, RoundingMode.HALF_EVEN), labourCostCategory.getTotal());
    }

    @Test
    public void getTotalWithNullWorkingDays() {

        int result = 0;
        labourCostCategory.setWorkingDaysPerYearCostItem(null);
        labourCostCategory.calculateTotal();

        assertEquals(BigDecimal.valueOf(result), labourCostCategory.getTotal());
    }

    @Test
    public void getWorkingDaysPerYear() {

        Integer result = labourCost.getLabourDays();
        assertEquals(result, labourCostCategory.getWorkingDaysPerYear());
    }

    @Test
    public void getWorkingDaysPerYearWithNullWorkingDays() {

        Integer result = 0;
        labourCost.setLabourDays(0);

        assertEquals(result, labourCostCategory.getWorkingDaysPerYear());
    }

    @Test
    public void getWorkingDaysPerYearCostItem() {

        assertEquals(labourCost, labourCostCategory.getWorkingDaysPerYearCostItem());

    }

    @Test
    public void addCost() {

        FinanceRowItem labourCost2 = newLabourCost()
                .withLabourDays(100)
                .withGrossEmployeeCost(BigDecimal.valueOf(10000))
                .withRole("Testers")
                .build();
        costs.add(labourCost2);
        labourCostCategory.addCost(labourCost2);

        assertEquals(costs, labourCostCategory.getCosts());
    }

    @Test
    public void excludeFromTotalCost() {

        assertFalse(labourCostCategory.excludeFromTotalCost());
    }
}