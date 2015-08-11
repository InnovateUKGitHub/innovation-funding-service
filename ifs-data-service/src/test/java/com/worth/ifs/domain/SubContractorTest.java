package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SubContractorTest {
    Subcontractor subcontractor;
    Long id;

    String name;
    String country;
    String role;
    Double cost;


    @Before
    public void setUp() throws Exception {
        id = 0l;
        name = "Subcontractor name";
        country = "UK";
        role = "Subcontractor role";
        cost = 100000d;

        subcontractor = new Subcontractor(id, name, country, role, cost);
    }

    @Test
    public void sectionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(subcontractor.getId(), id);
        Assert.assertEquals(subcontractor.getName(), name);
        Assert.assertEquals(subcontractor.getCountry(), country);
        Assert.assertEquals(subcontractor.getRole(), role);
        Assert.assertEquals(subcontractor.getCost(), cost);
    }
}