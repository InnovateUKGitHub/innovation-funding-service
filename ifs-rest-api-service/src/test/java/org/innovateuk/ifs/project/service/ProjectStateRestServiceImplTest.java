package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ProjectStateRestServiceImplTest extends BaseRestServiceUnitTest<ProjectStateRestServiceImpl> {
    private static final String projectRestURL = "/project";

    @Override
    protected ProjectStateRestServiceImpl registerRestServiceUnderTest() {
        ProjectStateRestServiceImpl projectService = new ProjectStateRestServiceImpl();
        ReflectionTestUtils.setField(projectService, "projectRestURL", projectRestURL);
        return projectService;
    }

    @Test
    public void withdrawProject() {
        long projectId = 123L;
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/withdraw", null, OK );
        RestResult<Void> result = service.withdrawProject(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/withdraw", Void.class);
        assertTrue(result.isSuccess());
    }

    @Test
    public void handleProjectOffline() {
        long projectId = 123L;
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/handle-offline", null, OK );
        RestResult<Void> result = service.handleProjectOffline(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/handle-offline", Void.class);
        assertTrue(result.isSuccess());
    }

    @Test
    public void completeProjectOffline() {
        long projectId = 123L;
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/complete-offline", null, OK );
        RestResult<Void> result = service.completeProjectOffline(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/complete-offline", Void.class);
        assertTrue(result.isSuccess());
    }

    @Test
    public void putProjectOnHold() {
        long projectId = 123L;
        OnHoldReasonResource onHoldReasonResource = new OnHoldReasonResource();
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/on-hold", onHoldReasonResource, OK );
        RestResult<Void> result = service.putProjectOnHold(projectId, onHoldReasonResource);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/on-hold", Void.class, onHoldReasonResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void resumeProject() {
        long projectId = 123L;
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/resume", null, OK );
        RestResult<Void> result = service.resumeProject(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/resume", Void.class);
        assertTrue(result.isSuccess());
    }

}
