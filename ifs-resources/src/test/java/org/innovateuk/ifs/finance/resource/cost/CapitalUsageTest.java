package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.CAPITAL_USAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CapitalUsageTest {

    private CapitalUsage capitalUsage;

    @Before
    public void setUp() {
        Long id = 1L;
        Integer deprecation = 12;
        String description = "";
        String existing = "New";
        BigDecimal npv = new BigDecimal(20000);
        BigDecimal residualValue = new BigDecimal(15000);
        Integer utilisation = 25;

        capitalUsage = new CapitalUsage(id, deprecation, description, existing, npv, residualValue, utilisation, 1L);
    }

    @Test
    public void getTotal() {
        assertEquals(BigDecimal.valueOf(1250).setScale(2, BigDecimal.ROUND_HALF_EVEN), capitalUsage.getTotal());
    }

    @Test
    public void totalMustBeZeroWhenDataIsNotAvailableTest() {
        CapitalUsage emptyCapitalUsage = new CapitalUsage(1L);
        assertEquals(BigDecimal.ZERO, emptyCapitalUsage.getTotal());
    }

    @Test
    public void getTotalNullNPV() {
        capitalUsage.setNpv(null);
        assertEquals(BigDecimal.ZERO, capitalUsage.getTotal());
    }

    @Test
    public void getTotalNullResidualValue() {
        capitalUsage.setResidualValue(null);
        assertEquals(BigDecimal.ZERO, capitalUsage.getTotal());
    }

    @Test
    public void getTotalNullUtilizationValue() {
        capitalUsage.setUtilisation(null);
        assertEquals(BigDecimal.ZERO, capitalUsage.getTotal());
    }

    @Test
    public void getTotalZeroNPV() {
        capitalUsage.setNpv(BigDecimal.ZERO);
        assertEquals(BigDecimal.valueOf(-3750).setScale(2, BigDecimal.ROUND_HALF_EVEN), capitalUsage.getTotal());
    }

    @Test
    public void getTotalZeroResidualVal() {
        capitalUsage.setResidualValue(BigDecimal.ZERO);
        assertEquals(BigDecimal.valueOf(5000).setScale(2, BigDecimal.ROUND_HALF_EVEN), capitalUsage.getTotal());
    }

    @Test
    public void getTotalZeroUtilisation() {
        capitalUsage.setUtilisation(0);
        assertEquals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN), capitalUsage.getTotal());
    }

    @Test
    public void getName() {
        assertEquals("capital_usage", capitalUsage.getName());
    }

    @Test
    public void isEmpty() {
        assertFalse(capitalUsage.isEmpty());
    }

    @Test
    public void getCostType() {
        assertEquals(CAPITAL_USAGE, capitalUsage.getCostType());
    }
}
