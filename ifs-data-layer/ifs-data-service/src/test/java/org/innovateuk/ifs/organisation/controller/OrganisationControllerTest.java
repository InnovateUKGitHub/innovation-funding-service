package org.innovateuk.ifs.organisation.controller;

import com.google.gson.Gson;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationInitialCreationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganisationControllerTest extends BaseControllerMockMVCTest<OrganisationController> {

    @Mock
    private OrganisationService organisationServiceMock;

    @Mock
    private OrganisationInitialCreationService organisationInitialCreationServiceMock;

    @Override
    protected OrganisationController supplyControllerUnderTest() {
        return new OrganisationController();
    }

    @Test
    public void findByIdShouldReturnOrganisation() throws Exception {
        when(organisationServiceMock.findById(1L)).thenReturn(serviceSuccess(newOrganisationResource().withId(1L).withName("uniqueOrganisationName").build()));

        mockMvc.perform(get("/organisation/find-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("uniqueOrganisationName")));
    }

    @Test
    public void createOrMatch_callsOrganisationServiceAndReturnsResultWithNoHash() throws Exception {
        OrganisationResource organisationResource = newOrganisationResource().build();

        Gson gson = new Gson();
        String json = gson.toJson(organisationResource, OrganisationResource.class);

        when(organisationInitialCreationServiceMock.createOrMatch(organisationResource)).thenReturn(serviceSuccess(newOrganisationResource().withId(1L).withName("uniqueOrganisationName").build()));

        mockMvc.perform(post("/organisation/create-or-match")
                .contentType(MediaType.APPLICATION_JSON).
                        content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("uniqueOrganisationName")));

        verify(organisationInitialCreationServiceMock, only()).createOrMatch(organisationResource);
    }

    @Test
    public void getByUserAndApplicationId() throws Exception {
        when(organisationServiceMock.getByUserAndApplicationId(1L, 2L)).thenReturn(serviceSuccess(newOrganisationResource().withId(1L).withName("uniqueOrganisationName").build()));

        mockMvc.perform(get("/organisation/by-user-and-application-id/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("uniqueOrganisationName")));
    }

    @Test
    public void getByUserAndProjectId() throws Exception {
        when(organisationServiceMock.getByUserAndProjectId(1L, 2L)).thenReturn(serviceSuccess(newOrganisationResource().withId(1L).withName("uniqueOrganisationName").build()));

        mockMvc.perform(get("/organisation/by-user-and-project-id/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("uniqueOrganisationName")));
    }

    @Test
    public void updateCompaniesHouseDetailsReturnOrganisation() throws Exception {
        OrganisationResource organisationResource = newOrganisationResource()
                .withId(1L)
                .withName("uniqueOrganisationName")
                .build();

        when(organisationServiceMock.syncCompaniesHouseDetails(any(OrganisationResource.class)))
                .thenReturn(serviceSuccess(organisationResource));

        mockMvc.perform(put("/organisation/sync-companies-house-details")
                .contentType(APPLICATION_JSON)
                .content(toJson(organisationResource)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(organisationResource)));
    }

    @Test
    public void updateCompaniesHouseDetailsReturnsNotFound() throws Exception {
        OrganisationResource organisationResource = newOrganisationResource()
                .withId(1L)
                .withName("uniqueOrganisationName")
                .build();

        when(organisationServiceMock.syncCompaniesHouseDetails(any(OrganisationResource.class)))
                .thenReturn(serviceFailure(notFoundError(OrganisationResource.class)));

        mockMvc.perform(put("/organisation/sync-companies-house-details")
                .contentType(APPLICATION_JSON)
                .content(toJson(organisationResource)))
                .andExpect(status().isNotFound());
    }
}
