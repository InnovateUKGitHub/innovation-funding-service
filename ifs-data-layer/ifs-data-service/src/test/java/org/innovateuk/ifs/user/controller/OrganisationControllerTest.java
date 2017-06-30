package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganisationControllerTest extends BaseControllerMockMVCTest<OrganisationController> {

    @Override
    protected OrganisationController supplyControllerUnderTest() {
        return new OrganisationController();
    }

    @Test
    public void findByIdShouldReturnOrganisation() throws Exception {
        when(organisationServiceMock.findById(1L)).thenReturn(serviceSuccess(newOrganisationResource().withId(1L).withName("uniqueOrganisationName").build()));

        mockMvc.perform(get("/organisation/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("uniqueOrganisationName")));
    }

    @Test
    public void getPrimaryForUserShouldReturnOrganisation() throws Exception {
        when(organisationServiceMock.getPrimaryForUser(1L)).thenReturn(serviceSuccess(newOrganisationResource().withId(1L).withName("uniqueOrganisationName").build()));

        mockMvc.perform(get("/organisation/getPrimaryForUser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("uniqueOrganisationName")));
    }
}
