package com.worth.ifs.application.service;

import java.util.Map;

import com.worth.ifs.application.resource.FundingDecision;

/**
 * Service for making the decision of which applications will receive funding and which will not for a given competition.
 */
public interface ApplicationFundingDecisionService {

	/**
	 * make the decision of what applications will receive funding and which will not for a given competition.
	 * @param competitionId the id of the competition
	 * @param applicationIdToFundingDecision map of application ids to funding decisions
	 */
	void makeApplicationFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision);
}
