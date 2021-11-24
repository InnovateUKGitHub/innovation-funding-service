package org.innovateuk.ifs.project.monitoring.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.monitoring.controller.MonitoringOfficerController;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignmentResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.SimpleUserResourceBuilder.newSimpleUserResource;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MonitoringOfficerControllerDocumentation extends BaseControllerMockMVCTest<MonitoringOfficerController> {

    @Mock
    private MonitoringOfficerService projectMonitoringOfficerServiceMock;

    @Override
    protected MonitoringOfficerController supplyControllerUnderTest() {
        return new MonitoringOfficerController(projectMonitoringOfficerServiceMock);
    }

    @Test
    public void findAll() throws Exception {

        when(projectMonitoringOfficerServiceMock.findAll()).thenReturn(serviceSuccess(newSimpleUserResource().build(1)));

        mockMvc.perform(get("/monitoring-officer/find-all"))
                .andExpect(status().isOk());

        verify(projectMonitoringOfficerServiceMock).findAll();
    }

    @Test
    public void getProjectMonitoringOfficer() throws Exception {
        long userId = 7;
        MonitoringOfficerAssignmentResource expected = new MonitoringOfficerAssignmentResource(1L, "firstName", "lastName",
                singletonList(new MonitoringOfficerUnassignedProjectResource(1, 1, "projectName")),
                singletonList(new MonitoringOfficerAssignedProjectResource(1, 1, 1, "projectName", "leadOrganisationName")));

        when(projectMonitoringOfficerServiceMock.getProjectMonitoringOfficer(userId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/monitoring-officer/{userId}", userId))
                .andExpect(status().isOk());

        verify(projectMonitoringOfficerServiceMock, only()).getProjectMonitoringOfficer(userId);
    }

    @Test
    public void assignProjectToMonitoringOfficer() throws Exception {
        long userId = 11;
        long projectId = 13;

        when(projectMonitoringOfficerServiceMock.assignProjectToMonitoringOfficer(userId, projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/monitoring-officer/{userId}/assign/{projectId}", userId, projectId))
                .andExpect(status().is2xxSuccessful());

        verify(projectMonitoringOfficerServiceMock, only()).assignProjectToMonitoringOfficer(userId, projectId);
    }

    @Test
    public void unassignProjectFromMonitoringOfficer() throws Exception {
        long userId = 11;
        long projectId = 13;

        when(projectMonitoringOfficerServiceMock.unassignProjectFromMonitoringOfficer(userId, projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/monitoring-officer/{userId}/unassign/{projectId}", userId, projectId))
                .andExpect(status().is2xxSuccessful());

        verify(projectMonitoringOfficerServiceMock, only()).unassignProjectFromMonitoringOfficer(userId, projectId);
    }

    @Test
    public void getMonitoringOfficerProjects() throws Exception {
        long userId = 11;

        when(projectMonitoringOfficerServiceMock.getMonitoringOfficerProjects(userId)).thenReturn(serviceSuccess(newProjectResource().build(1)));

        mockMvc.perform(get("/monitoring-officer/{userId}/projects", userId).contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().is2xxSuccessful());

        verify(projectMonitoringOfficerServiceMock, only()).getMonitoringOfficerProjects(userId);
    }
}