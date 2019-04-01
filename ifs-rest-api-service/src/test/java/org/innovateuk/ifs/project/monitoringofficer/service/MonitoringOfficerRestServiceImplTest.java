package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoringofficer.resource.LegacyMonitoringOfficerResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class MonitoringOfficerRestServiceImplTest extends BaseRestServiceUnitTest<LegacyMonitoringOfficerRestServiceImpl> {
    private static final String projectRestURL = "/project";

    @Override
    protected LegacyMonitoringOfficerRestServiceImpl registerRestServiceUnderTest() {
        LegacyMonitoringOfficerRestServiceImpl monitoringOfficerRestService = new LegacyMonitoringOfficerRestServiceImpl();
        ReflectionTestUtils.setField(monitoringOfficerRestService, "projectRestURL", projectRestURL);
        return monitoringOfficerRestService;
    }

    @Test
    public void testUpdateMonitoringOfficer() {

        Long projectId = 1L;

        LegacyMonitoringOfficerResource monitoringOfficerResource = new LegacyMonitoringOfficerResource();
        monitoringOfficerResource.setId(null);
        monitoringOfficerResource.setProject(projectId);
        monitoringOfficerResource.setFirstName("abc");
        monitoringOfficerResource.setLastName("xyz");
        monitoringOfficerResource.setEmail("abc.xyz@gmail.com");
        monitoringOfficerResource.setPhoneNumber("078323455");

        setupPutWithRestResultExpectations(projectRestURL + "/" + projectId + "/monitoring-officer", monitoringOfficerResource, OK);

        RestResult<Void> result = service.updateMonitoringOfficer(projectId, "abc", "xyz", "abc.xyz@gmail.com", "078323455");

        assertTrue(result.isSuccess());

    }

    @Test
    public void testGetMonitoringOfficerForProject() {

        LegacyMonitoringOfficerResource expectedMonitoringOfficerResource = new LegacyMonitoringOfficerResource();
        expectedMonitoringOfficerResource.setProject(1L);
        expectedMonitoringOfficerResource.setFirstName("abc");
        expectedMonitoringOfficerResource.setLastName("xyz");
        expectedMonitoringOfficerResource.setEmail("abc.xyz@gmail.com");
        expectedMonitoringOfficerResource.setPhoneNumber("078323455");

        setupGetWithRestResultExpectations(projectRestURL + "/1/monitoring-officer", LegacyMonitoringOfficerResource.class, expectedMonitoringOfficerResource);

        RestResult<LegacyMonitoringOfficerResource> result = service.getMonitoringOfficerForProject(1L);

        assertTrue(result.isSuccess());

        Assert.assertEquals(expectedMonitoringOfficerResource, result.getSuccess());

    }
}
