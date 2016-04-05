package com.worth.ifs.application.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryPageResource;
import com.worth.ifs.commons.rest.RestResult;

public class ApplicationSummaryRestServiceMocksTest  extends BaseRestServiceUnitTest<ApplicationSummaryRestServiceImpl> {

    private static final String APPLICATION_SUMMARY_REST_URL = "/appsummary";

    @Override
    protected ApplicationSummaryRestServiceImpl registerRestServiceUnderTest() {
    	ApplicationSummaryRestServiceImpl applicationSummaryRestService = new ApplicationSummaryRestServiceImpl();
    	applicationSummaryRestService.setApplicationSummaryRestUrl(APPLICATION_SUMMARY_REST_URL);
    	return applicationSummaryRestService;
    }

    @Test
    public void testFindByCompetitionWithoutSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?page=6", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.findByCompetitionId(Long.valueOf(123L), 6, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindByCompetitionWithSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?page=6&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.findByCompetitionId(Long.valueOf(123L), 6, "id");

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindSubmittedApplicationsByClosedCompetitionWithoutSortField() {
    	ClosedCompetitionApplicationSummaryPageResource responseBody = new ClosedCompetitionApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByClosedCompetition/123/submitted?page=6", ClosedCompetitionApplicationSummaryPageResource.class, responseBody);

        RestResult<ClosedCompetitionApplicationSummaryPageResource> result = service.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 6, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindSubmittedApplicationsByClosedCompetitionWithSortField() {
    	ClosedCompetitionApplicationSummaryPageResource responseBody = new ClosedCompetitionApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByClosedCompetition/123/submitted?page=6&sort=id", ClosedCompetitionApplicationSummaryPageResource.class, responseBody);

        RestResult<ClosedCompetitionApplicationSummaryPageResource> result = service.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 6, "id");

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindNotSubmittedApplicationsByClosedCompetitionWithoutSortField() {
    	ClosedCompetitionApplicationSummaryPageResource responseBody = new ClosedCompetitionApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByClosedCompetition/123/not-submitted?page=6", ClosedCompetitionApplicationSummaryPageResource.class, responseBody);

        RestResult<ClosedCompetitionApplicationSummaryPageResource> result = service.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 6, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindNotSubmittedApplicationsByClosedCompetitionWithSortField() {
    	ClosedCompetitionApplicationSummaryPageResource responseBody = new ClosedCompetitionApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByClosedCompetition/123/not-submitted?page=6&sort=id", ClosedCompetitionApplicationSummaryPageResource.class, responseBody);

        RestResult<ClosedCompetitionApplicationSummaryPageResource> result = service.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 6, "id");

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

}
