package com.worth.ifs.domain;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
        CostCategory costCategory = new CostCategory();
        costItem = new Cost(id, item, description, quantity, cost, costCategory);
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