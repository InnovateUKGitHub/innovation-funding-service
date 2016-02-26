package com.worth.ifs.address.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AddressControllerTest extends BaseControllerMockMVCTest<AddressController> {

    @Override
    protected AddressController supplyControllerUnderTest() {
        return new AddressController();
    }

    @Test
    public void findByIdShouldReturnOrganisation() throws Exception {
        when(addressServiceMock.findOne(1L)).thenReturn(serviceSuccess(newAddressResource().withId(1L).withAddressLine1("Address line 1").build()));

        mockMvc.perform(get("/address/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressLine1", is("Address line 1")))
                .andDo(document("address/findOne"));
    }

    @Test
    public void doLookupShouldReturnAddresses() throws Exception {
        int numberOfAddresses = 4;
        List<AddressResource> addressResources = newAddressResource().build(numberOfAddresses);
        when(addressLookupServiceMock.doLookup("BS348XU")).thenReturn(serviceSuccess(addressResources));
        mockMvc.perform(get("/address/doLookup/BS348XU"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfAddresses)))
                .andDo(document("address/lookup"));
    }
}
