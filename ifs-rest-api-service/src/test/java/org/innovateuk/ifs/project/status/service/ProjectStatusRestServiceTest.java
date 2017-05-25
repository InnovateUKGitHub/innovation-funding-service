package org.innovateuk.ifs.project.status.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ProjectStatusRestServiceTest extends BaseRestServiceUnitTest<ProjectStatusRestServiceImpl> {

    private static final String competitionURL = "/project/competition";
    private static final String projectRestURL = "/project";

    @Override
    protected ProjectStatusRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectStatusRestServiceImpl();
    }

    @Test
    public void testGetProjectById() {

        CompetitionProjectsStatusResource returnedResponse = new CompetitionProjectsStatusResource();

        setupGetWithRestResultExpectations(competitionURL + "/123", CompetitionProjectsStatusResource.class, returnedResponse);

        RestResult<CompetitionProjectsStatusResource> result = service.getCompetitionStatus(123L);

        assertTrue(result.isSuccess());

        Assert.assertEquals(returnedResponse, result.getSuccessObject());
    }

    @Test
    public void testGetStatusByProjectId() {
        ProjectStatusResource returnedResponse = new ProjectStatusResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123/status", ProjectStatusResource.class, returnedResponse);
        ProjectStatusResource result = service.getProjectStatus(123L).getSuccessObject();
        Assert.assertEquals(returnedResponse, result);
    }

    @Test
    public void testGetProjectTeamStatus() {
        String expectedUrl = projectRestURL + "/123/team-status";

        setupGetWithRestResultExpectations(expectedUrl, ProjectTeamStatusResource.class, null, OK);

        RestResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(123L, Optional.empty());

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetProjectTeamStatusWithFilterByUserId() {
        String expectedUrl = projectRestURL + "/123/team-status?filterByUserId=456";

        setupGetWithRestResultExpectations(expectedUrl, ProjectTeamStatusResource.class, null, OK);

        RestResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(123L, Optional.of(456L));

        assertTrue(result.isSuccess());
    }
}
