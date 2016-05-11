package com.worth.ifs.application.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.resource.FundingDecision;

@Service
public class ApplicationFundingDecisionServiceImpl implements ApplicationFundingDecisionService {

	@Autowired
	private ApplicationFundingDecisionRestService applicationFundingDecisionRestService;

	@Override
	public void makeApplicationFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision) {
		applicationFundingDecisionRestService.makeApplicationFundingDecision(competitionId, applicationIdToFundingDecision).getSuccessObjectOrThrowException();
	}
}
