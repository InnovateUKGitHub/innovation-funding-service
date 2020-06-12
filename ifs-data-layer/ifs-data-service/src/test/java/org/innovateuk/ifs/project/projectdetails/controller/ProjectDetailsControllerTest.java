package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.PostcodeAndTownResource;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.projectdetails.transactional.ProjectDetailsService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectDetailsControllerTest extends BaseControllerMockMVCTest<ProjectDetailsController> {

    @Mock
    private ProjectDetailsService projectDetailsService;

    @Mock
    private ProjectService projectService;

    @Override
    protected ProjectDetailsController supplyControllerUnderTest() {
        return new ProjectDetailsController();
    }

    @Test
    public void getProjectManager() throws Exception {
        Long project1Id = 1L;

        ProjectUserResource projectManager = newProjectUserResource().withId(project1Id).build();

        when(projectDetailsService.getProjectManager(project1Id)).thenReturn(serviceSuccess(projectManager));

        mockMvc.perform(get("/project/{id}/project-manager", project1Id))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectManager)));

        verify(projectDetailsService).getProjectManager(project1Id);
        verifyNoMoreInteractions(projectDetailsService);
    }

    @Test
    public void getProjectManagerNotFound() throws Exception {
        Long project1Id = -1L;

        when(projectDetailsService.getProjectManager(project1Id)).thenReturn(serviceFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/project/{id}/project-manager", project1Id))
                .andExpect(status().isNotFound());

        verify(projectDetailsService).getProjectManager(project1Id);
        verifyNoMoreInteractions(projectDetailsService);
    }

    @Test
    public void getProjectFinanceContacts() throws Exception {
        Long project1Id = 1L;
        List<ProjectParticipantRole> projectParticipantRoles = Collections.singletonList(ProjectParticipantRole.PROJECT_FINANCE_CONTACT);
        List<ProjectUserResource> projectFinanceContacts = Collections.singletonList(newProjectUserResource().withId(project1Id).build());

        when(projectService.getProjectUsersByProjectIdAndRoleIn(project1Id, projectParticipantRoles)).thenReturn(serviceSuccess(projectFinanceContacts));

        mockMvc.perform(get("/project/{id}/project-finance-contacts", project1Id))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectFinanceContacts)));

        verify(projectService).getProjectUsersByProjectIdAndRoleIn(project1Id, projectParticipantRoles);
        verifyNoMoreInteractions(projectService);
    }

    @Test
    public void getProjectFinanceContactsEmptyResults() throws Exception {
        Long project1Id = -1L;
        List<ProjectParticipantRole> projectParticipantRoles = Collections.singletonList(ProjectParticipantRole.PROJECT_FINANCE_CONTACT);
        List<ProjectUserResource> projectFinanceContacts = Collections.emptyList();

        when(projectService.getProjectUsersByProjectIdAndRoleIn(project1Id, projectParticipantRoles)).thenReturn(serviceSuccess(projectFinanceContacts));

        mockMvc.perform(get("/project/{id}/project-finance-contacts", project1Id))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectFinanceContacts)));

        verify(projectService).getProjectUsersByProjectIdAndRoleIn(project1Id, projectParticipantRoles);
        verifyNoMoreInteractions(projectService);
    }

    @Test
    public void getProjectFinanceContactsNotFound() throws Exception {
        Long project1Id = -1L;
        List<ProjectParticipantRole> projectParticipantRoles = Collections.singletonList(ProjectParticipantRole.PROJECT_FINANCE_CONTACT);

        when(projectService.getProjectUsersByProjectIdAndRoleIn(project1Id, projectParticipantRoles)).thenReturn(serviceFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/project/{id}/project-finance-contacts", project1Id))
                .andExpect(status().isNotFound());

        verify(projectService).getProjectUsersByProjectIdAndRoleIn(project1Id, projectParticipantRoles);
        verifyNoMoreInteractions(projectService);
    }

    @Test
    public void setProjectManager() throws Exception {
        when(projectDetailsService.setProjectManager(3L, 5L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/3/project-manager/5").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(projectDetailsService).setProjectManager(3L, 5L);
        verifyNoMoreInteractions(projectDetailsService);
    }

    @Test
    public void updateProjectDuration() throws Exception {

        long projectId = 3L;
        long durationInMonths = 18L;

        when(projectDetailsService.updateProjectDuration(projectId, durationInMonths)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/" + projectId + "/duration/" + durationInMonths)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(projectDetailsService).updateProjectDuration(projectId, durationInMonths);
        verifyNoMoreInteractions(projectDetailsService);
    }

    @Test
    public void updateFinanceContact() throws Exception {

        long projectId = 123L;
        long organisationId = 456L;
        long financeContactUserId = 789L;

        when(projectDetailsService.updateFinanceContact(new ProjectOrganisationCompositeId(projectId, organisationId), financeContactUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/finance-contact?financeContact=789", 123L, 456L))
                .andExpect(status().isOk());

        verify(projectDetailsService).updateFinanceContact(new ProjectOrganisationCompositeId(projectId, organisationId), financeContactUserId);
        verifyNoMoreInteractions(projectDetailsService);
    }

    @Test
    public void updatePartnerProjectLocation() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;
        PostcodeAndTownResource postcodeAndTown = new PostcodeAndTownResource("TW14 9QG", null);

        when(projectDetailsService.updatePartnerProjectLocation(new ProjectOrganisationCompositeId(projectId, organisationId), postcodeAndTown)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/partner-project-location", projectId, organisationId)
                .contentType(APPLICATION_JSON)
                .content(toJson(postcodeAndTown)))
                .andExpect(status().isOk());

        verify(projectDetailsService).updatePartnerProjectLocation(new ProjectOrganisationCompositeId(projectId, organisationId), postcodeAndTown);
        verifyNoMoreInteractions(projectDetailsService);
    }

    @Test
    public void updateProjectAddress() throws Exception {

        long projectId = 456L;

        AddressResource addressResource = newAddressResource().build();

        when(projectDetailsService.updateProjectAddress(projectId, addressResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/address", projectId)
                .contentType(APPLICATION_JSON)
                .content(toJson(addressResource)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(projectDetailsService).updateProjectAddress(projectId, addressResource);
        verifyNoMoreInteractions(projectDetailsService);
    }
}
