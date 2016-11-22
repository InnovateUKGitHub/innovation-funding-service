package com.worth.ifs.address.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddressResourceTest {
    AddressResource address;
    private Long id;

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String town;
    private String county;
    private String postcode;

    @Before
    public void setUp() throws Exception {
        id = 1L;
        addressLine1 = "Allies Computing Ltd";
        addressLine2 = "Fox Road";
        addressLine3 = "Framingham Pigot";
        town = "Norwich";
        county = "Norfolk";
        postcode = "NR14 7PZ";

        address = new AddressResource(addressLine1, addressLine2, addressLine3, town, county, postcode);
        address.setId(id);
    }

    @Test
    public void addresshouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(address.getId(), id);
        Assert.assertEquals(address.getAddressLine1(), addressLine1);
        Assert.assertEquals(address.getAddressLine2(), addressLine2);
        Assert.assertEquals(address.getAddressLine3(), addressLine3);
        Assert.assertEquals(address.getTown(), town);
        Assert.assertEquals(address.getCounty(), county);
        Assert.assertEquals(address.getPostcode(), postcode);
    }
}
