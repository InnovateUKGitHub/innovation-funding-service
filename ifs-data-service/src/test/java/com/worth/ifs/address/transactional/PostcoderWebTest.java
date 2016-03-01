package com.worth.ifs.address.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PostcoderWebTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private PostcoderWeb postcoderWeb = new PostcoderWeb();

    @Test
    public void testEmptyLookup() {
        ServiceResult<List<AddressResource>> lookupResult = postcoderWeb.doLookup("");
        List<AddressResource> addressResources = lookupResult.getSuccessObject();
        assertEquals(0, addressResources.size());
    }

    @Test
    public void testDummyDataLookup() {
        String postcode = "BS348XU";

        ServiceResult<List<AddressResource>> lookupResult = postcoderWeb.doLookup(postcode);
        List<AddressResource> addressResources = lookupResult.getSuccessObject();
        assertEquals(2, addressResources.size());
    }
}
