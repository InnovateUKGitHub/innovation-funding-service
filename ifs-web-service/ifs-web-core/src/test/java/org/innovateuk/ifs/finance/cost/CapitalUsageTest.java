package org.innovateuk.ifs.finance.cost;

import org.innovateuk.ifs.finance.resource.cost.CapitalUsage;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.CAPITAL_USAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CapitalUsageTest {

    private Long id;
    private Integer deprecation;
    private String description;
    private String existing;
    private BigDecimal npv;
    private BigDecimal residualValue;
    private Integer utilisation;
    private CapitalUsage capitalUsage;

    @Before
    public void setUp() {
        id = 1L;
        deprecation = 12;
        description = "";
        existing = "New";
        npv = new BigDecimal(20000);
        residualValue = new BigDecimal(15000);
        utilisation = 25;

        capitalUsage = new CapitalUsage(id, deprecation, description, existing, npv, residualValue, utilisation);
    }

    @Test
    public void getTotal() {
        assertEquals(BigDecimal.valueOf(1250).setScale(2, BigDecimal.ROUND_HALF_EVEN), capitalUsage.getTotal());
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
    public void getMinRows() {
        assertEquals(0, capitalUsage.getMinRows());
    }

    @Test
    public void getCostType() {
        assertEquals(CAPITAL_USAGE, capitalUsage.getCostType());
    }
}
