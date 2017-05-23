package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.isA;
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
    public void setProjectManager() throws Exception {
        when(projectDetailsServiceMock.setProjectManager(3L, 5L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/3/project-manager/5").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(projectDetailsServiceMock).setProjectManager(3L, 5L);
    }

    @Test
    public void updateFinanceContact() throws Exception {

        when(projectDetailsServiceMock.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 456L), 789L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/finance-contact?financeContact=789", 123L, 456L))
                .andExpect(status().isOk());

        verify(projectDetailsServiceMock).updateFinanceContact(new ProjectOrganisationCompositeId(123L, 456L), 789L);
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

    @Test
    public void isSubmitAllowed() throws Exception {
        when(projectDetailsServiceMock.isSubmitAllowed(123L)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/project/{projectId}/isSubmitAllowed", 123L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andReturn();
    }

    @Test
    public void isSubmitAllowedFalse() throws Exception {
        when(projectDetailsServiceMock.isSubmitAllowed(123L)).thenReturn(serviceSuccess(false));

        mockMvc.perform(get("/project/{projectId}/isSubmitAllowed", 123L))
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andReturn();
    }

    @Test
    public void setApplicationDetailsSubmitted() throws Exception {
        when(projectDetailsServiceMock.submitProjectDetails(isA(Long.class), isA(ZonedDateTime.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/setApplicationDetailsSubmitted", 123L))
                .andExpect(status().isOk())
                .andReturn();
    }
}
