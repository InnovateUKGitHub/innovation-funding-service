package org.innovateuk.ifs.monitoring.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.monitoring.controller.MonitoringOfficerController;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MonitoringOfficerControllerTest extends BaseControllerMockMVCTest<MonitoringOfficerController> {

    @Mock
    private MonitoringOfficerService monitoringOfficerServiceMock;

    @Override
    protected MonitoringOfficerController supplyControllerUnderTest() {
        return new MonitoringOfficerController(monitoringOfficerServiceMock);
    }

    @Test
    public void filterMonitoringOfficerProjects() throws Exception {
        int numberOfProjects = 2;
        long userId = 1L;
        List<ProjectResource> projectResources = newProjectResource()
                .withApplication(2L, 3L)
                .withName("name1", "name2")
                .build(2);

        when(monitoringOfficerServiceMock.filterMonitoringOfficerProjects(userId, true, true, false, true, false))
                .thenReturn(serviceSuccess(projectResources));

        mockMvc.perform(get("/monitoring-officer/1/filter-projects?projectInSetup={projectInSetup}&previousProject={previousProject}&documentsComplete={documentsComplete}&documentsIncomplete={documentsIncomplete}&documentsAwaitingReview={documentsAwaitingReview}", userId, true, true, false, true, false))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfProjects)));
    }
}
