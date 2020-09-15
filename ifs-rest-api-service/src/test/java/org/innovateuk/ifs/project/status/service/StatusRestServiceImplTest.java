package org.innovateuk.ifs.project.status.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.status.resource.ProjectStatusPageResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectStatusResourceListType;
import static org.junit.Assert.assertEquals;
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

        ProjectStatusPageResource returnedResponse = new ProjectStatusPageResource();

        setupGetWithRestResultExpectations(competitionURL + "/" + competitionId + "?applicationSearchString=" + applicationSearchString + "&page=1", ProjectStatusPageResource.class, returnedResponse);

        RestResult<ProjectStatusPageResource> result = service.getCompetitionStatus(competitionId, applicationSearchString, 1);

        assertTrue(result.isSuccess());

        assertEquals(returnedResponse, result.getSuccess());
    }

    @Test
    public void getPreviousCompetitionStatus() {
        Long competitionId = 1L;

        List<ProjectStatusResource> returnedResponse = asList(new ProjectStatusResource());

        setupGetWithRestResultExpectations("/project/previous/competition/" + competitionId, projectStatusResourceListType(), returnedResponse);

        RestResult<List<ProjectStatusResource>> result = service.getPreviousCompetitionStatus(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(returnedResponse, result.getSuccess());
    }
    @Test
    public void getStatusByProjectId() {
        ProjectStatusResource returnedResponse = new ProjectStatusResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123/status", ProjectStatusResource.class, returnedResponse);
        ProjectStatusResource result = service.getProjectStatus(123L).getSuccess();
        assertEquals(returnedResponse, result);
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
