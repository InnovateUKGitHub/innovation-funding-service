package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.builder.ProjectResourceBuilder;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.ProjectSetupStatusViewModel;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectSetupStatusControllerTest extends BaseControllerMockMVCTest<ProjectSetupStatusController> {
	
    @Override
    protected ProjectSetupStatusController supplyControllerUnderTest() {
        return new ProjectSetupStatusController();
    }

    private Long projectId = 20L;
    private CompetitionResource competition = newCompetitionResource().build();
    private ApplicationResource application = newApplicationResource().withCompetition(competition.getId()).build();
    private ProjectResourceBuilder projectBuilder = newProjectResource().withId(projectId).withApplication(application);


    @Test
    public void testViewProjectSetupStatus() throws Exception {

        ProjectResource project = projectBuilder.build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/overview"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertFalse(viewModel.isProjectDetailsSubmitted());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmitted() throws Exception {

        ProjectResource project = projectBuilder.withSubmittedDate(LocalDateTime.of(2016, 10, 10, 0, 0)).build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/overview"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
    }


    @Test
    public void testViewProjectSetupStatusWithMonitoringOfficerAssigned() throws Exception {

        ProjectResource project = projectBuilder.withSubmittedDate(LocalDateTime.of(2016, 10, 10, 0, 0)).build();
        MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
        Optional<MonitoringOfficerResource> monitoringOfficerResult = Optional.of(monitoringOfficer);

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(monitoringOfficerResult);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/overview"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertTrue(viewModel.isMonitoringOfficerAssigned());
        assertEquals(monitoringOfficer.getFullName(), viewModel.getMonitoringOfficerName());
    }

}
