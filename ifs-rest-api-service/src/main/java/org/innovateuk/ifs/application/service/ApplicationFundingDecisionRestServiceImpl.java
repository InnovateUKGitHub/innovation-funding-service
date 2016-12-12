package org.innovateuk.ifs.application.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;

/**
 * Implementing class for {@link ApplicationFundingDecisionRestService}, for the action on deciding what applications should be funded for a given competition.
 */
@Service
public class ApplicationFundingDecisionRestServiceImpl extends BaseRestService implements ApplicationFundingDecisionRestService {

    private String applicationFundingDecisionRestURL = "/applicationfunding";

	@Override
	public RestResult<Void> makeApplicationFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision) {
		 return postWithRestResult(applicationFundingDecisionRestURL + "/" + competitionId + "/submit", applicationIdToFundingDecision, Void.class);
	}

	@Override
	public RestResult<Void> saveApplicationFundingDecisionData(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision) {
		 return postWithRestResult(applicationFundingDecisionRestURL + "/" + competitionId, applicationIdToFundingDecision, Void.class);
	}

	protected void setApplicationFundingDecisionRestURL(String applicationFundingDecisionRestURL) {
		this.applicationFundingDecisionRestURL = applicationFundingDecisionRestURL;
	}

}
