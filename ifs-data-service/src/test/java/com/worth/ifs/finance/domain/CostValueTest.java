package com.worth.ifs.finance.domain;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.domain.CostValue;
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
        ApplicationFinance applicationFinance = new ApplicationFinance();
        Question question = new Question();
        cost = new Cost(1L, "cost item", "cost description", 10, 1000d, applicationFinance, question);
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