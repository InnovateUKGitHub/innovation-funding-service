package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.builder.AssessorCountSummaryPageResourceBuilder;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class AssessorCountSummaryRestServiceTest extends BaseRestServiceUnitTest<AssessorCountSummaryRestServiceImpl> {

    @Override
    protected  AssessorCountSummaryRestServiceImpl registerRestServiceUnderTest() {
        return new AssessorCountSummaryRestServiceImpl();
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() {
        String expectedUrl = "/assessorCountSummary/findByCompetitionId/1?innovationSector=5&businessType=ACADEMIC&page=2&size=3";
        AssessorCountSummaryPageResource pageResource = AssessorCountSummaryPageResourceBuilder.newAssessorCountSummaryPageResource().build();

        setupGetWithRestResultExpectations(expectedUrl, AssessorCountSummaryPageResource.class, pageResource, OK);

        AssessorCountSummaryPageResource result = service.getAssessorCountSummariesByCompetitionId(1L, Optional.of(5L), Optional.of(BusinessType.ACADEMIC), 2, 3).getSuccessObject();
        assertEquals(pageResource, result);
    }
}