package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.builder.AssessorCountSummaryPageResourceBuilder;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class AssessorCountSummaryRestServiceTest extends BaseRestServiceUnitTest<AssessorCountSummaryRestServiceImpl> {

    @Override
    protected  AssessorCountSummaryRestServiceImpl registerRestServiceUnderTest() {
        return new AssessorCountSummaryRestServiceImpl();
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() {
        String expectedUrl = "/assessorCountSummary/findByCompetitionId/1?page=2&size=3";
        AssessorCountSummaryPageResource pageResource = AssessorCountSummaryPageResourceBuilder.newAssessorCountSummaryPageResource().build();

        setupGetWithRestResultExpectations(expectedUrl, AssessorCountSummaryPageResource.class, pageResource, OK);

        AssessorCountSummaryPageResource result = service.getAssessorCountSummariesByCompetitionId(1L, 2, 3).getSuccessObject();
        assertEquals(pageResource, result);
    }
}
