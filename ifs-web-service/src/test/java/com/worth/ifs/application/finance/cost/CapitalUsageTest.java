package com.worth.ifs.application.finance.cost;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class CapitalUsageTest {

    private Long id = 1L;
    private Integer deprecation = 12;
    private String description = "";
    private String existing = "New";
    private BigDecimal npv = new BigDecimal(20000);
    private BigDecimal residualValue = new BigDecimal(15000);
    private Integer utilisation = new Integer(25);
    private CapitalUsage capitalUsage;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    public CapitalUsage initCapitalUsage(){
        capitalUsage = new CapitalUsage(id, deprecation, description, existing, npv, residualValue, utilisation);
        return capitalUsage;
    }

    @Test
    public void testGetTotal() throws Exception {
        initCapitalUsage();
        assertEquals(BigDecimal.valueOf(1250).setScale(2), capitalUsage.getTotal());
    }
    @Test
    public void testGetTotalNullNPV() throws Exception {
        npv = null;
        initCapitalUsage();
        assertEquals(BigDecimal.ZERO, capitalUsage.getTotal());
    }
    @Test
    public void testGetTotalNullResidualValue() throws Exception {
        residualValue = null;
        initCapitalUsage();
        assertEquals(BigDecimal.ZERO, capitalUsage.getTotal());
    }
    @Test
    public void testGetTotalNullUtilizationValue() throws Exception {
        utilisation = null;
        initCapitalUsage();
        assertEquals(BigDecimal.ZERO, capitalUsage.getTotal());
    }
    @Test
    public void testGetTotalZeroNPV() throws Exception {
        npv = BigDecimal.ZERO;
        initCapitalUsage();
        assertEquals(BigDecimal.valueOf(-3750).setScale(2), capitalUsage.getTotal());
    }
    @Test
    public void testGetTotalZeroResidualVal() throws Exception {
        residualValue = BigDecimal.ZERO;
        initCapitalUsage();
        assertEquals(BigDecimal.valueOf(5000).setScale(2), capitalUsage.getTotal());
    }
    @Test
    public void testGetTotalZeroUtilisation() throws Exception {
        utilisation = 0;
        initCapitalUsage();
        assertEquals(BigDecimal.ZERO, capitalUsage.getTotal());
    }
}