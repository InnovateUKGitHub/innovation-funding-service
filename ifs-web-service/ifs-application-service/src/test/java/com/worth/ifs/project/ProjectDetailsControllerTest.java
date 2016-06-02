package com.worth.ifs.project;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.ProcessRoleResource;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDetailsControllerTest extends BaseControllerMockMVCTest<ProjectDetailsController> {
	
	@Before
	public void setUp() {
		super.setUp();
		setupInvites();
		loginDefaultUser();
		loggedInUser.setOrganisations(asList(8L));
	}
	
    @Override
    protected ProjectDetailsController supplyControllerUnderTest() {
        return new ProjectDetailsController();
    }
    
    @Test
    public void testCompetitionDetailsCompetitionId() throws Exception {
    	Long projectId = 20L;

        CompetitionResource competitionResource = newCompetitionResource().build();
    	ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withId(applicationResource.getId()).build();

    	when(applicationService.getById(projectId)).thenReturn(applicationResource);
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);

        mockMvc.perform(get("/project/{id}/details", projectId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", projectResource))
                .andExpect(model().attribute("app", applicationResource))
                .andExpect(model().attribute("competition", competitionResource))
                .andExpect(view().name("project/detail"));
    }
    
    @Test
    public void testCompetitionDetailsProjectManager() throws Exception {
    	Long projectId = 20L;

        CompetitionResource competitionResource = newCompetitionResource().build();
    	ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withId(applicationResource.getId()).build();

    	when(applicationService.getById(projectId)).thenReturn(applicationResource);
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);

        mockMvc.perform(get("/project/{id}/details/project-manager", projectId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", projectResource))
                .andExpect(model().attribute("app", applicationResource))
                .andExpect(view().name("project/project-manager"));
    }
    
    @Test
    public void testCompetitionDetailsSetProjectManager() throws Exception {
    	Long projectId = 20L;
    	Long projectManagerUserId = 80L;

        CompetitionResource competitionResource = newCompetitionResource().build();
    	ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withId(applicationResource.getId()).build();

    	when(applicationService.getById(projectId)).thenReturn(applicationResource);
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);
        ProcessRoleResource processRoleResource = new ProcessRoleResource();
        processRoleResource.setUser(projectManagerUserId);
        when(userService.getLeadPartnerOrganisationProcessRoles(applicationResource)).thenReturn(asList(processRoleResource));
        
        mockMvc.perform(post("/project/{id}/details/project-manager", projectId)
        		.param("projectManager", projectManagerUserId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/details"));
        
        verify(projectService).updateProjectManager(projectId, projectManagerUserId);
    }

}
