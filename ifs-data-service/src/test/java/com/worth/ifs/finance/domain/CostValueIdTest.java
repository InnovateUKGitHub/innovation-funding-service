package com.worth.ifs.finance.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CostValueIdTest {
    Long cost;
    Long costField;
    int hash;
    CostValueId costValueId;

    @Before
    public void setUp() throws Exception {
        cost = 123456L;
        costField = 654321L;
        hash = cost.hashCode() + costField.hashCode();
        costValueId = new CostValueId(cost,costField);
    }

    @Test
    public void constructorsShouldCreateInstancesOnValidInput() throws Exception {
        new CostValueId();
        new CostValueId(1234L, 7938L);
    }

    @Test
    public void gettersShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(costValueId.getCost(), cost);
        Assert.assertEquals(costValueId.getCostField(), costField);
    }

    @Test
    public void equalsMethodShouldCheckForEquality() throws Exception {
        CostValueId equalObject = new CostValueId(cost,costField);
        CostValueId inequalObject = new CostValueId(cost+1L,costField-1L);
        Object differentObject = new Object();
        Assert.assertEquals(costValueId, equalObject);
        Assert.assertNotEquals(costValueId, inequalObject);
        Assert.assertNotEquals(costValueId, differentObject);
        Assert.assertNotEquals(costValueId, null);
    }
}