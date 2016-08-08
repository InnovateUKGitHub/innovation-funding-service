package com.worth.ifs.finance.domain;

import com.worth.ifs.application.domain.Question;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class FinanceRowMetaValueTest {
    FinanceRowMetaValue costValue;
    Cost cost;
    CostField costField;
    String value;
    ApplicationFinance applicationFinance;
    Question question;
    BigDecimal price;

    @Before
    public void setUp() throws Exception {
        price  = new BigDecimal(1000);
        applicationFinance = new ApplicationFinance();
        question = new Question();
        cost = new Cost(1L, "cost key", "cost item", "cost description", 10, price, applicationFinance, question);
        costField = new CostField(1L, "NVP", "String");
        value = "19000";
        costValue = new FinanceRowMetaValue(cost, costField, value);
    }

    @Test
    public void constructorsShouldCreateInstancesOnValidInput() throws Exception {
        new FinanceRowMetaValue();
        new FinanceRowMetaValue(costField, value);
        new FinanceRowMetaValue(cost, costField, value);
    }

    @Test
    public void costValueShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(costValue.getCost(), cost);
        Assert.assertEquals(costValue.getCostField(), costField);
        Assert.assertEquals(costValue.getValue(), value);
    }

    @Test
    public void costValueShouldReturnCorrectAttributeValuesAfterSetters() throws Exception {
        Cost newCost = new Cost(2L, "cost key", "cost item", "cost description", 10, price, applicationFinance, question);
        CostField newCostField = new CostField(2L,"title","type");

        costValue.setCost(newCost);
        costValue.setCostField(newCostField);

        Assert.assertEquals(costValue.getCost(), newCost);
        Assert.assertEquals(costValue.getCostField(), newCostField);
    }
}