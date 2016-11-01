package com.worth.ifs.project.monitoringofficer;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.monitoringofficer.controller.ProjectMonitoringOfficerController;
import com.worth.ifs.project.monitoringofficer.viewmodel.ProjectMonitoringOfficerViewModel;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectMonitoringOfficerControllerMockMvcTest extends BaseControllerMockMVCTest<ProjectMonitoringOfficerController> {

    @Test
    public void testViewMonitoringOfficer() throws Exception {

        ProjectResource project = newProjectResource().withId(123L).withApplication(345L).build();
        MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getMonitoringOfficerForProject(123L)).thenReturn(Optional.of(monitoringOfficer));

        MvcResult result = mockMvc.perform(get("/project/123/monitoring-officer")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        ProjectMonitoringOfficerViewModel viewModel =
                (ProjectMonitoringOfficerViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(Long.valueOf(123), viewModel.getProjectId());
        assertEquals(Long.valueOf(345), viewModel.getApplicationId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertTrue(viewModel.isMonitoringOfficerAssigned());
        assertEquals(monitoringOfficer.getFullName(), viewModel.getMonitoringOfficerName());
        assertEquals(monitoringOfficer.getEmail(), viewModel.getMonitoringOfficerEmailAddress());
        assertEquals(monitoringOfficer.getPhoneNumber(), viewModel.getMonitoringOfficerPhoneNumber());
    }

    @Test
    public void testViewMonitoringOfficerWithNoMonitoringOfficerYetAssigned() throws Exception {

        ProjectResource project = newProjectResource().withId(123L).withApplication(345L).build();

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getMonitoringOfficerForProject(123L)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/project/123/monitoring-officer")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        ProjectMonitoringOfficerViewModel viewModel =
                (ProjectMonitoringOfficerViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(Long.valueOf(123), viewModel.getProjectId());
        assertEquals(Long.valueOf(345), viewModel.getApplicationId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
        assertEquals("", viewModel.getMonitoringOfficerEmailAddress());
        assertEquals("", viewModel.getMonitoringOfficerPhoneNumber());
    }

    @Override
    protected ProjectMonitoringOfficerController supplyControllerUnderTest() {
        return new ProjectMonitoringOfficerController();
    }
}