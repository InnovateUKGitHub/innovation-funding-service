package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ProjectStateRestServiceImplTest extends BaseRestServiceUnitTest<ProjectStateRestServiceImpl> {
    private static final String projectRestURL = "/project";
    private static final long projectId = 123L;

    @Override
    protected ProjectStateRestServiceImpl registerRestServiceUnderTest() {
        ProjectStateRestServiceImpl projectService = new ProjectStateRestServiceImpl();
        ReflectionTestUtils.setField(projectService, "projectRestURL", projectRestURL);
        return projectService;
    }

    @Test
    public void withdrawProject() {
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/withdraw", null, OK );
        RestResult<Void> result = service.withdrawProject(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/withdraw", Void.class);
        assertTrue(result.isSuccess());
    }

    @Test
    public void handleProjectOffline() {
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/handle-offline", null, OK );
        RestResult<Void> result = service.handleProjectOffline(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/handle-offline", Void.class);
        assertTrue(result.isSuccess());
    }

    @Test
    public void completeProjectOffline() {
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/complete-offline", null, OK );
        RestResult<Void> result = service.completeProjectOffline(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/complete-offline", Void.class);
        assertTrue(result.isSuccess());
    }

    @Test
    public void putProjectOnHold() {
        OnHoldReasonResource onHoldReasonResource = new OnHoldReasonResource("something", "else");
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/on-hold", onHoldReasonResource, OK );
        RestResult<Void> result = service.putProjectOnHold(projectId, onHoldReasonResource);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/on-hold", Void.class, onHoldReasonResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void resumeProject() {
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/resume", null, OK );
        RestResult<Void> result = service.resumeProject(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/resume", Void.class);
        assertTrue(result.isSuccess());
    }

    @Test
    public void markAsSuccessful() {
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/successful", null, OK );
        RestResult<Void> result = service.markAsSuccessful(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/successful", Void.class);
        assertTrue(result.isSuccess());
    }

    @Test
    public void markAsSuccessfulLoan() {
        LocalDate projectStartDate = LocalDate.now().plusDays(10);
        String projectStartDateStr = projectStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/loans-successful?projectStartDate=" + projectStartDateStr, null, OK );
        RestResult<Void> result = service.markAsSuccessful(projectId, projectStartDate);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/loans-successful?projectStartDate=" + projectStartDateStr, Void.class);
        assertTrue(result.isSuccess());
    }

    @Test
    public void markAsUnsuccessful() {
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/unsuccessful", null, OK );
        RestResult<Void> result = service.markAsUnsuccessful(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/unsuccessful", Void.class);
        assertTrue(result.isSuccess());
    }
    @Test
    public void markAsSuccessfulLoanWithoutProjectStartDate() {
        LocalDate projectStartDate = null;
        String markAsSuccessfulURL = format("%s/%s/%s", projectRestURL, projectId, "loans-successful");
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(markAsSuccessfulURL);
        setupPostWithRestResultExpectations(builder.toUriString(), null, OK );
        RestResult<Void> result = service.markAsSuccessful(projectId, projectStartDate);
        setupPostWithRestResultVerifications(builder.toUriString(), Void.class);
        assertTrue(result.isSuccess());
    }
}
