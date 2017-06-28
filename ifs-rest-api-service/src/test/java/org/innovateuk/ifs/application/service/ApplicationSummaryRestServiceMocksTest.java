
package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
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
    public void findByCompetitionWithoutSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getAllApplications(123L, null, 6, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void findByCompetitionWithSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getAllApplications(123L, "id", 6, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void findByCompetitionWithFilter() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?filter=10&page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getAllApplications(123L, null, 6, 20, of("10"));

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    @Test
    public void findByCompetitionWithFilterAndSort() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123?filter=10&page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getAllApplications(123L, "id", 6, 20, of("10"));

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void findSubmittedApplicationsByCompetitionWithoutSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/submitted?page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getSubmittedApplications(123L, null, 6, 20, empty(), empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void findSubmittedApplicationsByCompetitionWithSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/submitted?page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getSubmittedApplications(123L, "id", 6, 20, empty(), empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void findSubmittedApplicationsByCompetitionWithFilter() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/submitted?filter=10&fundingFilter=FUNDED&page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getSubmittedApplications(123L, null, 6, 20, of("10"), of(FUNDED));

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void findSubmittedApplicationsByCompetitionWithFilterAndSortField() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/submitted?filter=10&page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getSubmittedApplications(123L, "id", 6, 20, of("10"), empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void findIneligibleApplicationsByCompetitionWithoutSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/ineligible?page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getIneligibleApplications(123L, null, 6, 20, empty(), empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void findIneligibleApplicationsByCompetitionWithSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/ineligible?page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getIneligibleApplications(123L, "id", 6, 20, empty(), empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void findIneligibleApplicationsByCompetitionWithFilter() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/ineligible?filter=10&informFilter=true&page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getIneligibleApplications(123L, null, 6, 20, of("10"), of(true));

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void findIneligibleApplicationsByCompetitionWithFilterAndSortField() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/ineligible?filter=10&informFilter=false&page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getIneligibleApplications(123L, "id", 6, 20, of("10"), of(false));

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void findNotSubmittedApplicationsByCompetitionWithoutSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/not-submitted?page=6&size=20", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getNonSubmittedApplications(123L, null, 6, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }
    
    @Test
    public void findNotSubmittedApplicationsByCompetitionWithSortField() {
    	ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/not-submitted?page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getNonSubmittedApplications(123L, "id", 6, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void getWithFundingDecisionApplications() {
        ApplicationSummaryPageResource responseBody = new ApplicationSummaryPageResource();
        setupGetWithRestResultExpectations(APPLICATION_SUMMARY_REST_URL + "/findByCompetition/123/with-funding-decision?filter=filter&sendFilter=false&fundingFilter=FUNDED&page=6&size=20&sort=id", ApplicationSummaryPageResource.class, responseBody);

        RestResult<ApplicationSummaryPageResource> result = service.getWithFundingDecisionApplications(123L, "id", 6, 20, of("filter"), Optional.of(false), Optional.of(FUNDED));

        assertTrue(result.isSuccess());
        assertEquals(responseBody, result.getSuccessObject());
    }

    @Test
    public void getAllWithFundingDecisionApplications() {
        List<ApplicationSummaryResource> appSummaries = newApplicationSummaryResource().build(2);

        setupGetWithRestResultExpectations(
                format("%s/%s/%s/%s?all&filter=filter&sendFilter=false&fundingFilter=FUNDED", APPLICATION_SUMMARY_REST_URL, "findByCompetition", 123L, "with-funding-decision"),
                ParameterizedTypeReferences.applicationSummaryResourceListType(),
                appSummaries
        );

        RestResult<List<ApplicationSummaryResource>> result = service.getWithFundingDecisionApplications(123L, of("filter"), Optional.of(false), Optional.of(FUNDED));

        assertTrue(result.isSuccess());
        assertEquals(appSummaries, result.getSuccessObject());
    }
}