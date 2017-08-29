package org.innovateuk.ifs.user.controller;

import com.google.gson.Gson;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.organisation.transactional.OrganisationInitialCreationService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganisationControllerTest extends BaseControllerMockMVCTest<OrganisationController> {

    @Mock
    private OrganisationInitialCreationService organisationInitialCreationServiceMock;

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
    }

    //@Test
    public void findByIdShouldReturnOrganisation_callsOrganisationServiceWithPresentOptional() throws Exception {
        OrganisationResource organisationResource = newOrganisationResource().build();
        Optional<String> inviteHash = Optional.of("thisisahash");

        Gson gson = new Gson();
        String json = gson.toJson(organisationResource, OrganisationResource.class);

        //when(organisationServiceMock.createOrMatch(organisationResource, inviteHash)).thenReturn(serviceSuccess(newOrganisationResource().withId(1L).withName("uniqueOrganisationName").build()));

        mockMvc.perform(post("/organisation/createOrMatch")
                .contentType(MediaType.APPLICATION_JSON).
                        content(json).param("inviteHash", "thisisahash"))
                .andExpect(status().isCreated())
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
