package com.worth.ifs.application.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;

/**
 * Implementing class for {@link ApplicationFundingDecisionRestService}, for the action on deciding what applications should be funded for a given competition.
 */
public class ApplicationFundingDecisionRestServiceImpl extends BaseRestService implements ApplicationFundingDecisionRestService {

	@Value("${ifs.data.service.rest.applicationFundingDecision}")
    private String applicationFundingDecisionRestURL;

	@Override
	public RestResult<Void> makeApplicationFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision) {
		 return postWithRestResult(applicationFundingDecisionRestURL + "/" + competitionId, applicationIdToFundingDecision, Void.class);
	}
	
	protected void setApplicationFundingDecisionRestURL(String applicationFundingDecisionRestURL) {
		this.applicationFundingDecisionRestURL = applicationFundingDecisionRestURL;
	}

}
