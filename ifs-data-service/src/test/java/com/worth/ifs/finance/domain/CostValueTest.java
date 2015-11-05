package com.worth.ifs.finance.domain;

import com.worth.ifs.application.domain.Question;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CostValueTest {
    CostValue costValue;
    Cost cost;
    CostField costField;
    String value;
    ApplicationFinance applicationFinance;
    Question question;

    @Before
    public void setUp() throws Exception {
        applicationFinance = new ApplicationFinance();
        question = new Question();
        cost = new Cost(1L, "cost item", "cost description", 10, 1000d, applicationFinance, question);
        costField = new CostField(1L, "NVP", "String");
        value = "19000";
        costValue = new CostValue(cost, costField, value);
    }

    @Test
    public void constructorsShouldCreateInstancesOnValidInput() throws Exception {
        new CostValue();
        new CostValue(costField, value);
        new CostValue(cost, costField, value);
    }

    @Test
    public void costValueShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(costValue.getCost(), cost);
        Assert.assertEquals(costValue.getCostField(), costField);
        Assert.assertEquals(costValue.getValue(), value);
    }

    @Test
    public void costValueShouldReturnCorrectAttributeValuesAfterSetters() throws Exception {
        Cost newCost = new Cost(2L, "cost item", "cost description", 10, 1000d, applicationFinance, question);
        CostField newCostField = new CostField(2l,"title","type");

        costValue.setCost(newCost);
        costValue.setCostField(newCostField);

        Assert.assertEquals(costValue.getCost(), newCost);
        Assert.assertEquals(costValue.getCostField(), newCostField);
    }
}