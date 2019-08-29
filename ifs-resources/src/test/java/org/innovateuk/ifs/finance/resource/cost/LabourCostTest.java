package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LabourCostTest {

    private LabourCost labourCost;
    private Long id;
    private String key;
    private String role;
    private BigDecimal grossEmployeeCost;
    private Integer labourDays;
    private String description;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        key = "Labour";
        role = "Manager";
        grossEmployeeCost = new BigDecimal(50000);
        labourDays = 168;
        description = "";

        labourCost = new LabourCost(id, key, role, grossEmployeeCost, labourDays, description, 1L);
    }

    @Test
    public void labourCostShouldReturnCorrectBaseAttributes() {
        assertEquals(id, labourCost.getId());
        assertEquals(role, labourCost.getRole());
        assertEquals(grossEmployeeCost, labourCost.getGrossEmployeeCost());
        assertEquals(labourDays, labourCost.getLabourDays());
        assertEquals(description, labourCost.getDescription());
    }

    @Test
    public void getRate() {
        Integer workingDaysPerYear = 232;
        BigDecimal ratePerDay = labourCost.getRate(workingDaysPerYear);
        BigDecimal expected = new BigDecimal(215.51724).setScale(5, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(expected, ratePerDay);
    }

    @Test
    public void rateNullLabourDays() {
        Integer workingDaysPerYear = null;
        BigDecimal ratePerDay = labourCost.getRate(workingDaysPerYear);
        assertNull(ratePerDay);
    }

    @Test
    public void rateWithDivisionByZeroLabourDays() {
        int workingDaysPerYear = 0;
        BigDecimal ratePerDay = labourCost.getRate(workingDaysPerYear);
        assertEquals(BigDecimal.ZERO, ratePerDay);
    }

    @Test
    public void getRateWithoutGrossEmployeeCost() {
        int workingDaysPerYear = 50;
        labourCost.setGrossEmployeeCost(BigDecimal.ZERO);
        BigDecimal ratePerDay = labourCost.getRate(workingDaysPerYear);
        assertEquals(labourCost.getRate(), ratePerDay);
    }

    @Test
    public void getLabourCostTotal() {
        Integer workingDaysPerYear = 232;
        BigDecimal totalLabourCost = labourCost.getTotal(workingDaysPerYear);
        BigDecimal expected = new BigDecimal(36206.89632).setScale(5, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal totalStoredLabourCost = labourCost.getTotal();
        assertEquals(expected, totalLabourCost);
        assertEquals(expected, totalStoredLabourCost);
    }

    @Test
    public void getLabourCostTotalWithoutLabourDays() {
        Integer workingDaysPerYear = 232;
        labourCost.setLabourDays(null);
        BigDecimal totalLabourCost = labourCost.getTotal(workingDaysPerYear);
        assertEquals(BigDecimal.ZERO, totalLabourCost);
    }
}