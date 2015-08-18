package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TravelCostTest {
    TravelCost travelCost;

    Long id;
    String purpose;
    Integer numberOfTimes;
    Double cost;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        purpose = "Journey purpose";
        numberOfTimes = 10;
        cost = 1000d;

        travelCost = new TravelCost(id, purpose, numberOfTimes, cost);
    }

    @Test
    public void travelCostShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(travelCost.getId(), id);
        Assert.assertEquals(travelCost.getPurpose(), purpose);
        Assert.assertEquals(travelCost.getNumberOfTimes(), numberOfTimes);
        Assert.assertEquals(travelCost.getCost(), cost);
    }
}