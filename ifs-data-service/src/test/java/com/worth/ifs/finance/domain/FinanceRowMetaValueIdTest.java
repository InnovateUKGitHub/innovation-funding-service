package com.worth.ifs.finance.domain;

import com.worth.ifs.finance.resource.FinanceRowMetaValueId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FinanceRowMetaValueIdTest {
    Long cost;
    Long costField;
    int hash;
    FinanceRowMetaValueId financeRowMetaValueId;

    @Before
    public void setUp() throws Exception {
        cost = 123456L;
        costField = 654321L;
        hash = cost.hashCode() + costField.hashCode();
        financeRowMetaValueId = new FinanceRowMetaValueId(cost,costField);
    }

    @Test
    public void constructorsShouldCreateInstancesOnValidInput() throws Exception {
        new FinanceRowMetaValueId();
        new FinanceRowMetaValueId(1234L, 7938L);
    }

    @Test
    public void gettersShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(financeRowMetaValueId.getFinanceRow(), cost);
        Assert.assertEquals(financeRowMetaValueId.getFinanceRowMetaField(), costField);
    }

    @Test
    public void equalsMethodShouldCheckForEquality() throws Exception {
        FinanceRowMetaValueId equalObject = new FinanceRowMetaValueId(cost,costField);
        FinanceRowMetaValueId inequalObject = new FinanceRowMetaValueId(cost+1L,costField-1L);
        Object differentObject = new Object();
        Assert.assertEquals(financeRowMetaValueId, equalObject);
        Assert.assertNotEquals(financeRowMetaValueId, inequalObject);
        Assert.assertNotEquals(financeRowMetaValueId, differentObject);
        Assert.assertNotEquals(financeRowMetaValueId, null);
    }
}