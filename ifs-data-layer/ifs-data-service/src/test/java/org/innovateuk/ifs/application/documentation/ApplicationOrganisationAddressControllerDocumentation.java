package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.controller.ApplicationOrganisationAddressController;
import org.innovateuk.ifs.application.transactional.ApplicationOrganisationAddressService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AddressDocs.addressResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationOrganisationAddressControllerDocumentation extends BaseControllerMockMVCTest<ApplicationOrganisationAddressController> {

    @Mock
    private ApplicationOrganisationAddressService applicationOrganisationAddressService;

    @Override
    protected ApplicationOrganisationAddressController supplyControllerUnderTest() {
        return new ApplicationOrganisationAddressController();
    }

    @Test
    public void getAddress() throws Exception {
        long applicationId = 1L;
        long organisationId = 2L;
        OrganisationAddressType type = OrganisationAddressType.INTERNATIONAL;

        AddressResource address = newAddressResource().build();

        when(applicationOrganisationAddressService.getAddress(applicationId, organisationId, type)).thenReturn(serviceSuccess(address));

        mockMvc.perform(get("/application/{applicationId}/organisation/{organisationId}/address/{addressType}", applicationId, organisationId, type)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application the address is linked to."),
                                parameterWithName("organisationId").description("Id of the organisation the address is linked to."),
                                parameterWithName("addressType").description("The type of address being requested.")
                        ),
                        responseFields(addressResourceFields)
                ));
    }

    @Test
    public void updateAddress() throws Exception {
        long applicationId = 1L;
        long organisationId = 2L;
        OrganisationAddressType type = OrganisationAddressType.INTERNATIONAL;
        AddressResource address = newAddressResource().build();

        when(applicationOrganisationAddressService.updateAddress(applicationId, organisationId, type, address)).thenReturn(serviceSuccess(address));

        mockMvc.perform(put("/application/{applicationId}/organisation/{organisationId}/address/{addressType}", applicationId, organisationId, type)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application the address is linked to."),
                                parameterWithName("organisationId").description("Id of the organisation the address is linked to."),
                                parameterWithName("addressType").description("The type of address being requested.")
                        ),
                        relaxedRequestFields(addressResourceFields)
                ));
    }

}
