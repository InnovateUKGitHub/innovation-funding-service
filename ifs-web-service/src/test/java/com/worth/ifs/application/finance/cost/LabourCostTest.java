package com.worth.ifs.application.finance.cost;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * {@code LabourCostTest} test for {@Link LabourCost}
 */

public class LabourCostTest {
    private Long id;
    private String role;
    private BigDecimal grossAnnualSalary;
    private Integer labourDays;
    private BigDecimal rate;
    private String description;
    private BigDecimal total;
    private LabourCost labourCost;

    @Before
    public void setup(){
        id = 1L;
        role = "role";
        grossAnnualSalary = new BigDecimal(100000);
        labourDays = 100;
        rate = new BigDecimal(1000);
        description = "description";
        total = BigDecimal.ZERO;
        labourCost = new LabourCost(id, role, grossAnnualSalary, labourDays, description);
        rate = labourCost.getRate(labourDays);
        total = labourCost.getTotal(labourDays);
    }

    @Test
    public void constructorShouldReturnNewInstance(){
        new LabourCost();
        new LabourCost(id, role, grossAnnualSalary, labourDays, description);
    }

    @Test
    public void getRatePerDayShouldNotFailOnInfiniteDivisions(){
        grossAnnualSalary = new BigDecimal(10);
        labourDays = 3;
        LabourCost labour = new LabourCost(id, role, grossAnnualSalary, labourDays, description);
        BigDecimal result = grossAnnualSalary.divide(new BigDecimal(labourDays), 5, RoundingMode.HALF_EVEN);

        Assert.assertEquals(result, labour.getRatePerDay(labourDays));
    }

    @Test
    public void gettersShouldReturnCorrectValues(){
        Assert.assertEquals(id, labourCost.getId());
        Assert.assertEquals(role, labourCost.getRole());
        Assert.assertEquals(grossAnnualSalary, labourCost.getGrossAnnualSalary());
        Assert.assertEquals(rate, labourCost.getRate());
        Assert.assertEquals(labourDays, labourCost.getLabourDays());
        Assert.assertEquals(total, labourCost.getTotal());
    }

    @Test
    public void getRatePerDayShouldReturnZeroWhenWorkingDaysIsZero(){
        Assert.assertEquals(BigDecimal.ZERO, labourCost.getRatePerDay(0));
    }

    @Test
    public void getRateShouldNotRecalculateRateWhenArgumentIsNull(){
        Assert.assertEquals(rate, labourCost.getRate(null));
    }

    @Test
    public void getTotalShouldReturnZeroWhenLabourDaysIsNull(){
        labourCost.setLabourDays(null);

        Assert.assertNull(labourCost.getLabourDays());
        Assert.assertEquals(BigDecimal.ZERO, labourCost.getTotal(labourDays));
    }

    @Test
    public void getTotalShouldReturnZeroWhenRateIsNull(){
        labourCost = new LabourCost(id, role, grossAnnualSalary, labourDays, description);

        Assert.assertNull(labourCost.getRate());
        Assert.assertEquals(BigDecimal.ZERO, labourCost.getTotal(0));
    }

    @Test
    public void setGrossAnnualSalaryShouldNotThrowAnError(){
        labourCost.setGrossAnnualSalary(new BigDecimal(123));
    }

    @Test
    public void setGrossAnnualSalaryShouldNotThrowAnErrorOnNegativeValue(){
        labourCost.setGrossAnnualSalary(new BigDecimal(-123));
    }

    @Test
    public void setGrossAnnualSalaryShouldNotThrowAnErrorOnNull(){
        labourCost.setGrossAnnualSalary(null);
    }

    @Test
    public void setRoleShouldNotThrowAnErrorOnString(){
        labourCost.setRole("new role");
    }

    @Test
    public void setRoleShouldNotThrowAnErrorOnNull(){
        labourCost.setRole(null);
    }
}
