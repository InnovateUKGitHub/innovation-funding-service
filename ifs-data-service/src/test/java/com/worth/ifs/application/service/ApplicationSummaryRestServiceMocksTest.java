package com.worth.ifs.application.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
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
    public void testGetById() {
    	ApplicationSummaryResource responseBody = new ApplicationSummaryResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/123", ApplicationSummaryResource.class, responseBody);

        RestResult<ApplicationSummaryResource> result = service.getApplicationSummary(Long.valueOf(123L));

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindByCompetition() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?page=6", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.findByCompetitionId(Long.valueOf(123L), 6);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

}
