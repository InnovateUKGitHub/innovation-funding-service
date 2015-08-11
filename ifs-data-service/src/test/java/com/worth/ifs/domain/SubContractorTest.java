package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SubContractorTest {
    SubContractor subContractor;
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

        subContractor = new SubContractor(id, name, country, role, cost);
    }

    @Test
    public void sectionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(subContractor.getId(), id);
        Assert.assertEquals(subContractor.getName(), name);
        Assert.assertEquals(subContractor.getCountry(), country);
        Assert.assertEquals(subContractor.getRole(), role);
        Assert.assertEquals(subContractor.getCost(), cost);
    }
}