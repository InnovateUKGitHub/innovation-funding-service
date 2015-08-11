package com.worth.ifs.domain;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MaterialsTest {
    Materials materials;
    Long id;

    String item;
    Integer quantity;
    Integer costPerItem;

    @Before
    public void setUp() throws Exception {
        id = 0l;
        item = "Material item";
        quantity = 100;
        costPerItem = 20;

        materials = new Materials(id, item, quantity, costPerItem);
    }

    @Test
    public void materialsShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(materials.getId(), id);
        Assert.assertEquals(materials.getItem(), item);
        Assert.assertEquals(materials.getQuantity(), quantity);
        Assert.assertEquals(materials.getCostPerItem(), costPerItem);
    }
}