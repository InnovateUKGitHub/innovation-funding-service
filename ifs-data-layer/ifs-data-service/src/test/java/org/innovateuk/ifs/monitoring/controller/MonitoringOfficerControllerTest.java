package org.innovateuk.ifs.monitoring.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.monitoring.controller.MonitoringOfficerController;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerDashboardPageResource;
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

        MonitoringOfficerDashboardPageResource monitoringOfficerDashboardPageResource = new MonitoringOfficerDashboardPageResource();
        monitoringOfficerDashboardPageResource.setContent(projectResources);
        monitoringOfficerDashboardPageResource.setNumber(0);
        monitoringOfficerDashboardPageResource.setSize(10);
        monitoringOfficerDashboardPageResource.setTotalElements(projectResources.size());
        monitoringOfficerDashboardPageResource.setTotalPages(1);

        when(monitoringOfficerServiceMock.filterMonitoringOfficerProjects(userId, "keyword",true, true, 0, 10))
                .thenReturn(serviceSuccess(monitoringOfficerDashboardPageResource));

        mockMvc.perform(get("/monitoring-officer/1/filter-projects?keywordSearch=keyword&projectInSetup={projectInSetup}&previousProject={previousProject}&pageIndex={pageIndex}&pageSize={pageSize}", userId, true, true, 0, 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }
}
