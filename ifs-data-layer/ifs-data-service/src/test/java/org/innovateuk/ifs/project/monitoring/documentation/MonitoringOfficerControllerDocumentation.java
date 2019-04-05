package org.innovateuk.ifs.project.monitoring.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.monitoring.controller.MonitoringOfficerController;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ProjectDocs.projectResourceFields;
import static org.innovateuk.ifs.documentation.ProjectMonitoringOfficerResourceDocs.*;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
        List<MonitoringOfficerUnassignedProjectResource> unassignedProjects =
                singletonList(new MonitoringOfficerUnassignedProjectResource(1,
                                                                             1,
                                                                             "projectName"));
        List<MonitoringOfficerAssignedProjectResource> assignedProjects =
                singletonList(new MonitoringOfficerAssignedProjectResource(1,
                                                                           1,
                                                                           1,
                                                                           "projectName",
                                                                           "leadOrganisationName"));

        List<MonitoringOfficerResource> expected =
                singletonList(new MonitoringOfficerResource(1L,
                                                                   "firstName",
                                                                   "lastName",
                                                                   unassignedProjects,
                                                                   assignedProjects));

        when(projectMonitoringOfficerServiceMock.findAll()).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/monitoring-officer/find-all"))
                .andExpect(status().isOk())
                .andDo(document("monitoring-officer/{method-name}",
                                responseFields(fieldWithPath("[]").description("List of monitoring officers"))
                                        .andWithPrefix("[].", projectMonitoringOfficerResourceFields)
                                        .andWithPrefix("[].unassignedProjects[].", monitoringOfficerUnassignedProjectResourceFields)
                                        .andWithPrefix("[].assignedProjects[].", monitoringOfficerAssignedProjectResourceFields)
                ));

        verify(projectMonitoringOfficerServiceMock).findAll();
    }

    @Test
    public void getProjectMonitoringOfficer() throws Exception {
        long userId = 7;
        MonitoringOfficerResource expected = new MonitoringOfficerResource(1L, "firstName", "lastName",
                singletonList(new MonitoringOfficerUnassignedProjectResource(1, 1, "projectName")),
                singletonList(new MonitoringOfficerAssignedProjectResource(1, 1, 1, "projectName", "leadOrganisationName")));

        when(projectMonitoringOfficerServiceMock.getProjectMonitoringOfficer(userId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/monitoring-officer/{userId}", userId))
                .andExpect(status().isOk())
                .andDo(document("monitoring-officer/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the monitoring officer user")
                        ),
                        responseFields(projectMonitoringOfficerResourceFields)
                                .andWithPrefix("unassignedProjects[].", monitoringOfficerUnassignedProjectResourceFields)
                                .andWithPrefix("assignedProjects[].", monitoringOfficerAssignedProjectResourceFields)
                ));

        verify(projectMonitoringOfficerServiceMock, only()).getProjectMonitoringOfficer(userId);
    }

    @Test
    public void assignProjectToMonitoringOfficer() throws Exception {
        long userId = 11;
        long projectId = 13;

        when(projectMonitoringOfficerServiceMock.assignProjectToMonitoringOfficer(userId, projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/monitoring-officer/{userId}/assign/{projectId}", userId, projectId))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("monitoring-officer/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the monitoring officer user"),
                                parameterWithName("projectId").description("Id of the project to assign ")
                        )
                ));

        verify(projectMonitoringOfficerServiceMock, only()).assignProjectToMonitoringOfficer(userId, projectId);
    }

    @Test
    public void unassignProjectFromMonitoringOfficer() throws Exception {
        long userId = 11;
        long projectId = 13;

        when(projectMonitoringOfficerServiceMock.unassignProjectFromMonitoringOfficer(userId, projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/monitoring-officer/{userId}/unassign/{projectId}", userId, projectId))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("monitoring-officer/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the monitoring officer user"),
                                parameterWithName("projectId").description("Id of the project to unassign ")
                        )
                ));

        verify(projectMonitoringOfficerServiceMock, only()).unassignProjectFromMonitoringOfficer(userId, projectId);
    }

    @Test
    public void getMonitoringOfficerProjects() throws Exception {
        long userId = 11;

        when(projectMonitoringOfficerServiceMock.getMonitoringOfficerProjects(userId)).thenReturn(serviceSuccess(newProjectResource().build(1)));

        mockMvc.perform(get("/monitoring-officer/{userId}/projects", userId).contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("monitoring-officer/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the monitoring officer user")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of projects the user is allowed to monitor")
                        ).andWithPrefix("[].", projectResourceFields)
                ));

        verify(projectMonitoringOfficerServiceMock, only()).getMonitoringOfficerProjects(userId);
    }
}