package org.innovateuk.ifs.organisation.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.builder.AddressTypeResourceBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.documentation.AddressDocs;
import org.innovateuk.ifs.documentation.OrganisationAddressDocs;
import org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder;
import org.innovateuk.ifs.organisation.controller.OrganisationAddressController;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationAddressService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganisationAddressControllerDocumentation extends BaseControllerMockMVCTest<OrganisationAddressController> {

    @Mock
    private OrganisationAddressService organisationAddressServiceMock;
    
    @Override
    protected OrganisationAddressController supplyControllerUnderTest() {
        return new OrganisationAddressController();
    }

    @Test
    public void findById() throws Exception {

        long id = 1L;

        AddressResource addressResource = AddressDocs.addressResourceBuilder.build();
        AddressTypeResource addressTypeResource = AddressTypeResourceBuilder.newAddressTypeResource().withId(11L).withName("PROJECT").build();
        OrganisationAddressResource organisationAddressResource = OrganisationAddressResourceBuilder.newOrganisationAddressResource()
                .withOrganisation(2L)
                .withAddress(addressResource)
                .withAddressType(addressTypeResource)
                .build();
        when(organisationAddressServiceMock.findOne(id)).thenReturn(serviceSuccess(organisationAddressResource));

        mockMvc.perform(get("/organisationaddress/{id}", id)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(organisationAddressResource)))
                .andDo(document("organisationaddress/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the Organisation Address for which Organisation Address is being retrieved")
                        ),
                        responseFields(OrganisationAddressDocs.organisationAddressResourceFields)
                ));

        verify(organisationAddressServiceMock).findOne(id);
    }

    @Test
    public void findByOrganisationIdAndAddressId() throws Exception {

        long organisationId = 1L;
        long addressId = 2L;

        AddressResource addressResource = AddressDocs.addressResourceBuilder.build();
        AddressTypeResource addressTypeResource = AddressTypeResourceBuilder.newAddressTypeResource().withId(11L).withName("PROJECT").build();
        OrganisationAddressResource organisationAddressResource = OrganisationAddressResourceBuilder.newOrganisationAddressResource()
                .withOrganisation(2L)
                .withAddress(addressResource)
                .withAddressType(addressTypeResource)
                .build();

        when(organisationAddressServiceMock.findByOrganisationIdAndAddressId(organisationId, addressId)).thenReturn(serviceSuccess(organisationAddressResource));

        mockMvc.perform(get("/organisationaddress/organisation/{organisationId}/address/{addressId}", organisationId, addressId)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(organisationAddressResource)))
                .andDo(document("organisationaddress/{method-name}",
                        pathParameters(
                                parameterWithName("organisationId").description("Id of the Organisation for which Organisation Address is being retrieved"),
                                parameterWithName("addressId").description("Id of the Address for which Organisation Address is being retrieved")
                        ),
                        responseFields(OrganisationAddressDocs.organisationAddressResourceFields)
                ));

        verify(organisationAddressServiceMock).findByOrganisationIdAndAddressId(organisationId, addressId);
    }
}