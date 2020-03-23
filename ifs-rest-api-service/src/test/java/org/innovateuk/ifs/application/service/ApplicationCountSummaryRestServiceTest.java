package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;
import static org.springframework.http.HttpStatus.OK;

public class ApplicationCountSummaryRestServiceTest extends BaseRestServiceUnitTest<ApplicationCountSummaryRestServiceImpl> {

    @Override
    protected ApplicationCountSummaryRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationCountSummaryRestServiceImpl();
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() {
        String expectedUrl = "/application-count-summary/find-by-competition-id/1?filter=filter&page=2&size=3";
        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();

        setupGetWithRestResultExpectations(expectedUrl, ApplicationCountSummaryPageResource.class, pageResource, OK);

        ApplicationCountSummaryPageResource result = service.getApplicationCountSummariesByCompetitionId(1L, 2, 3, "filter").getSuccess();
        Assert.assertEquals(pageResource, result);
    }

    @Test
    public void getApplicationCountSummariesByCompetitionIdAndAssessorId() {
        String expectedUrl = "/application-count-summary/find-by-competition-id-and-assessor-id/1/10?page=2&filter=filter&sort=ASSESSORS";
        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();

        setupGetWithRestResultExpectations(expectedUrl, ApplicationCountSummaryPageResource.class, pageResource, OK);

        ApplicationCountSummaryPageResource result = service.getApplicationCountSummariesByCompetitionIdAndAssessorId(1L, 10L,2, Sort.ASSESSORS, "filter").getSuccess();
        Assert.assertEquals(pageResource, result);
    }

    @Test
    public void getApplicationIdsByCompetitionIdAndAssessorId() {
        String expectedUrl = "/application-count-summary/find-ids-by-competition-id-and-assessor-id/1/10?filter=filter";
        List<Long> list = asList(1L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), list, OK);

        List<Long> result = service.getApplicationIdsByCompetitionIdAndAssessorId(1L, 10L,"filter").getSuccess();
        Assert.assertEquals(list, result);
    }
}
