package com.worth.ifs.controller;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.application.service.ApplicationFundingDecisionService;

@Controller
@RequestMapping("/competition/{competitionId}/fundingdecision")
public class FundingDecisionController {
	
	@Autowired
	private ApplicationFundingDecisionService applicationFundingDecisionService;
	
	@RequestMapping(method = RequestMethod.POST)
    public String submitFundingDecision(HttpServletRequest request, @PathVariable("competitionId") Long competitionId){
		Map<Long, FundingDecision> applicationIdToFundingDecision = applicationIdToFundingDecisionFromRequest(request);
		applicationFundingDecisionService.makeApplicationFundingDecision(competitionId, applicationIdToFundingDecision);

		return "redirect:/competition/" + competitionId;
    }

	private Map<Long, FundingDecision> applicationIdToFundingDecisionFromRequest(HttpServletRequest request) {
		return request.getParameterMap().entrySet().stream()
				.filter(this::keyIsNumeric)
				.collect(Collectors.toMap(
		            e -> Long.parseLong(e.getKey()),
		            e -> fundingDecisionForString(e.getValue())
		        ));
	}
	
	private boolean keyIsNumeric(Entry<String, String[]> entry) {
		boolean numeric;
		try {
			Long.parseLong(entry.getKey());
			numeric = true;
		} catch(NumberFormatException e) {
			numeric = false;
		}
		return numeric;
	}
	
	private FundingDecision fundingDecisionForString(String[] val) {
		FundingDecision decision;
		if (val.length > 0 && "Y".equals(val[0])) {
			decision = FundingDecision.FUNDED;
		} else {
			decision = FundingDecision.NOT_FUNDED;
		}
		return decision;
	}
}
