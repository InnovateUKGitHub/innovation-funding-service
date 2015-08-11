package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CapitalUsageTest {
    CapitalUsage capitalUsage;
    Long id;

    String description;
    String newOrExisting;
    Integer deprecation;
    Double npv;
    Double residualValue;
    Integer utilisation;
    Double netCost;

    @Before
    public void setUp() throws Exception {
        id = 0l;
        description = "Capital usage description";
        newOrExisting = "Existing";
        deprecation = 12;
        npv = 100000d;
        residualValue = 1000d;
        utilisation = 5;
        netCost = 900d;

        capitalUsage = new CapitalUsage(id, description, newOrExisting, deprecation, npv,
                residualValue, utilisation, netCost);
    }

    @Test
    public void capitalUsageShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(capitalUsage.getId(), id);
        Assert.assertEquals(capitalUsage.getDescription(), description);
        Assert.assertEquals(capitalUsage.getNewOrExisting(), newOrExisting);
        Assert.assertEquals(capitalUsage.getNpv(), npv);
        Assert.assertEquals(capitalUsage.getResidualValue(), residualValue);
        Assert.assertEquals(capitalUsage.getUtilisation(), utilisation);
        Assert.assertEquals(capitalUsage.getNetCost(), netCost);
    }
}