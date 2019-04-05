package org.innovateuk.ifs.project.monitoring.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MonitoringOfficerControllerTest extends BaseControllerMockMVCTest<MonitoringOfficerController> {

    @Mock
    private MonitoringOfficerService projectMonitoringOfficerServiceMock;

    @Test
    public void findAll() throws Exception {
        List<MonitoringOfficerResource> expected = singletonList(new MonitoringOfficerResource());

        when(projectMonitoringOfficerServiceMock.findAll()).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/monitoring-officer/find-all"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(projectMonitoringOfficerServiceMock).findAll();
    }


    @Test
    public void getProjectMonitoringOfficer() throws Exception {
        long userId = 7;
        MonitoringOfficerResource expected = new MonitoringOfficerResource();

        when(projectMonitoringOfficerServiceMock.getProjectMonitoringOfficer(userId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/monitoring-officer/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(projectMonitoringOfficerServiceMock, only()).getProjectMonitoringOfficer(userId);
    }

    @Test
    public void assignProjectToMonitoringOfficer() throws Exception {
        long userId = 11;
        long projectId = 13;

        when(projectMonitoringOfficerServiceMock.assignProjectToMonitoringOfficer(userId, projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/monitoring-officer/{userId}/assign/{projectId}", userId, projectId))
                .andExpect(status().is2xxSuccessful());

        verify(projectMonitoringOfficerServiceMock, only()).assignProjectToMonitoringOfficer(userId, projectId);
    }

    @Test
    public void unassignProjectFromMonitoringOfficer() throws Exception {
        long userId = 11;
        long projectId = 13;

        when(projectMonitoringOfficerServiceMock.unassignProjectFromMonitoringOfficer(userId, projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/monitoring-officer/{userId}/unassign/{projectId}", userId, projectId))
                .andExpect(status().is2xxSuccessful());

        verify(projectMonitoringOfficerServiceMock, only()).unassignProjectFromMonitoringOfficer(userId, projectId);
    }

    @Test
    public void getMonitoringOfficerProjects() throws Exception {
        long userId = 11;

        when(projectMonitoringOfficerServiceMock.getMonitoringOfficerProjects(userId)).thenReturn(serviceSuccess(emptyList()));

        mockMvc.perform(MockMvcRequestBuilders.get("/monitoring-officer/{userId}/projects", userId))
                .andExpect(status().is2xxSuccessful());

        verify(projectMonitoringOfficerServiceMock, only()).getMonitoringOfficerProjects(userId);
    }

    @Override
    protected MonitoringOfficerController supplyControllerUnderTest() {
        return new MonitoringOfficerController(projectMonitoringOfficerServiceMock);
    }
}