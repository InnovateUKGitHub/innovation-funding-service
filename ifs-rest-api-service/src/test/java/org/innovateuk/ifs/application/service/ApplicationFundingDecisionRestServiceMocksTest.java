package org.innovateuk.ifs.application.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.util.MapFunctions;

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
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    
    @Test
    public void testSaveFundingDecision() {

    	Long competitionId = 123L;
    	Map<Long, FundingDecision> applicationIdToFundingDecision = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.FUNDED);
    	
        String expectedUrl = applicationFundingDecisionRestURL + "/" + 123;
        setupPostWithRestResultExpectations(expectedUrl, applicationIdToFundingDecision, HttpStatus.OK);

        RestResult<Void> result = service.saveApplicationFundingDecisionData(competitionId, applicationIdToFundingDecision);
        assertNotNull(result);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    
}
