package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class FinanceRowMetaValueTest {
    FinanceRowMetaValue costValue;
    FinanceRow cost;
    FinanceRowMetaField financeRowMetaField;
    String value;
    ApplicationFinance applicationFinance;
    BigDecimal price;

    @Before
    public void setUp() throws Exception {
        price  = new BigDecimal(1000);
        applicationFinance = new ApplicationFinance();
        cost = new ApplicationFinanceRow(1L, "cost key", "cost item", "cost description", 10, price, applicationFinance, FinanceRowType.FINANCE);
        financeRowMetaField = new FinanceRowMetaField(1L, "NVP", "String");
        value = "19000";
        costValue = new FinanceRowMetaValue(cost, financeRowMetaField, value);
    }

    @Test
    public void constructorsShouldCreateInstancesOnValidInput() throws Exception {
        new FinanceRowMetaValue();
        new FinanceRowMetaValue(financeRowMetaField, value);
        new FinanceRowMetaValue(cost, financeRowMetaField, value);
    }

    @Test
    public void costValueShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(costValue.getFinanceRowId(), cost.getId());
        Assert.assertEquals(costValue.getFinanceRowMetaField(), financeRowMetaField);
        Assert.assertEquals(costValue.getValue(), value);
    }

    @Test
    public void costValueShouldReturnCorrectAttributeValuesAfterSetters() throws Exception {
        FinanceRow newCost = new ApplicationFinanceRow(2L, "cost key", "cost item", "cost description", 10, price, applicationFinance, FinanceRowType.FINANCE);
        FinanceRowMetaField newFinanceRowMetaField = new FinanceRowMetaField(2L, "title","type");

        costValue.setFinanceRowId(newCost.getId());
        costValue.setFinanceRowMetaField(newFinanceRowMetaField);

        Assert.assertEquals(costValue.getFinanceRowId(), newCost.getId());
        Assert.assertEquals(costValue.getFinanceRowMetaField(), newFinanceRowMetaField);
    }
}
