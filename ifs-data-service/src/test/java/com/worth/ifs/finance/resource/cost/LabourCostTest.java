package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.resource.cost.LabourCost;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LabourCostTest {

    LabourCost labourCost;
    private Long id;
    private String key;
    private String role;
    private BigDecimal grossAnnualSalary;
    private Integer labourDays;
    private String description;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        key = "Labour";
        role = "Manager";
        grossAnnualSalary = new BigDecimal(50000);
        labourDays = new Integer(168);
        description = "";

        labourCost = new LabourCost(id, key, role, grossAnnualSalary, labourDays,  description);
    }

    @Test
    public void labourCostShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(labourCost.getId().equals(id));
        assert(labourCost.getRole().equals(role));
        assert(labourCost.getGrossAnnualSalary().equals(grossAnnualSalary));
        assert(labourCost.getLabourDays().equals(labourDays));
        assert(labourCost.getDescription().equals(description));
    }

    @Test
    public void getRateTest() throws Exception {
        Integer workingDaysPerYear = new Integer(232);
        BigDecimal ratePerDay = labourCost.getRate(workingDaysPerYear);
        BigDecimal expected = new BigDecimal(215.51724).setScale(5, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(expected, ratePerDay);
    }

    @Test
    public void rateNullLabourDaysTest() throws Exception {
        Integer workingDaysPerYear = null;
        BigDecimal ratePerDay = labourCost.getRate(workingDaysPerYear);
        assertNull(ratePerDay);
    }

    @Test
    public void rateWithDivisionByZeroLabourDaysTest() throws Exception {
        int workingDaysPerYear = 0;
        BigDecimal ratePerDay = labourCost.getRate(workingDaysPerYear);
        assertEquals(BigDecimal.ZERO, ratePerDay);
    }

    @Test
    public void getRateWithoutGrossAnnualSalaryTest() throws Exception {
        int workingDaysPerYear = 50;
        labourCost.setGrossAnnualSalary(BigDecimal.ZERO);
        BigDecimal ratePerDay = labourCost.getRate(workingDaysPerYear);
        assertEquals(labourCost.getRate(), ratePerDay);
    }

    @Test
    public void getLabourCostTotalTest() throws Exception {
        Integer workingDaysPerYear = new Integer(232);
        BigDecimal totalLabourCost = labourCost.getTotal(workingDaysPerYear);
        BigDecimal expected = new BigDecimal(36206.89632).setScale(5, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal totalStoredLabourCost = labourCost.getTotal();
        assertEquals(expected, totalLabourCost);
        assertEquals(expected, totalStoredLabourCost);
    }

    @Test
    public void getLabourCostTotalWithoutLabourDaysTest() throws Exception {
        Integer workingDaysPerYear = new Integer(232);
        labourCost.setLabourDays(null);
        BigDecimal totalLabourCost = labourCost.getTotal(workingDaysPerYear);
        assertEquals(BigDecimal.ZERO, totalLabourCost);
    }

    @Test
    public void setRoleTest() throws Exception {
        labourCost.setRole("Developer");
        assertEquals("Developer", labourCost.getRole());
    }
}
