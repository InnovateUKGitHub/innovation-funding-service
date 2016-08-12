package com.worth.ifs.address.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AddressControllerTest extends BaseControllerMockMVCTest<AddressController> {

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

        mockMvc.perform(get("/address/doLookup?lookup=" + postCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfAddresses)));
    }

    @Test
    public void doLookupWithSpecialCharacters() throws Exception {
        int numberOfAddresses = 4;
        String postCode = "!@£ !@£$";
        List<AddressResource> addressResources = newAddressResource().build(numberOfAddresses);

        when(addressLookupServiceMock.doLookup(postCode)).thenReturn(serviceSuccess(addressResources));

        mockMvc.perform(get("/address/doLookup?lookup=" + postCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfAddresses)));
    }

    @Test
    public void getByIdShouldReturnAddress() throws Exception {
        AddressResource addressResource = newAddressResource().build();
        when(addressService.getById(addressResource.getId())).thenReturn(serviceSuccess(addressResource));
        mockMvc.perform(get("/address/{id}", addressResource.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(addressResource)));;
    }
}
