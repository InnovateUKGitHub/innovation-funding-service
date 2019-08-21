package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.LABOUR;
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
    public void labourCostShouldReturnCorrectBaseAttributesTest() {
        assert (labourCost.getId().equals(id));
        assert (labourCost.getRole().equals(role));
        assert (labourCost.getGrossEmployeeCost().equals(grossEmployeeCost));
        assert (labourCost.getLabourDays().equals(labourDays));
        assert (labourCost.getDescription().equals(description));
    }

    @Test
    public void getRateTest() {
        Integer workingDaysPerYear = 232;
        BigDecimal ratePerDay = labourCost.getRate(workingDaysPerYear);
        BigDecimal expected = new BigDecimal(215.51724).setScale(5, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(expected, ratePerDay);
    }

    @Test
    public void rateNullLabourDaysTest() {
        Integer workingDaysPerYear = null;
        BigDecimal ratePerDay = labourCost.getRate(workingDaysPerYear);
        assertNull(ratePerDay);
    }

    @Test
    public void rateWithDivisionByZeroLabourDaysTest() {
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
    public void getLabourCostTotalTest() {
        Integer workingDaysPerYear = 232;
        BigDecimal totalLabourCost = labourCost.getTotal(workingDaysPerYear);
        BigDecimal expected = new BigDecimal(36206.89632).setScale(5, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal totalStoredLabourCost = labourCost.getTotal();
        assertEquals(expected, totalLabourCost);
        assertEquals(expected, totalStoredLabourCost);
    }

    @Test
    public void getLabourCostTotalWithoutLabourDaysTest() {
        Integer workingDaysPerYear = 232;
        labourCost.setLabourDays(null);
        BigDecimal totalLabourCost = labourCost.getTotal(workingDaysPerYear);
        assertEquals(BigDecimal.ZERO, totalLabourCost);
    }

    @Test
    public void setRoleTest() {
        labourCost.setRole("Developer");
        assertEquals("Developer", labourCost.getRole());
    }

    @Test
    public void getName() {
        String name = "Rob Garner";
        labourCost.setName(name);
        assertEquals(name, labourCost.getName());
    }

    @Test
    public void getCostType() {
        assertEquals(LABOUR, labourCost.getCostType());
    }
}