package org.innovateuk.ifs.project.projectmonitoringofficer.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerDashboardPageResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.simpleUserListType;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.SimpleUserResourceBuilder.newSimpleUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class MonitoringOfficerRestServiceImplTest extends BaseRestServiceUnitTest<MonitoringOfficerRestServiceImpl> {

    @Test
    public void findAll() {
        List<SimpleUserResource> expected = newSimpleUserResource().build(1);
        setupGetWithRestResultExpectations("/monitoring-officer/find-all", simpleUserListType(), expected, OK);

        RestResult<List<SimpleUserResource>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), expected);
    }

    @Test
    public void getProjectsForMonitoringOfficer() {
        long userId = 1L;
        List<ProjectResource> expected = newProjectResource().build(1);
        setupGetWithRestResultExpectations("/monitoring-officer/1/projects", projectResourceListType(), expected, OK);

        RestResult<List<ProjectResource>> result = service.getProjectsForMonitoringOfficer(userId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), expected);
    }

    @Test
    public void filterProjectsForMonitoringOfficerByProjectNumber() {
        long userId = 1L;
        List<ProjectResource> expected = newProjectResource().build(1);

        String expectedURL = "/monitoring-officer/1/filter-projects?pageNumber=0&pageSize=10&keywordSearch=123&projectInSetup=true&previousProject=true";

        MonitoringOfficerDashboardPageResource monitoringOfficerDashboardPageResource = new MonitoringOfficerDashboardPageResource();
        monitoringOfficerDashboardPageResource.setContent(expected);
        monitoringOfficerDashboardPageResource.setNumber(0);
        monitoringOfficerDashboardPageResource.setSize(10);
        monitoringOfficerDashboardPageResource.setTotalElements(expected.size());
        monitoringOfficerDashboardPageResource.setTotalPages(1);

        setupGetWithRestResultExpectations(expectedURL,MonitoringOfficerDashboardPageResource.class, monitoringOfficerDashboardPageResource);

        RestResult<MonitoringOfficerDashboardPageResource> result = service.filterProjectsForMonitoringOfficer(userId, 0, 10, "123", true, true);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess().getContent(), expected);
    }

    @Test
    public void filterProjectsForMonitoringOfficerByKeyword() {
        long userId = 1L;
        List<ProjectResource> expected = newProjectResource().build(1);

        MonitoringOfficerDashboardPageResource monitoringOfficerDashboardPageResource = new MonitoringOfficerDashboardPageResource();
        monitoringOfficerDashboardPageResource.setContent(expected);
        monitoringOfficerDashboardPageResource.setNumber(0);
        monitoringOfficerDashboardPageResource.setSize(10);
        monitoringOfficerDashboardPageResource.setTotalElements(expected.size());
        monitoringOfficerDashboardPageResource.setTotalPages(1);

        setupGetWithRestResultExpectations("/monitoring-officer/1/filter-projects?pageNumber=0&pageSize=10&keywordSearch=%name%&projectInSetup=true&previousProject=true",
                MonitoringOfficerDashboardPageResource.class, monitoringOfficerDashboardPageResource);

       RestResult<MonitoringOfficerDashboardPageResource> result = service.filterProjectsForMonitoringOfficer(userId, 0, 10, "name", true, true);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess().getContent(), expected);
    }

    @Override
    protected MonitoringOfficerRestServiceImpl registerRestServiceUnderTest() {
        return new MonitoringOfficerRestServiceImpl();
    }
}
