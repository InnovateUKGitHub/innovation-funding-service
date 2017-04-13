package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class ApplicationFundingDecisionRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationFundingDecisionRestServiceImpl> {

    private static final String applicationFundingDecisionRestURL = "/applicationfunding";

    @Override
    protected ApplicationFundingDecisionRestServiceImpl registerRestServiceUnderTest() {
    	ApplicationFundingDecisionRestServiceImpl applicationFundingDecisionRestService = new ApplicationFundingDecisionRestServiceImpl();
    	applicationFundingDecisionRestService.setApplicationFundingDecisionRestURL(applicationFundingDecisionRestURL);
    	return applicationFundingDecisionRestService;
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

    @Test
    public void testSendFundingDecision() {
        Map<Long, FundingDecision> decisions = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.ON_HOLD);

        FundingNotificationResource notification = new FundingNotificationResource("Body.", decisions);

        String expectedUrl = applicationFundingDecisionRestURL + "/sendNotifications";
        setupPostWithRestResultExpectations(expectedUrl, notification, HttpStatus.OK);

        RestResult<Void> result = service.sendApplicationFundingDecisions(notification);
        assertNotNull(result);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    
}
