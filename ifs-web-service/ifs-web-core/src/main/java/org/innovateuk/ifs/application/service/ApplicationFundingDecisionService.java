package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;
import java.util.Optional;

/**
 * Service for making the decision of which applications will receive funding and which will not for a given competition.
 */
public interface ApplicationFundingDecisionService {
	/**
	 * save the funding decision data
	 * @param competitionId the id of the competition
	 * @param fundingDecision fundingDecision that the applications should be set to
	 * @param applicationIds application ids that will have their funding decision changed
	 */
	ServiceResult<Void> saveApplicationFundingDecisionData(Long competitionId, FundingDecision fundingDecision, List<Long> applicationIds);

	Optional<FundingDecision> getFundingDecisionForString(String fundingDecisionName);
}
