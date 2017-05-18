package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.Map;

/**
 * Interface for the action for the funding decisions.
 */
public interface ApplicationFundingDecisionRestService {

	RestResult<Void> saveApplicationFundingDecisionData(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision);
	RestResult<Void> sendApplicationFundingDecisions(FundingNotificationResource fundingNotificationResource);
}
