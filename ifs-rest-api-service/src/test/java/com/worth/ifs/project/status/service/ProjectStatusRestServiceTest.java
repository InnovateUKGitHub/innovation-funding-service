package com.worth.ifs.project.status.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.service.ProjectStatusRestServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import static com.worth.ifs.project.builder.CompetitionProjectsStatusResourceBuilder.newCompetitionProjectsStatusResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectStatusRestServiceTest extends BaseRestServiceUnitTest<ProjectStatusRestServiceImpl> {

    private static final String competitionURL = "/project/competition";

    @Override
    protected ProjectStatusRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectStatusRestServiceImpl();
    }

    @Test
    public void testGetProjectById() {

        CompetitionProjectsStatusResource returnedResponse = newCompetitionProjectsStatusResource().build();

        setupGetWithRestResultExpectations(competitionURL + "/123", CompetitionProjectsStatusResource.class, returnedResponse);

        RestResult<CompetitionProjectsStatusResource> result = service.getCompetitionStatus(123L);

        assertTrue(result.isSuccess());

        Assert.assertEquals(returnedResponse, result.getSuccessObject());
    }
}
