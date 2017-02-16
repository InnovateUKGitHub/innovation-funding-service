package org.innovateuk.ifs.application.service;

import org.apache.catalina.util.ParameterMap;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApplicationFundingDecisionServiceImpl implements ApplicationFundingDecisionService {

	@Autowired
	private ApplicationFundingDecisionRestService applicationFundingDecisionRestService;


	//TODO: remove this and subsequent methods after implementation of INFUND-7378
	/*
	@Override
	public void makeApplicationFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision) {
		applicationFundingDecisionRestService.makeApplicationFundingDecision(competitionId, applicationIdToFundingDecision).getSuccessObjectOrThrowException();
	}*/

	@Autowired
	private ApplicationSummaryService applicationSummaryService;
	
	@Override
	public ServiceResult<Void> saveApplicationFundingDecisionData(Long competitionId, String fundingDecision, List<Long> applicationIds) {
		Map<Long, FundingDecision> applicationIdToFundingDecision = createSubmittedApplicationFundingDecisionMap(applicationIds, competitionId, fundingDecision);
		applicationFundingDecisionRestService.saveApplicationFundingDecisionData(competitionId, applicationIdToFundingDecision).getSuccessObjectOrThrowException();
		return ServiceResult.serviceSuccess();
	}

	public boolean isAllowedFundingDecision(String fundingDecisionString) {
		FundingDecision fundingDecision = fundingDecisionForString(fundingDecisionString);
		if(fundingDecision.equals(FundingDecision.UNDECIDED)) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public FundingDecision fundingDecisionForString(String val) {
		return FundingDecision.valueOf(val);
	}

	private List<Long> submittedApplicationIdsForCompetition(Long competitionId) {
		return applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionId, null, 0, Integer.MAX_VALUE).getContent()
				.stream().map(e -> e.getId()).collect(Collectors.toList());
	}

	private Map<Long, FundingDecision> createSubmittedApplicationFundingDecisionMap(List<Long> applicationIds, Long competitionId, String fundingDecisionChoice) {
		FundingDecision fundingDecision = fundingDecisionForString(fundingDecisionChoice);

		Map<Long, FundingDecision> applicationIdToFundingDecision = new HashMap<>();

		if(fundingDecision != null) {
			applicationIdToFundingDecision = filteredListOfFundingDecisions(applicationIds, competitionId, fundingDecision);
		}

		return applicationIdToFundingDecision;
	}

	private Map<Long, FundingDecision> filteredListOfFundingDecisions(List<Long> applicationIds, Long competitionId, FundingDecision fundingDecision) {
		Map<Long, FundingDecision> applicationIdToFundingDecision = new ParameterMap<>();

		List<Long> ids = submittedApplicationIdsForCompetition(competitionId);
		applicationIds.stream()
				.filter(id -> ids.contains(id))
				.forEach(id -> applicationIdToFundingDecision.put(id, fundingDecision));

		return applicationIdToFundingDecision;
	}
}
