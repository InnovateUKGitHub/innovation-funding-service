package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PersonnelCostTest {

    private PersonnelCost personnelCost;
    private Long id;
    private String key;
    private String role;
    private BigDecimal grossEmployeeCost;
    private Integer labourDays;
    private String description;
    private BigDecimal rate;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        key = "Labour";
        role = "Manager";
        grossEmployeeCost = new BigDecimal(50000);
        labourDays = 168;
        description = "";
        rate = BigDecimal.ZERO;

        personnelCost = new PersonnelCost(id, key, role, grossEmployeeCost, labourDays, description, 1L, rate, false);
    }

    @Test
    public void personnelCostShouldReturnCorrectBaseAttributes() {
        assertEquals(id, personnelCost.getId());
        assertEquals(role, personnelCost.getRole());
        assertEquals(grossEmployeeCost, personnelCost.getGrossEmployeeCost());
        assertEquals(labourDays, personnelCost.getLabourDays());
        assertEquals(description, personnelCost.getDescription());
    }

    @Test
    public void getRate() {
        Integer workingDaysPerYear = 232;
        BigDecimal ratePerDay = personnelCost.getRate(workingDaysPerYear);
        BigDecimal expected = new BigDecimal(215.51724).setScale(5, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(expected, ratePerDay);
    }

    @Test
    public void rateNullLabourDays() {
        Integer workingDaysPerYear = null;
        BigDecimal ratePerDay = personnelCost.getRate(workingDaysPerYear);
        assertNull(ratePerDay);
    }

    @Test
    public void rateWithDivisionByZeroLabourDays() {
        int workingDaysPerYear = 0;
        BigDecimal ratePerDay = personnelCost.getRate(workingDaysPerYear);
        assertEquals(BigDecimal.ZERO, ratePerDay);
    }

    @Test
    public void getRateWithoutGrossEmployeeCost() {
        int workingDaysPerYear = 50;
        personnelCost.setGrossEmployeeCost(BigDecimal.ZERO);
        BigDecimal ratePerDay = personnelCost.getRate(workingDaysPerYear);
        assertEquals(personnelCost.getRate(), ratePerDay);
    }

    @Test
    public void getPersonnelCostTotal() {
        Integer workingDaysPerYear = 232;
        BigDecimal totalLabourCost = personnelCost.getTotal(workingDaysPerYear);
        BigDecimal expected = new BigDecimal(36206.89632).setScale(5, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal totalStoredLabourCost = personnelCost.getTotal();
        assertEquals(expected, totalLabourCost);
        assertEquals(expected, totalStoredLabourCost);
    }

    @Test
    public void getPersonnelTotalWithoutLabourDays() {
        Integer workingDaysPerYear = 232;
        personnelCost.setLabourDays(null);
        BigDecimal totalLabourCost = personnelCost.getTotal(workingDaysPerYear);
        assertEquals(BigDecimal.ZERO, totalLabourCost);
    }

    @Test
    public void getPersonnelCostForThirdPartyOfgem() {
        key = "";
        role = "Manager";
        labourDays = 100;
        description = "";
        rate = BigDecimal.ONE;

        Integer workingDaysPerYear = 0;

        personnelCost = new PersonnelCost(id, key, role, grossEmployeeCost, labourDays, description, 1L, rate, true);

        assertEquals("third-party-ofgem", personnelCost.getName());
        assertEquals(0, BigDecimal.ONE.compareTo(personnelCost.getRate(workingDaysPerYear)));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(personnelCost.getTotalWithoutWorkingDays()));
    }
}