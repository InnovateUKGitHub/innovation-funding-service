package org.innovateuk.ifs.address.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddressTypeTest {
    private AddressType addressType;
    private Long id;
    private String name;

    @Before
    public void setUp() throws Exception {
        id = 1L;
        name = "REGISTERED";
        addressType = new AddressType(name);
        addressType.setId(id);
    }

    @Test
    public void addresshouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(addressType.getId(), id);
        Assert.assertEquals(addressType.getName(), name);
    }
}
