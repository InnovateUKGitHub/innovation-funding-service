package org.innovateuk.ifs.project.status.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class StatusRestServiceImplTest extends BaseRestServiceUnitTest<StatusRestServiceImpl> {

    private static final String competitionURL = "/project/competition";
    private static final String projectRestURL = "/project";

    @Override
    protected StatusRestServiceImpl registerRestServiceUnderTest() {
        return new StatusRestServiceImpl();
    }

    @Test
    public void getCompetitionStatus() {

        Long competitionId = 1L;
        String applicationSearchString = "12";

        CompetitionProjectsStatusResource returnedResponse = new CompetitionProjectsStatusResource();

        setupGetWithRestResultExpectations(competitionURL + "/" + competitionId + "?applicationSearchString=" + applicationSearchString, CompetitionProjectsStatusResource.class, returnedResponse);

        RestResult<CompetitionProjectsStatusResource> result = service.getCompetitionStatus(competitionId, applicationSearchString);

        assertTrue(result.isSuccess());

        Assert.assertEquals(returnedResponse, result.getSuccess());
        setupGetWithRestResultVerifications(competitionURL + "/" + competitionId + "?applicationSearchString=" + applicationSearchString, null, CompetitionProjectsStatusResource.class);
    }

    @Test
    public void getStatusByProjectId() {
        ProjectStatusResource returnedResponse = new ProjectStatusResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123/status", ProjectStatusResource.class, returnedResponse);
        ProjectStatusResource result = service.getProjectStatus(123L).getSuccess();
        Assert.assertEquals(returnedResponse, result);
        setupGetWithRestResultVerifications(projectRestURL + "/123/status", null, ProjectStatusResource.class);
    }

    @Test
    public void getProjectTeamStatus() {
        String expectedUrl = projectRestURL + "/123/team-status";

        setupGetWithRestResultExpectations(expectedUrl, ProjectTeamStatusResource.class, null, OK);

        RestResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(123L, Optional.empty());

        assertTrue(result.isSuccess());
        setupGetWithRestResultVerifications(projectRestURL + "/123/team-status", null, ProjectTeamStatusResource.class);
    }

    @Test
    public void getProjectTeamStatusWithFilterByUserId() {
        String expectedUrl = projectRestURL + "/123/team-status?filterByUserId=456";

        setupGetWithRestResultExpectations(expectedUrl, ProjectTeamStatusResource.class, null, OK);

        RestResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(123L, Optional.of(456L));

        assertTrue(result.isSuccess());
        setupGetWithRestResultVerifications(projectRestURL + "/123/team-status?filterByUserId=456", null, ProjectTeamStatusResource.class);
    }
}
