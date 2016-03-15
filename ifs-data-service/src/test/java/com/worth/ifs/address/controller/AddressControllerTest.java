package com.worth.ifs.address.controller;

import java.util.List;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressResource;

import org.junit.Test;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AddressControllerTest extends BaseControllerMockMVCTest<AddressController> {

    @Override
    protected AddressController supplyControllerUnderTest() {
        return new AddressController();
    }

    @Test
    public void findByIdShouldReturnOrganisation() throws Exception {
        Long addressId = 1L;

        when(addressServiceMock.findOne(addressId)).thenReturn(serviceSuccess(newAddressResource().withId(addressId).withAddressLine1("Address line 1").build()));

        mockMvc.perform(get("/address/{id}", addressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressLine1", is("Address line 1")))
                .andDo(document(
                    "address/findOne",
                    pathParameters(
                        parameterWithName("id").description("Id of the address that needs to be found")
                    ),
                    responseFields(
                        fieldWithPath("id").description("id of the address"),
                        fieldWithPath("addressLine1").description("first addressLine"),
                        fieldWithPath("addressLine2").description("second addressLine"),
                        fieldWithPath("addressLine3").description("third addressLine"),
                        fieldWithPath("town").description("fourth addressLine"),
                        fieldWithPath("county").description("county where requested address is located"),
                        fieldWithPath("postcode").description("postcode of the requested address")
                    )
                ));
    }

    @Test
    public void doLookupShouldReturnAddresses() throws Exception {
        int numberOfAddresses = 4;
        String postCode = "BS348XU";
        List<AddressResource> addressResources = newAddressResource().build(numberOfAddresses);

        when(addressLookupServiceMock.doLookup(postCode)).thenReturn(serviceSuccess(addressResources));

        mockMvc.perform(get("/address/doLookup/{postcode}", postCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfAddresses)))
                .andDo(document(
                    "address/lookup",
                    pathParameters(
                        parameterWithName("postcode").description("Postcode to look up")
                    ),
                    responseFields(
                        fieldWithPath("[]").description("list with the addresses the requesting user has access to. ")
                    )
                ));
    }

    @Test
    public void documentValidatePostcode() throws Exception {
        String postCode = "BA12LN";

        when(addressLookupServiceMock.validatePostcode(postCode)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/address/validatePostcode/{postcode}", postCode))
                .andExpect(status().isOk())
                .andDo(document(
                    "address/validate",
                    pathParameters(
                        parameterWithName("postcode").description("Postcode to validate")
                    )
                ));
    }
}
