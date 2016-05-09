package com.worth.ifs.application.service;

import java.util.Map;

import com.worth.ifs.application.resource.FundingDecision;

public interface ApplicationFundingDecisionService {

	void makeApplicationFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision);
}
