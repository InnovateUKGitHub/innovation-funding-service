package org.innovateuk.ifs.project.core.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectControllerTest extends BaseControllerMockMVCTest<ProjectController> {

    @Mock
    private ProjectService projectServiceMock;

    @Override
    protected ProjectController supplyControllerUnderTest() {
        return new ProjectController();
    }

    @Test
    public void getProjectById() throws Exception {
        ProjectResource project1 = newProjectResource().build();
        ProjectResource project2 = newProjectResource().build();

        when(projectServiceMock.getProjectById(project1.getId())).thenReturn(serviceSuccess(project1));
        when(projectServiceMock.getProjectById(project2.getId())).thenReturn(serviceSuccess(project2));

        mockMvc.perform(get("/project/{id}", project1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(project1)));

        mockMvc.perform(get("/project/{id}", project2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(project2)));
    }

    @Test
    public void findAll() throws Exception {
        int projectNumber = 3;
        List<ProjectResource> projects = newProjectResource().build(projectNumber);
        when(projectServiceMock.findAll()).thenReturn(serviceSuccess(projects));

        mockMvc.perform(get("/project/").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(projectNumber)));
    }

    @Test
    public void getProjectUsers() throws Exception {
        List<ProjectUserResource> projectUsers = newProjectUserResource().build(3);

        when(projectServiceMock.getProjectUsersByProjectIdAndRoleIn(123L, PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(serviceSuccess(projectUsers));

        mockMvc.perform(get("/project/{projectId}/project-users", 123))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectUsers)));
    }

    @Test
    public void createProjectFromApplication() throws Exception {
        long applicationId = 1;
        ProjectResource expectedProject = newProjectResource().build();

        when(projectServiceMock.createProjectFromApplication(applicationId)).thenReturn(serviceSuccess(expectedProject));

        mockMvc.perform(post("/project/create-project/application/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedProject)));

        verify(projectServiceMock).createProjectFromApplication(applicationId);
    }

    @Test
    public void getLeadOrganisation() throws Exception {
        long projectId = 5;
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(projectServiceMock.getLeadOrganisation(projectId)).thenReturn(serviceSuccess(organisationResource));

        mockMvc.perform(get("/project/{projectId}/lead-organisation", projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(organisationResource)));

        verify(projectServiceMock, only()).getLeadOrganisation(projectId);
    }
}