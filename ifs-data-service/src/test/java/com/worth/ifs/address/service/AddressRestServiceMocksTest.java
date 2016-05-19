package com.worth.ifs.address.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.address.resource.AddressResource;
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
        addressRestService.addressRestUrl = addressRestURL;
        return addressRestService;
    }

    @Test
    public void testDoLookup() throws Exception{
        String expectedUrl = addressRestURL + "/doLookup/BS348XU";
        List<AddressResource> returnedAddresses = newAddressResource().build(4);
        setupGetWithRestResultExpectations(expectedUrl, addressResourceListType(), returnedAddresses);

        // now run the method under test
        List<AddressResource> addresses = service.doLookup("BS348XU").getSuccessObject();
        assertNotNull(addresses);
        assertEquals(returnedAddresses, addresses);
    }
}
