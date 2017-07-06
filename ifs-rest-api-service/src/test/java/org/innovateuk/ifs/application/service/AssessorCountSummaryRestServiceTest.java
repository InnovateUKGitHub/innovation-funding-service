package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.junit.Assert;
import org.junit.Test;

import static org.springframework.http.HttpStatus.OK;

public class AssessorCountSummaryRestServiceTest extends BaseRestServiceUnitTest<AssessorCountSummaryRestServiceImpl> {

    @Override
    protected  AssessorCountSummaryRestServiceImpl registerRestServiceUnderTest() {
        return new  AssessorCountSummaryRestServiceImpl();
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() {
        String expectedUrl = "/assessorCountSummary/findByCompetitionId/1?filter=filter&page=2&size=3";
        AssessorCountSummaryPageResource pageResource = new AssessorCountSummaryPageResource();

        setupGetWithRestResultExpectations(expectedUrl, AssessorCountSummaryPageResource.class, pageResource, OK);

        AssessorCountSummaryPageResource result = service.getAssessorCountSummariesByCompetitionId(1L, 2, 3).getSuccessObject();
        Assert.assertEquals(pageResource, result);
    }
}
