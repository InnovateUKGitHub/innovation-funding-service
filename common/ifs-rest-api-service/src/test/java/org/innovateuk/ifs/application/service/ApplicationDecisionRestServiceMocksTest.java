package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class ApplicationDecisionRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationDecisionRestServiceImpl> {

    private static final String applicationDecisionRestURL = "/applicationfunding";

    @Override
    protected ApplicationDecisionRestServiceImpl registerRestServiceUnderTest() {
    	ApplicationDecisionRestServiceImpl applicationDecisionRestService = new ApplicationDecisionRestServiceImpl();
    	applicationDecisionRestService.setApplicationDecisionRestURL(applicationDecisionRestURL);
    	return applicationDecisionRestService;
    }
    
    @Test
    public void testSaveDecision() {

    	Long competitionId = 123L;
    	Map<Long, Decision> applicationIdToDecision = MapFunctions.asMap(1L, Decision.FUNDED, 2L, Decision.UNFUNDED, 3L, Decision.FUNDED);
    	
        String expectedUrl = applicationDecisionRestURL + "/" + 123;
        setupPostWithRestResultExpectations(expectedUrl, applicationIdToDecision, HttpStatus.OK);

        RestResult<Void> result = service.saveApplicationDecisionData(competitionId, applicationIdToDecision);
        assertNotNull(result);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testSendDecision() {
        Map<Long, Decision> decisions = MapFunctions.asMap(1L, Decision.FUNDED, 2L, Decision.UNFUNDED, 3L, Decision.ON_HOLD);

        FundingNotificationResource notification = new FundingNotificationResource("Body.", decisions);

        String expectedUrl = applicationDecisionRestURL + "/send-notifications";
        setupPostWithRestResultExpectations(expectedUrl, notification, HttpStatus.OK);

        RestResult<Void> result = service.sendApplicationDecisions(notification);
        assertNotNull(result);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    
}
