package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateForm;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateViewModel;
import com.worth.ifs.project.viewmodel.ProjectDetailsViewModel;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.BaseBuilderAmendFunctions.name;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    	ApplicationResource applicationResource = newApplicationResource().withId(projectId).withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withId(applicationResource.getId()).build();

    	when(applicationService.getById(projectId)).thenReturn(applicationResource);
        when(userService.isLeadApplicant(loggedInUser.getId(), applicationResource)).thenReturn(Boolean.TRUE);
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);

        MvcResult result = mockMvc.perform(get("/project/{id}/details", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/detail"))
                .andReturn();

        ProjectDetailsViewModel viewModel = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(projectResource, viewModel.getProject());
        assertEquals(applicationResource, viewModel.getApp());
        assertEquals(competitionResource, viewModel.getCompetition());
    }
    
    @Test
    public void testCompetitionDetailsProjectManager() throws Exception {
    	Long projectId = 20L;

        CompetitionResource competitionResource = newCompetitionResource().build();
    	ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withId(applicationResource.getId()).build();

        when(userService.isLeadApplicant(loggedInUser.getId(), applicationResource)).thenReturn(Boolean.TRUE);
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

        when(userService.isLeadApplicant(loggedInUser.getId(), applicationResource)).thenReturn(Boolean.TRUE);
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

    @Test
    public void testViewStartDate() throws Exception {

        ProjectResource project = newProjectResource().
                with(name("My Project")).
                withDuration(4L).
                withTargetStartDate(LocalDate.now().withDayOfMonth(5)).
                withDuration(4L).
                build();

        ApplicationResource applicationResource = newApplicationResource().build();
        when(applicationService.getById(123L)).thenReturn(applicationResource);
        when(userService.isLeadApplicant(loggedInUser.getId(), applicationResource)).thenReturn(Boolean.TRUE);
        when(projectService.getById(123L)).thenReturn(project);

        MvcResult result = mockMvc.perform(get("/project/{id}/details/start-date", 123L))
                .andExpect(status().isOk())
                .andExpect(view().name("project/details-start-date"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();
        ProjectDetailsStartDateViewModel viewModel = (ProjectDetailsStartDateViewModel) model.get("model");

        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(project.getFormattedId(), viewModel.getProjectNumber());
        assertEquals(project.getDurationInMonths(), Long.valueOf(viewModel.getProjectDurationInMonths()));

        ProjectDetailsStartDateForm form = (ProjectDetailsStartDateForm) model.get("form");
        assertEquals(project.getTargetStartDate().withDayOfMonth(1), form.getProjectStartDate());
    }

    @Test
    public void testUpdateStartDate() throws Exception {

        ApplicationResource applicationResource = newApplicationResource().build();
        when(applicationService.getById(123L)).thenReturn(applicationResource);
        when(userService.isLeadApplicant(loggedInUser.getId(), applicationResource)).thenReturn(Boolean.TRUE);
        when(projectRestService.updateProjectStartDate(123L, LocalDate.of(2017, 6, 3))).thenReturn(restSuccess());

        mockMvc.perform(post("/project/{id}/details/start-date", 123L).
                    contentType(MediaType.APPLICATION_FORM_URLENCODED).
                    param("projectStartDate", "projectStartDate").
                    param("projectStartDate.dayOfMonth", "3").
                    param("projectStartDate.monthValue", "6").
                    param("projectStartDate.year", "2017"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/123/details"))
                .andReturn();

    }

    @Test
    public void testUpdateFinanceContact() throws Exception {

        List<ProjectUserResource> availableUsers = newProjectUserResource().withUser(789L).withOrganisation(8L).build(1);

        when(projectService.getProjectUsersForProject(123L)).thenReturn(availableUsers);

        mockMvc.perform(post("/project/{id}/details/finance-contact", 123L).
                    contentType(MediaType.APPLICATION_FORM_URLENCODED).
                    param("organisation", "8").
                    param("financeContact", "789")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/details")).
                andReturn();

        verify(projectService).updateFinanceContact(123L, 8L, 789L);
    }
}
