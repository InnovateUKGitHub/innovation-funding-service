package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementing class for {@link ApplicationFundingDecisionRestService}, for the action on deciding what applications should be funded for a given competition.
 */
@Service
public class ApplicationFundingDecisionRestServiceImpl extends BaseRestService implements ApplicationFundingDecisionRestService {

    private String applicationFundingDecisionRestURL = "/applicationfunding";

	@Override
	public RestResult<Void> saveApplicationFundingDecisionData(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision) {
		 return postWithRestResult(applicationFundingDecisionRestURL + "/" + competitionId, applicationIdToFundingDecision, Void.class);
	}

	protected void setApplicationFundingDecisionRestURL(String applicationFundingDecisionRestURL) {
		this.applicationFundingDecisionRestURL = applicationFundingDecisionRestURL;
	}

	public RestResult<Void> sendApplicationFundingDecisions(FundingNotificationResource fundingNotificationResource) {
		return postWithRestResult(applicationFundingDecisionRestURL + "/sendNotifications", fundingNotificationResource, Void.class);
	}

}
