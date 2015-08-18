package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CostValueTest {
    CostValue costValue;
    Cost cost;
    CostField costField;
    String value;

    @Before
    public void setUp() throws Exception {
        cost = new Cost(1L, "cost item", "cost description", 10, 1000d);
        costField = new CostField(1L, "NVP", "String");
        value = "19000";
        costValue = new CostValue(cost, costField, value);
    }

    @Test
    public void sectionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(costValue.getCost(), cost);
        Assert.assertEquals(costValue.getCostField(), costField);
        Assert.assertEquals(costValue.getValue(), value);
    }
}