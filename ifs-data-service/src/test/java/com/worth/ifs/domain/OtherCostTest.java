package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OtherCostTest {
    OtherCost otherCost;

    Long id;
    String description;
    Double cost;

    @Before
    public void setUp() throws Exception {
        id = 0l;
        description = "Other cost";
        cost = 1000d;

        otherCost = new OtherCost(id, description, cost);
    }

    @Test
    public void otherCostShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(otherCost.getId(), id);
        Assert.assertEquals(otherCost.getDescription(), description);
        Assert.assertEquals(otherCost.getCost(), cost);
    }

}