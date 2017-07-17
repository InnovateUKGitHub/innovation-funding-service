package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.OK;

public class ApplicationCountSummaryRestServiceTest extends BaseRestServiceUnitTest<ApplicationCountSummaryRestServiceImpl> {

    @Override
    protected ApplicationCountSummaryRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationCountSummaryRestServiceImpl();
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() {
        String expectedUrl = "/applicationCountSummary/findByCompetitionId/1?filter=filter&page=2&size=3";
        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();

        setupGetWithRestResultExpectations(expectedUrl, ApplicationCountSummaryPageResource.class, pageResource, OK);

        ApplicationCountSummaryPageResource result = service.getApplicationCountSummariesByCompetitionId(1L, 2, 3, "filter").getSuccessObject();
        Assert.assertEquals(pageResource, result);
    }

    @Test
    public void getApplicationCountSummariesByCompetitionIdAndInnovationArea() {
        String expectedUrl = "/applicationCountSummary/findByCompetitionIdAndInnovationArea/1?page=2&size=3&innovationArea=4";
        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();

        setupGetWithRestResultExpectations(expectedUrl, ApplicationCountSummaryPageResource.class, pageResource, OK);

        ApplicationCountSummaryPageResource result = service.getApplicationCountSummariesByCompetitionIdAndInnovationArea(1L, 2, 3, ofNullable(4L), "").getSuccessObject();
        Assert.assertEquals(pageResource, result);
    }
}
