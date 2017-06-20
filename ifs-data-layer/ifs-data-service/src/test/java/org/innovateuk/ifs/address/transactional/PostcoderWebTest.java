package org.innovateuk.ifs.address.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
