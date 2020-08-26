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
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LabourCostCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();

    private LabourCost labourCost;
    private LabourCost workingDays;
    private LabourCostCategory labourCostCategory;
    private static final BigDecimal GROSS_EMPLOYEE_COST = new BigDecimal("20000.00");

    @Before
    public void setUp() throws Exception {

        workingDays = newLabourCost()
                .withLabourDays(100)
                .withDescription(WORKING_DAYS_PER_YEAR)
                .build();
        labourCost = newLabourCost()
                .withLabourDays(100)
                .withGrossEmployeeCost(GROSS_EMPLOYEE_COST)
                .withRole("Developer")
                .build();

        costs.add(labourCost);
        labourCostCategory = newLabourCostCategory()
                .withCosts(asList(labourCost, workingDays))
                .build();
    }

    @Test
    public void getCosts() {

        assertEquals(costs, labourCostCategory.getCosts());
    }

    @Test
    public void getTotalWithWorkingDays() {

        labourCostCategory.calculateTotal();

        assertEquals(GROSS_EMPLOYEE_COST, labourCostCategory.getTotal());
    }

    @Test
    public void getTotalWithNullWorkingDays() {

        workingDays.setLabourDays(0);
        labourCostCategory.calculateTotal();

        assertEquals(new BigDecimal("0.00"), labourCostCategory.getTotal());
    }

    @Test
    public void getWorkingDaysPerYear() {

        Integer result = workingDays.getLabourDays();
        assertEquals(result, labourCostCategory.getWorkingDaysPerYear());
    }

    @Test
    public void getWorkingDaysPerYearWithNullWorkingDays() {

        Integer result = 0;
        workingDays.setLabourDays(0);
        assertEquals(result, labourCostCategory.getWorkingDaysPerYear());
    }

    @Test
    public void getWorkingDaysPerYearCostItem() {

        assertEquals(workingDays, labourCostCategory.getWorkingDaysPerYearCostItem());
    }

    @Test
    public void addCost() {

        LabourCost testerCost = newLabourCost()
                .withLabourDays(100)
                .withGrossEmployeeCost(BigDecimal.valueOf(10000))
                .withRole("Tester")
                .build();

        costs.add(testerCost);
        labourCostCategory.addCost(testerCost);

        assertEquals(costs, labourCostCategory.getCosts());
    }

    @Test
    public void getTotalWithRounding() {

        labourCost.setGrossEmployeeCost(new BigDecimal("20000.00080"));
        labourCostCategory.calculateTotal();

        assertEquals(new BigDecimal("20000.00"), labourCostCategory.getTotal());
    }

    @Test
    public void checkCostCategorySetToExcludeFromTotalCosts() {

        assertFalse(labourCostCategory.excludeFromTotalCost());
    }
}