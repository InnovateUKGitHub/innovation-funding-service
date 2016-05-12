package com.worth.ifs.application.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.util.MapFunctions;

public class ApplicationFundingDecisionRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationFundingDecisionRestServiceImpl> {

    private static final String applicationFundingDecisionRestURL = "/applicationfunding";

    @Override
    protected ApplicationFundingDecisionRestServiceImpl registerRestServiceUnderTest() {
    	ApplicationFundingDecisionRestServiceImpl applicationFundingDecisionRestService = new ApplicationFundingDecisionRestServiceImpl();
    	applicationFundingDecisionRestService.setApplicationFundingDecisionRestURL(applicationFundingDecisionRestURL);
    	return applicationFundingDecisionRestService;
    }

    @Test
    public void testMakeFundingDecision() {

    	Long competitionId = 123L;
    	Map<Long, FundingDecision> applicationIdToFundingDecision = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.FUNDED);
    	
        String expectedUrl = applicationFundingDecisionRestURL + "/" + 123 + "/submit";
        setupPostWithRestResultExpectations(expectedUrl, applicationIdToFundingDecision, HttpStatus.OK);

        RestResult<Void> result = service.makeApplicationFundingDecision(competitionId, applicationIdToFundingDecision);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    
    @Test
    public void testSaveFundingDecision() {

    	Long competitionId = 123L;
    	Map<Long, FundingDecision> applicationIdToFundingDecision = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.FUNDED);
    	
        String expectedUrl = applicationFundingDecisionRestURL + "/" + 123;
        setupPutWithRestResultExpectations(expectedUrl, applicationIdToFundingDecision, HttpStatus.OK);

        RestResult<Void> result = service.saveApplicationFundingDecisionData(competitionId, applicationIdToFundingDecision);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    
    @Test
    public void testGetFundingDecision() {

    	Long competitionId = 123L;
    	Map<Long, FundingDecision> applicationIdToFundingDecision = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.FUNDED);
    	
        String expectedUrl = applicationFundingDecisionRestURL + "/" + 123;
        setupGetWithRestResultExpectations(expectedUrl, new ParameterizedTypeReference<Map<Long, FundingDecision>>() {}, applicationIdToFundingDecision);

        RestResult<Map<Long, FundingDecision>> result = service.getApplicationFundingDecisionData(competitionId);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

}
