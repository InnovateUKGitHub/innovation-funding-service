package org.innovateuk.ifs.address.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.transactional.AddressLookupService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AddressControllerTest extends BaseControllerMockMVCTest<AddressController> {

    @Mock
    private AddressLookupService addressLookupServiceMock;

    @Override
    protected AddressController supplyControllerUnderTest() {
        return new AddressController();
    }

    @Test
    public void doLookupShouldReturnAddresses() throws Exception {
        int numberOfAddresses = 4;
        String postCode = "BS348XU";
        List<AddressResource> addressResources = newAddressResource().build(numberOfAddresses);

        when(addressLookupServiceMock.doLookup(postCode)).thenReturn(serviceSuccess(addressResources));

        mockMvc.perform(get("/address/do-lookup?lookup=" + postCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfAddresses)));
    }

    @Test
    public void doLookupWithSpecialCharacters() throws Exception {
        int numberOfAddresses = 4;
        String postCode = "!@£ !@£$";
        List<AddressResource> addressResources = newAddressResource().build(numberOfAddresses);

        when(addressLookupServiceMock.doLookup(postCode)).thenReturn(serviceSuccess(addressResources));

        mockMvc.perform(get("/address/do-lookup?lookup=" + postCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfAddresses)));
    }
}
