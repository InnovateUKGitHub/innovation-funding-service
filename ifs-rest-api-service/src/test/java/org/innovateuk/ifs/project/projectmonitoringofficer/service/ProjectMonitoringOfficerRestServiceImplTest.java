package org.innovateuk.ifs.project.projectmonitoringofficer.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestServiceImpl;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectMonitoringOfficerResourceListType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ProjectMonitoringOfficerRestServiceImplTest extends BaseRestServiceUnitTest<MonitoringOfficerRestServiceImpl> {

    @Test
    public void findAll() {
        List<MonitoringOfficerResource> expected = singletonList(new MonitoringOfficerResource());
        setupGetWithRestResultExpectations("/monitoring-officer/find-all", projectMonitoringOfficerResourceListType(), expected, OK);

        RestResult<List<MonitoringOfficerResource>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), expected);
    }

    @Override
    protected MonitoringOfficerRestServiceImpl registerRestServiceUnderTest() {
        return new MonitoringOfficerRestServiceImpl();
    }
}
