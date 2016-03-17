package com.worth.ifs.address.documentation;

import java.util.List;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.controller.AddressController;
import com.worth.ifs.address.resource.AddressResource;

import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.AddressDocs.addressResourceBuilder;
import static com.worth.ifs.documentation.AddressDocs.addressResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class AddressControllerDocumentation extends BaseControllerMockMVCTest<AddressController> {
    @Override
    protected AddressController supplyControllerUnderTest() {
        return new AddressController();
    }

    @Test
    public void documentValidatePostcode() throws Exception {
        String postCode = "BA12LN";

        when(addressLookupServiceMock.validatePostcode(postCode)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/address/validatePostcode/{postcode}", postCode))
                .andDo(document(
                        "address/validate",
                        pathParameters(
                                parameterWithName("postcode").description("Postcode to validate")
                        )
                ));
    }

    @Test
    public void documentFindById() throws Exception {
        Long addressId = 1L;

        when(addressServiceMock.findOne(addressId)).thenReturn(serviceSuccess(addressResourceBuilder.build()));

        mockMvc.perform(get("/address/{id}", addressId))
                .andDo(document(
                        "address/findOne",
                        pathParameters(
                                parameterWithName("id").description("Id of the address that needs to be found")
                        ),
                        responseFields(addressResourceFields)
                ));
    }

    @Test
    public void documentLookupAddress() throws Exception {
        int numberOfAddresses = 2;
        String postCode = "BS348XU";
        List<AddressResource> addressResources = addressResourceBuilder.build(numberOfAddresses);

        when(addressLookupServiceMock.doLookup(postCode)).thenReturn(serviceSuccess(addressResources));

        mockMvc.perform(get("/address/doLookup/{postcode}", postCode))
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
}