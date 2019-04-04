package org.innovateuk.ifs.project.projectmonitoringofficer.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.ProjectMonitoringOfficerRestServiceImpl;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectMonitoringOfficerResourceListType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ProjectMonitoringOfficerRestServiceImplTest extends BaseRestServiceUnitTest<ProjectMonitoringOfficerRestServiceImpl> {

    @Test
    public void findAll() {
        List<ProjectMonitoringOfficerResource> expected = singletonList(new ProjectMonitoringOfficerResource());
        setupGetWithRestResultExpectations("/monitoring-officer/find-all", projectMonitoringOfficerResourceListType(), expected, OK);

        RestResult<List<ProjectMonitoringOfficerResource>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), expected);
    }

    @Test
    public void existsByProjectIdAndUserId() {

        long projectId = 1L;
        long userId = 2L;

        boolean expected = true;

        setupGetWithRestResultExpectations(format("%s/%d/%s/%d", "/monitoring-officer", projectId, "exists", userId), Boolean.class, expected, OK);

        RestResult<Boolean> result = service.existsByProjectIdAndUserId(projectId, userId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), expected);
    }

    @Override
    protected ProjectMonitoringOfficerRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectMonitoringOfficerRestServiceImpl();
    }
}
