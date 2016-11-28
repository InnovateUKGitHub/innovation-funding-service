package com.worth.ifs.address.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddressTypeResourceTest {
    private AddressTypeResource addressType;
    private Long id;
    private String name;

    @Before
    public void setUp() throws Exception {
        id = 1L;
        name = "REGISTERED";
        addressType = new AddressTypeResource();
        addressType.setId(id);
        addressType.setName(name);
    }

    @Test
    public void addresshouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(addressType.getId(), id);
        Assert.assertEquals(addressType.getName(), name);
    }
}
