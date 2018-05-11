package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.junit.Test;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectDetailsControllerTest extends BaseControllerMockMVCTest<ProjectDetailsController> {

    @Override
    protected ProjectDetailsController supplyControllerUnderTest() {
        return new ProjectDetailsController();
    }

    @Test
    public void testGetProjectManager() throws Exception {
        Long project1Id = 1L;

        ProjectUserResource projectManager = newProjectUserResource().withId(project1Id).build();

        when(projectDetailsServiceMock.getProjectManager(project1Id)).thenReturn(serviceSuccess(projectManager));

        mockMvc.perform(get("/project/{id}/project-manager", project1Id))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectManager)));
    }

    @Test
    public void testGetProjectManagerNotFound() throws Exception {
        Long project1Id = -1L;

        when(projectDetailsServiceMock.getProjectManager(project1Id)).thenReturn(serviceFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/project/{id}/project-manager", project1Id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void setProjectManager() throws Exception {
        when(projectDetailsServiceMock.setProjectManager(3L, 5L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/3/project-manager/5").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(projectDetailsServiceMock).setProjectManager(3L, 5L);
    }

    @Test
    public void updateProjectDuration() throws Exception {

        long projectId = 3L;
        long durationInMonths = 18L;

        when(projectDetailsServiceMock.updateProjectDuration(projectId, durationInMonths)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/" + projectId + "/duration/" + durationInMonths)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(projectDetailsServiceMock).updateProjectDuration(projectId, durationInMonths);
    }

    @Test
    public void updateFinanceContact() throws Exception {

        when(projectDetailsServiceMock.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 456L), 789L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/finance-contact?financeContact=789", 123L, 456L))
                .andExpect(status().isOk());

        verify(projectDetailsServiceMock).updateFinanceContact(new ProjectOrganisationCompositeId(123L, 456L), 789L);
    }

    @Test
    public void updatePartnerProjectLocation() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;
        String postcode = "TW14 9QG";
        when(projectDetailsServiceMock.updatePartnerProjectLocation(new ProjectOrganisationCompositeId(projectId, organisationId), postcode)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/partner-project-location?postcode={postcode}", projectId, organisationId, postcode))
                .andExpect(status().isOk());

        verify(projectDetailsServiceMock).updatePartnerProjectLocation(new ProjectOrganisationCompositeId(projectId, organisationId), postcode);
    }

    @Test
    public void updateProjectAddress() throws Exception {
        AddressResource addressResource = newAddressResource().withId(1L).build();

        when(projectDetailsServiceMock.updateProjectAddress(123L, 456L, OrganisationAddressType.REGISTERED, addressResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/address", 456L)
                .param("leadOrganisationId", "123")
                .param("addressType", OrganisationAddressType.REGISTERED.name())
                .contentType(APPLICATION_JSON)
                .content(toJson(addressResource)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(projectDetailsServiceMock).updateProjectAddress(123L, 456L, OrganisationAddressType.REGISTERED, addressResource);
    }
}
