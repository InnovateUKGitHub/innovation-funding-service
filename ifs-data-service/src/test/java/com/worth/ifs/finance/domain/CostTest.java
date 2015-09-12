package com.worth.ifs.finance.domain;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CostTest {
    Cost costItem;
    Long id;
    String item;
    String description;
    Integer quantity;
    Double cost;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        item = "cost item";
        description = "description of cost item";
        quantity = 10;
        cost = 1000d;
        ApplicationFinance applicationFinance = new ApplicationFinance();
        Question question = new Question();
        costItem = new Cost(id, item, description, quantity, cost, applicationFinance, question);
    }

    @Test
    public void labourShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(costItem.getId(), id);
        Assert.assertEquals(costItem.getItem(), item);
        Assert.assertEquals(costItem.getDescription(), description);
        Assert.assertEquals(costItem.getQuantity(), quantity);
        Assert.assertEquals(costItem.getCost(), cost);
    }
}