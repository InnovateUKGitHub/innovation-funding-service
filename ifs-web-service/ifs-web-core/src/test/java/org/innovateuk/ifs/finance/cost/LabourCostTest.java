package org.innovateuk.ifs.finance.cost;

import org.innovateuk.ifs.finance.resource.cost.LabourCost;
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
    private String key;
    private String role;
    private BigDecimal grossEmployeeCost;
    private Integer labourDays;
    private BigDecimal rate;
    private String description;
    private BigDecimal total;
    private LabourCost labourCost;

    @Before
    public void setup() {
        id = 1L;
        key = "working_days_per_year";
        role = "role";
        grossEmployeeCost = new BigDecimal(100000);
        labourDays = 100;
        rate = new BigDecimal(1000);
        description = "description";
        total = BigDecimal.ZERO;
        labourCost = new LabourCost(id, key, role, grossEmployeeCost, labourDays, description);
        rate = labourCost.getRate(labourDays);
        total = labourCost.getTotal(labourDays);
    }

    @Test
    public void getRatePerDayShouldNotFailOnInfiniteDivisions(){
        grossEmployeeCost = new BigDecimal(10);
        labourDays = 3;
        LabourCost labour = new LabourCost(id, key, role, grossEmployeeCost, labourDays, description);
        BigDecimal result = grossEmployeeCost.divide(new BigDecimal(labourDays), 5, RoundingMode.HALF_EVEN);

        Assert.assertEquals(result, labour.getRate(labourDays));
    }

    @Test
    public void gettersShouldReturnCorrectValues(){
        Assert.assertEquals(id, labourCost.getId());
        Assert.assertEquals(key, labourCost.getName());
        Assert.assertEquals(role, labourCost.getRole());
        Assert.assertEquals(grossEmployeeCost, labourCost.getGrossEmployeeCost());
        Assert.assertEquals(rate, labourCost.getRate());
        Assert.assertEquals(labourDays, labourCost.getLabourDays());
        Assert.assertEquals(total, labourCost.getTotal());
    }

    @Test
    public void getRatePerDayShouldReturnZeroWhenWorkingDaysIsZero(){
        Assert.assertEquals(BigDecimal.ZERO, labourCost.getRate(0));
    }

    @Test
    public void getTotalShouldReturnZeroWhenLabourDaysIsNull(){
        labourCost.setLabourDays(null);

        Assert.assertNull(labourCost.getLabourDays());
        Assert.assertEquals(BigDecimal.ZERO, labourCost.getTotal(labourDays));
    }

    @Test
    public void getTotalShouldReturnZeroWhenRateIsNull(){
        labourCost = new LabourCost(id, key, role, grossEmployeeCost, labourDays, description);

        Assert.assertNull(labourCost.getRate());
        Assert.assertEquals(BigDecimal.ZERO, labourCost.getTotal(0));
    }

    @Test
    public void setGrossAnnualSalaryShouldNotThrowAnError(){
        labourCost.setGrossEmployeeCost(new BigDecimal(123));
    }

    @Test
    public void setGrossAnnualSalaryShouldNotThrowAnErrorOnNegativeValue(){
        labourCost.setGrossEmployeeCost(new BigDecimal(-123));
    }

    @Test
    public void setGrossAnnualSalaryShouldNotThrowAnErrorOnNull(){
        labourCost.setGrossEmployeeCost(null);
    }

    @Test
    public void setRoleShouldNotThrowAnErrorOnNull(){
        labourCost.setRole(null);
    }
}
