package org.innovateuk.ifs.project.status.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.service.ProjectStatusRestServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProjectStatusRestServiceTest extends BaseRestServiceUnitTest<ProjectStatusRestServiceImpl> {

    private static final String competitionURL = "/project/competition";

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
}
