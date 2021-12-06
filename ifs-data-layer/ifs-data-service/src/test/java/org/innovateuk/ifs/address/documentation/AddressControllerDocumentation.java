package org.innovateuk.ifs.address.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.controller.AddressController;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.transactional.AddressLookupService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AddressDocs.addressResourceBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class AddressControllerDocumentation extends BaseControllerMockMVCTest<AddressController> {

    @Mock
    private AddressLookupService addressLookupServiceMock;

    @Override
    protected AddressController supplyControllerUnderTest() {
        return new AddressController();
    }

    @Test
    public void validate() throws Exception {
        String postCode = "BA12LN";

        when(addressLookupServiceMock.validatePostcode(postCode)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/address/validate-postcode/?postcode=" +  postCode)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }


    @Test
    public void lookup() throws Exception {
        int numberOfAddresses = 2;
        String postCode = "BS348XU";
        List<AddressResource> addressResources = addressResourceBuilder.build(numberOfAddresses);

        when(addressLookupServiceMock.doLookup(postCode)).thenReturn(serviceSuccess(addressResources));

        mockMvc.perform(get("/address/do-lookup/?lookup=" + postCode)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }
}
