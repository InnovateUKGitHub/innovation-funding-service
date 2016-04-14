package com.worth.ifs.application.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryPageResource;
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
    	ClosedCompetitionSubmittedApplicationSummaryPageResource responseBody = new ClosedCompetitionSubmittedApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByClosedCompetition/123/submitted?page=6", ClosedCompetitionSubmittedApplicationSummaryPageResource.class, responseBody);

        RestResult<ClosedCompetitionSubmittedApplicationSummaryPageResource> result = service.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 6, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindSubmittedApplicationsByClosedCompetitionWithSortField() {
    	ClosedCompetitionSubmittedApplicationSummaryPageResource responseBody = new ClosedCompetitionSubmittedApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByClosedCompetition/123/submitted?page=6&sort=id", ClosedCompetitionSubmittedApplicationSummaryPageResource.class, responseBody);

        RestResult<ClosedCompetitionSubmittedApplicationSummaryPageResource> result = service.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 6, "id");

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindNotSubmittedApplicationsByClosedCompetitionWithoutSortField() {
    	ClosedCompetitionNotSubmittedApplicationSummaryPageResource responseBody = new ClosedCompetitionNotSubmittedApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByClosedCompetition/123/not-submitted?page=6", ClosedCompetitionNotSubmittedApplicationSummaryPageResource.class, responseBody);

        RestResult<ClosedCompetitionNotSubmittedApplicationSummaryPageResource> result = service.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 6, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindNotSubmittedApplicationsByClosedCompetitionWithSortField() {
    	ClosedCompetitionNotSubmittedApplicationSummaryPageResource responseBody = new ClosedCompetitionNotSubmittedApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByClosedCompetition/123/not-submitted?page=6&sort=id", ClosedCompetitionNotSubmittedApplicationSummaryPageResource.class, responseBody);

        RestResult<ClosedCompetitionNotSubmittedApplicationSummaryPageResource> result = service.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 6, "id");

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

}
