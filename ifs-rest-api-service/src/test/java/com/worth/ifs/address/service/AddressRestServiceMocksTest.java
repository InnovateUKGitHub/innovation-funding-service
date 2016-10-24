package com.worth.ifs.address.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.address.resource.AddressResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.addressResourceListType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AddressRestServiceMocksTest extends BaseRestServiceUnitTest<AddressRestServiceImpl> {
    private static final String addressRestURL = "/address";

    @Override
    protected AddressRestServiceImpl registerRestServiceUnderTest() {
        AddressRestServiceImpl addressRestService = new AddressRestServiceImpl();
        return addressRestService;
    }

    @Test
    public void testDoLookup() throws Exception{
        String postcode = "BS348XU";
        String expectedUrl = addressRestURL + "/doLookup?lookup=" + postcode;
        List<AddressResource> returnedAddresses = newAddressResource().build(4);
        setupGetWithRestResultExpectations(expectedUrl, addressResourceListType(), returnedAddresses);

        // now run the method under test
        List<AddressResource> addresses = service.doLookup(postcode).getSuccessObject();
        assertNotNull(addresses);
        assertEquals(returnedAddresses, addresses);
    }

    @Test
    public void testGetById(){
        AddressResource addressResource = newAddressResource().build();
        String url = addressRestURL + "/" + addressResource.getId();
        setupGetWithRestResultExpectations(url, AddressResource.class, addressResource);

        AddressResource returnedAddressResource = service.getById(addressResource.getId()).getSuccessObject();
        assertNotNull(returnedAddressResource);
        Assert.assertEquals(returnedAddressResource, addressResource);
    }
}
