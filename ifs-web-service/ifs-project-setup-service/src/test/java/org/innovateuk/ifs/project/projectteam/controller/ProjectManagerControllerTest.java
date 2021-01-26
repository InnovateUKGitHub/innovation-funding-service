package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectManagerControllerTest extends BaseControllerMockMVCTest<ProjectManagerController> {

    @Override
    protected ProjectManagerController supplyControllerUnderTest() {
        return new ProjectManagerController(projectService, projectDetailsService);
    }

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectDetailsService projectDetailsService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void viewProjectManagerPage() throws Exception {

        ProjectResource projectResource = newProjectResource().withName("Project Name").build();
        List<ProjectUserResource> projectUsers = newProjectUserResource()
                .withUser(111L, 222L).withRole(ProjectParticipantRole.PROJECT_MANAGER, ProjectParticipantRole.PROJECT_PARTNER).build(2);
        CompetitionResource competitionResource = newCompetitionResource().build();
        setLoggedInUser(newUserResource().withId(111L).build());

        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUsers);
        when(projectService.getLeadPartners(projectResource.getId())).thenReturn(projectUsers);
        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(competitionRestService.getCompetitionById(projectResource.getCompetition())).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get("/project/{id}/team/project-manager", projectResource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("project/team/project-manager"))
                .andReturn();

        verify(projectService).getProjectUsersForProject(projectResource.getId());
        verify(projectService).getLeadPartners(projectResource.getId());
        verify(projectService).getById(projectResource.getId());
    }

    @Test
    public void updateProjectManager() throws Exception {

        long projectId = 123L;
        long projectManagerUserId = 456L;

        when(projectDetailsService.updateProjectManager(projectId, projectManagerUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/team/project-manager", projectId)
                .param("projectManager", String.valueOf(projectManagerUserId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/team"));

        verify(projectDetailsService).updateProjectManager(projectId, projectManagerUserId);
    }
}
