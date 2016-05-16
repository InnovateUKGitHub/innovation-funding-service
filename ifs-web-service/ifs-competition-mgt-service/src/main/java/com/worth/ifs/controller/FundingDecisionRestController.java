package com.worth.ifs.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.application.service.ApplicationFundingDecisionService;
import com.worth.ifs.util.MapFunctions;

/**
 * This controller handles restful calls from javascript to autosave funding decisions.
 */
@RestController
public class FundingDecisionRestController {

	@Autowired
	private ApplicationFundingDecisionService applicationFundingDecisionService;
	
	@RequestMapping(method = RequestMethod.POST, value = "/funding/{competitionId}")
	public Map<String, Object> saveFundingDecision(@PathVariable("competitionId") Long competitionId, @RequestParam("applicationId") Long applicationId, @RequestParam("fundingDecision") String decision) {
		
		Map<Long, FundingDecision> applicationIdToFundingDecision = MapFunctions.asMap(applicationId, applicationFundingDecisionService.fundingDecisionForString(decision));
		
		applicationFundingDecisionService.saveApplicationFundingDecisionData(competitionId, applicationIdToFundingDecision);
		
		return MapFunctions.asMap("success", "true");
    }

}
