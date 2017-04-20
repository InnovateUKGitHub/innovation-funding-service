package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.application.resource.FundingDecision.FUNDED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApplicationSummaryRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationSummaryRestServiceImpl> {

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
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getAllApplications(123L, null, 6, 20, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindByCompetitionWithSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getAllApplications(123L, "id", 6, 20, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void testFindByCompetitionWithFilter() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?filter=10&page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getAllApplications(123L, null, 6, 20, "10");

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    @Test
    public void testFindByCompetitionWithFilterAndSort() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?filter=10&page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getAllApplications(123L, "id", 6, 20, "10");

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindSubmittedApplicationsByCompetitionWithoutSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/submitted?page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getSubmittedApplications(123L, null, 6, 20, null, empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindSubmittedApplicationsByCompetitionWithSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/submitted?page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getSubmittedApplications(123L, "id", 6, 20, null, empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void testFindSubmittedApplicationsByCompetitionWithFilter() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/submitted?filter=10&fundingFilter=FUNDED&page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getSubmittedApplications(123L, null, 6, 20, "10", of(FUNDED));

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void testFindSubmittedApplicationsByCompetitionWithFilterAndSortField() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/submitted?filter=10&page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getSubmittedApplications(123L, "id", 6, 20, "10", empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void testFindIneligibleApplicationsByCompetitionWithoutSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/ineligible?page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getIneligibleApplications(123L, null, 6, 20, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void testFindIneligibleApplicationsByCompetitionWithSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/ineligible?page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getIneligibleApplications(123L, "id", 6, 20, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void testFindIneligibleApplicationsByCompetitionWithFilter() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/ineligible?filter=10&page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getIneligibleApplications(123L, null, 6, 20, "10");

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void testFindIneligibleApplicationsByCompetitionWithFilterAndSortField() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/ineligible?filter=10&page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getIneligibleApplications(123L, "id", 6, 20, "10");

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void testFindNotSubmittedApplicationsByCompetitionWithoutSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/not-submitted?page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getNonSubmittedApplications(123L, null, 6, 20, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindNotSubmittedApplicationsByCompetitionWithSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/not-submitted?page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getNonSubmittedApplications(123L, "id", 6, 20, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindFeedbackRequiredpplicationsByCompetitionWithoutSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/feedback-required?page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getFeedbackRequiredApplications(123L, null, 6, 20, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void testFindFeedbackRequiredApplicationsByCompetitionWithSortField() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/feedback-required?page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getFeedbackRequiredApplications(123L, "id", 6, 20, null);

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void testgetWithFundingDecisionApplications() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/with-funding-decision?filter=filter&sendFilter=false&fundingFilter=FUNDED&page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getWithFundingDecisionApplications(123L, "id", 6, 20, "filter", Optional.of(false), Optional.of(FUNDED));

        assertTrue(result.isSuccess());
        Assert.assertEquals(responseBody, result.getSuccessObject());
    }

}