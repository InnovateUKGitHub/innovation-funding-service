package org.innovateuk.ifs.address.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.addressResourceListType;
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
    public void doLookup() throws Exception{
        String postcode = "BS348XU";
        String expectedUrl = addressRestURL + "/do-lookup?lookup=" + postcode;
        List<AddressResource> returnedAddresses = Stream.of(1, 2, 3, 4).map(i -> new AddressResource()).collect(Collectors.toList());// newAddressResource().build(4);
        setupGetWithRestResultAnonymousExpectations(expectedUrl, addressResourceListType(), returnedAddresses, HttpStatus.OK);

        // now run the method under test
        List<AddressResource> addresses = service.doLookup(postcode).getSuccess();
        assertNotNull(addresses);
        assertEquals(returnedAddresses, addresses);
    }
}
