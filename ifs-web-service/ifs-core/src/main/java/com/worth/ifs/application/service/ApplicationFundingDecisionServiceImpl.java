package com.worth.ifs.application.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	@Override
	public void saveApplicationFundingDecisionData(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision) {
		applicationFundingDecisionRestService.saveApplicationFundingDecisionData(competitionId, applicationIdToFundingDecision).getSuccessObjectOrThrowException();
	}
	
	@Override
	public boolean verifyAllApplicationsRepresented(Map<String, String[]> parameterMap, List<Long> applicationIds) {
		Map<Long, FundingDecision> applicationIdToFundingDecision = applicationIdToDeterminedFundingDecisionFromRequestParams(parameterMap, applicationIds);
		
		Set<Long> submittedIds = applicationIdToFundingDecision.keySet();
		
		List<Long> notSubmittedIds = applicationIds.stream()
				.filter(e -> submittedIds.stream().noneMatch(s -> s.equals(e)))
				.collect(Collectors.toList());

		return notSubmittedIds.isEmpty();
	}
	
	@Override
	public Map<Long, FundingDecision> applicationIdToFundingDecisionFromRequestParams(Map<String, String[]> parameterMap, List<Long> applicationIds) {
		return parameterMap.entrySet().stream()
				.filter(e -> keyIsApplicationId(e.getKey(), applicationIds) && valueIsValid(e.getValue()))
				.collect(Collectors.toMap(
		            e -> Long.parseLong(e.getKey()),
		            e -> fundingDecisionForStringArray(e.getValue())
		        ));
	}
	
	@Override
	public FundingDecision fundingDecisionForString(String val) {
		switch(val) {
			case "Y":
				return FundingDecision.FUNDED;
			case "N":
				return FundingDecision.UNFUNDED;
			case "-":
				return FundingDecision.UNDECIDED;
			default:
				return null;	
		}
	}
	
	private Map<Long, FundingDecision> applicationIdToDeterminedFundingDecisionFromRequestParams(Map<String, String[]> parameterMap, List<Long> applicationIds) {
		Map<Long, FundingDecision> applicationIdToFundingDecision = applicationIdToFundingDecisionFromRequestParams(parameterMap, applicationIds);
		return applicationIdToFundingDecision.entrySet().stream()
								.filter(e -> !FundingDecision.UNDECIDED.equals(e.getValue()))
								.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	
	private boolean valueIsValid(String[] value) {
		if(value.length == 0) {
			return false;
		}
		return ("Y".equals(value[0]) || "N".equals(value[0]) || "-".equals(value[0]));
	}

	private boolean keyIsApplicationId(String key, List<Long> applicationIds) {
		Long id;
		try {
			id = Long.parseLong(key);
		} catch(NumberFormatException e) {
			return false;
		}
		
		return applicationIds.stream().anyMatch(e -> e.equals(id));
	}
	
	private FundingDecision fundingDecisionForStringArray(String[] val) {
		return fundingDecisionForString(val[0]);
	}

}
