package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.Map;

/**
 * Interface for the action for the funding decisions.
 */
public interface ApplicationFundingDecisionRestService {
	
	/**
	 * Make the funding decision - this will affect the competition and the applications.
	 * @param competitionId the id of the competition for which we are providing the funding decision
	 * @param applicationIdToFundingDecision a map of all application ids for the competition to the decision for each
	 * @return rest result to indicate if this has successfully been done
	 */
	//TODO: reuse or remove this and subsequent methods after implementation of INFUND-7378
	RestResult<Void> makeApplicationFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision);

	/**
	 * Save funding decision, so it can be retrieved and worked on at a later time.
	 * @param competitionId the id of the competition for which we are providing the funding decision
	 * @param applicationIdToFundingDecision a map of all application ids for the competition to the decision for each
	 * @return rest result to indicate if this has successfully been done
	 */
	RestResult<Void> saveApplicationFundingDecisionData(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision);
}
