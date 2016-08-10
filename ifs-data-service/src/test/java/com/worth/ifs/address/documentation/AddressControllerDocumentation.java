package com.worth.ifs.address.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.controller.AddressController;
import com.worth.ifs.address.resource.AddressResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.AddressDocs.addressResourceBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

public class AddressControllerDocumentation extends BaseControllerMockMVCTest<AddressController> {
    private RestDocumentationResultHandler document;

    @Override
    protected AddressController supplyControllerUnderTest() {
        return new AddressController();
    }

    @Before
    public void setup(){
        this.document = document("address/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void validate() throws Exception {
        String postCode = "BA12LN";

        when(addressLookupServiceMock.validatePostcode(postCode)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/address/validatePostcode/?postcode=" +  postCode))
                .andDo(this.document.snippets(
                        requestParameters(
                                parameterWithName("postcode").description("Postcode to validate")
                        )
                ));
    }


    @Test
    public void lookup() throws Exception {
        int numberOfAddresses = 2;
        String postCode = "BS348XU";
        List<AddressResource> addressResources = addressResourceBuilder.build(numberOfAddresses);

        when(addressLookupServiceMock.doLookup(postCode)).thenReturn(serviceSuccess(addressResources));

        mockMvc.perform(get("/address/doLookup/?lookup=" + postCode))
                .andDo(this.document.snippets(
                        requestParameters(
                                parameterWithName("lookup").description("Postcode to look up")
                        ),
                        responseFields(
                                fieldWithPath("[]id").description("Address Id"),
                                fieldWithPath("[]addressLine1").description("Address line1"),
                                fieldWithPath("[]addressLine2").description("Address line2"),
                                fieldWithPath("[]addressLine3").description("Address Line3"),
                                fieldWithPath("[]town").description("Town"),
                                fieldWithPath("[]county").description("County"),
                                fieldWithPath("[]postcode").description("Postcode"),
                                fieldWithPath("[]organisations[]").description("List of Organisations with this address")
                        )
                ));
    }
    @Test
    public void findOne() throws Exception {
        long id = 1;
        AddressResource addressResource = addressResourceBuilder.build();
        when(addressService.getById(id)).thenReturn(serviceSuccess(addressResource));

        mockMvc.perform(get("/address/{id}", id))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("Id of Address to find")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Address Id"),
                                fieldWithPath("addressLine1").description("Address line1"),
                                fieldWithPath("addressLine2").description("Address line2"),
                                fieldWithPath("addressLine3").description("Address Line3"),
                                fieldWithPath("town").description("Town"),
                                fieldWithPath("county").description("County"),
                                fieldWithPath("postcode").description("Postcode"),
                                fieldWithPath("organisations[]").description("List of Organisations with this address")
                        )
                ));
    }

}