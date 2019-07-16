package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LabourCostCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();

    private LabourCost labourCost;
    private LabourCost workingDays;
    private LabourCostCategory labourCostCategoryWithWorkingDays;
    private LabourCostCategory nullWorkingDaysLabourCostCategory;

    @Before
    public void setUp() throws Exception {

        workingDays = newLabourCost()
                .withLabourDays(100)
                .withDescription(WORKING_DAYS_PER_YEAR)
                .build();
        labourCost = newLabourCost()
                .withLabourDays(100)
                .withGrossEmployeeCost(BigDecimal.valueOf(20000))
                .withRole("Developer")
                .build();

        costs.add(labourCost);

        labourCostCategoryWithWorkingDays = newLabourCostCategory().withCosts(asList(labourCost, workingDays)).build();
        nullWorkingDaysLabourCostCategory = newLabourCostCategory().withCosts(singletonList(labourCost)).build();
    }

    @Test
    public void getCosts() {

        assertEquals(costs, labourCostCategoryWithWorkingDays.getCosts());
    }

    @Test
    public void getTotalWithWorkingDays() {

        BigDecimal result = labourCost.getGrossEmployeeCost();
        labourCostCategoryWithWorkingDays.calculateTotal();

        assertEquals(result.setScale(5, RoundingMode.HALF_EVEN), labourCostCategoryWithWorkingDays.getTotal());
    }

    @Test
    public void getTotalWithNullWorkingDays() {

        int result = 0;
        nullWorkingDaysLabourCostCategory.calculateTotal();

        assertEquals(BigDecimal.valueOf(result), nullWorkingDaysLabourCostCategory.getTotal());
    }

    @Test
    public void getWorkingDaysPerYear() {

        Integer result = workingDays.getLabourDays();
        assertEquals(result, labourCostCategoryWithWorkingDays.getWorkingDaysPerYear());
    }

    @Test
    public void getWorkingDaysPerYearWithNullWorkingDays() {

        Integer result = 0;
        assertEquals(result, nullWorkingDaysLabourCostCategory.getWorkingDaysPerYear());
    }

    @Test
    public void getWorkingDaysPerYearCostItem() {

        assertEquals(workingDays, labourCostCategoryWithWorkingDays.getWorkingDaysPerYearCostItem());
    }

    @Test
    public void addCost() {

        LabourCost testerCost = newLabourCost()
                .withLabourDays(100)
                .withGrossEmployeeCost(BigDecimal.valueOf(10000))
                .withRole("Tester")
                .build();

        costs.add(testerCost);
        labourCostCategoryWithWorkingDays.addCost(testerCost);

        assertEquals(costs, labourCostCategoryWithWorkingDays.getCosts());
    }

    @Test
    public void excludeFromTotalCost() {

        assertFalse(labourCostCategoryWithWorkingDays.excludeFromTotalCost());
    }
}