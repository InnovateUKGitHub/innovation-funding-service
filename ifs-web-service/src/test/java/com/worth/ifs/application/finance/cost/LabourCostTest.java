package com.worth.ifs.application.finance.cost;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

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
        total = new BigDecimal(0);

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
    public void gettersShouldReturnCorrectValues(){
        Assert.assertEquals(id, labourCost.getId());
        Assert.assertEquals(role, labourCost.getRole());
        Assert.assertEquals(grossAnnualSalary, labourCost.getGrossAnnualSalary());
        Assert.assertEquals(rate, labourCost.getRate());
        Assert.assertEquals(labourDays, labourCost.getLabourDays());
        Assert.assertEquals(total, labourCost.getTotal());
    }

    @Test
    public void getRatePerDayShouldReturnZeroWhenWokingdaysIsZero(){
        Assert.assertEquals(BigDecimal.ZERO, labourCost.getRatePerDay(0));
    }
}
