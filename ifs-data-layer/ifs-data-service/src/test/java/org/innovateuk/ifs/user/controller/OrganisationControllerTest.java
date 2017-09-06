package org.innovateuk.ifs.user.controller;

import com.google.gson.Gson;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void createOrMatch_callsOrganisationServiceAndReturnsResultWithNoHash() throws Exception {
        OrganisationResource organisationResource = newOrganisationResource().build();

        Gson gson = new Gson();
        String json = gson.toJson(organisationResource, OrganisationResource.class);

        when(organisationInitialCreationServiceMock.createOrMatch(organisationResource)).thenReturn(serviceSuccess(newOrganisationResource().withId(1L).withName("uniqueOrganisationName").build()));

        mockMvc.perform(post("/organisation/createOrMatch")
                .contentType(MediaType.APPLICATION_JSON).
                        content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("uniqueOrganisationName")));

        verify(organisationInitialCreationServiceMock, only()).createOrMatch(organisationResource);
    }

    @Test
    public void findByIdShouldReturnOrganisation_callsOrganisationServiceWithPresentOptional() throws Exception {
        OrganisationResource organisationResource = newOrganisationResource().build();
        String inviteHash = "thisisahash";

        Gson gson = new Gson();
        String json = gson.toJson(organisationResource, OrganisationResource.class);

        when(organisationInitialCreationServiceMock.createAndLinkByInvite(organisationResource, inviteHash)).thenReturn(serviceSuccess(newOrganisationResource().withId(1L).withName("uniqueOrganisationName").build()));

        mockMvc.perform(post("/organisation/createAndLinkByInvite")
                .contentType(MediaType.APPLICATION_JSON).
                        content(json).param("inviteHash", "thisisahash"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("uniqueOrganisationName")));

        verify(organisationInitialCreationServiceMock, only()).createAndLinkByInvite(organisationResource, inviteHash);
    }

    @Test
    public void getPrimaryForUserShouldReturnOrganisation() throws Exception {
        when(organisationServiceMock.getPrimaryForUser(1L)).thenReturn(serviceSuccess(newOrganisationResource().withId(1L).withName("uniqueOrganisationName").build()));

        mockMvc.perform(get("/organisation/getPrimaryForUser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("uniqueOrganisationName")));
    }
}
