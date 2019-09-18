package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.builder.AssessorCountSummaryPageResourceBuilder;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
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
        String expectedUrl = "/assessor-count-summary/find-by-competition-id/1?assessorNameFilter=name&page=2&size=3";
        AssessorCountSummaryPageResource pageResource = AssessorCountSummaryPageResourceBuilder.newAssessorCountSummaryPageResource().build();

        setupGetWithRestResultExpectations(expectedUrl, AssessorCountSummaryPageResource.class, pageResource, OK);

        AssessorCountSummaryPageResource result = service.getAssessorCountSummariesByCompetitionId(1L, "name", 2, 3).getSuccess();
        assertEquals(pageResource, result);
    }
}