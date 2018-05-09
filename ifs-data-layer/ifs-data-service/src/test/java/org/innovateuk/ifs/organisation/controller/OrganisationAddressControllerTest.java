package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganisationAddressControllerTest extends BaseControllerMockMVCTest<OrganisationAddressController> {

    @Override
    protected OrganisationAddressController supplyControllerUnderTest() {
        return new OrganisationAddressController();
    }

    @Test
    public void findById() throws Exception {

        long id = 1L;

        OrganisationAddressResource organisationAddressResource = OrganisationAddressResourceBuilder.newOrganisationAddressResource().build();
        when(organisationAddressServiceMock.findOne(id)).thenReturn(serviceSuccess(organisationAddressResource));

        mockMvc.perform(get("/organisationaddress/{id}", id)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(organisationAddressResource)));

        verify(organisationAddressServiceMock).findOne(id);
    }

    @Test
    public void findByOrganisationIdAndAddressId() throws Exception {

        long organisationId = 1L;
        long addressId = 2L;

        OrganisationAddressResource organisationAddressResource = OrganisationAddressResourceBuilder.newOrganisationAddressResource().build();
        when(organisationAddressServiceMock.findByOrganisationIdAndAddressId(organisationId, addressId)).thenReturn(serviceSuccess(organisationAddressResource));

        mockMvc.perform(get("/organisationaddress/organisation/{organisationId}/address/{addressId}", organisationId, addressId)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(organisationAddressResource)));

        verify(organisationAddressServiceMock).findByOrganisationIdAndAddressId(organisationId, addressId);
    }
}


