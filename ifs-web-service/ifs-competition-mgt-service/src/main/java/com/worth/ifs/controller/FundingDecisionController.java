package com.worth.ifs.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.application.service.ApplicationFundingDecisionService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.filter.CookieFlashMessageFilter;

/**
 * This controller gets the decision to fund or not fund the applications for a given competition.
 */
@Controller
public class FundingDecisionController {
	
	@Autowired
	private ApplicationSummaryService applicationSummaryService;
	
	@Autowired
	private ApplicationFundingDecisionService applicationFundingDecisionService;
	
	@Autowired
	private CookieFlashMessageFilter cookieFlashMessageFilter;
	
	@RequestMapping(method = RequestMethod.POST, value = "/competition/{competitionId}/fundingdecision")
    public String fundingDecisionCheck(Model model, HttpServletRequest request, HttpServletResponse response, @PathVariable("competitionId") Long competitionId){
		
		List<Long> applicationIds = submittedApplicationIdsForCompetition(competitionId);

		if(!validate(request, applicationIds)) {
			cookieFlashMessageFilter.setFlashMessage(response, "fundingNotDecidedForAllApplications");
			return "redirect:/competition/" + competitionId;
		}
		
		Map<Long, FundingDecision> applicationIdToFundingDecision = applicationIdToFundingDecisionFromRequest(request, applicationIds);

		model.addAttribute("competitionId", competitionId);
		model.addAttribute("applicationFundingDecisions", applicationIdToFundingDecision);
		
		return "funding-decision-confirmation";
    }
	
	@RequestMapping(method = RequestMethod.POST, value = "/competition/{competitionId}/fundingdecisionsubmit")
    public String submitFundingDecision(HttpServletRequest request, HttpServletResponse response, @PathVariable("competitionId") Long competitionId){
		
		List<Long> applicationIds = submittedApplicationIdsForCompetition(competitionId);

		if(!validate(request, applicationIds)) {
			cookieFlashMessageFilter.setFlashMessage(response, "fundingNotDecidedForAllApplications");
			return "redirect:/competition/" + competitionId;
		}
		
		Map<Long, FundingDecision> applicationIdToFundingDecision = applicationIdToFundingDecisionFromRequest(request, applicationIds);

		applicationFundingDecisionService.makeApplicationFundingDecision(competitionId, applicationIdToFundingDecision);

		return "redirect:/competition/" + competitionId;
    }

	private boolean validate(HttpServletRequest request, List<Long> applicationIds) {
		Map<Long, FundingDecision> applicationIdToFundingDecision = applicationIdToFundingDecisionFromRequest(request, applicationIds);
		Set<Long> submittedIds = applicationIdToFundingDecision.keySet();
		
		List<Long> notSubmittedIds = applicationIds.stream()
				.filter(e -> submittedIds.stream().noneMatch(s -> s.equals(e)))
				.collect(Collectors.toList());

		return notSubmittedIds.isEmpty();
	}
	
	private List<Long> submittedApplicationIdsForCompetition(Long competitionId) {
		return applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionId, null, 0, Integer.MAX_VALUE).getContent()
				.stream().map(e -> e.getId()).collect(Collectors.toList());
	}

	private Map<Long, FundingDecision> applicationIdToFundingDecisionFromRequest(HttpServletRequest request, List<Long> applicationIds) {
		return request.getParameterMap().entrySet().stream()
				.filter(e -> keyIsApplicationId(e.getKey(), applicationIds) && valueIsDecided(e.getValue()))
				.collect(Collectors.toMap(
		            e -> Long.parseLong(e.getKey()),
		            e -> fundingDecisionForString(e.getValue())
		        ));
	}
	
	private boolean valueIsDecided(String[] value) {
		if(value.length == 0) {
			return false;
		}
		return ("Y".equals(value[0]) || "N".equals(value[0]));
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
	
	private FundingDecision fundingDecisionForString(String[] val) {
		FundingDecision decision;
		if ("Y".equals(val[0])) {
			decision = FundingDecision.FUNDED;
		} else {
			decision = FundingDecision.UNFUNDED;
		}
		return decision;
	}
}
