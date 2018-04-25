package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TermsAndConditionsController exposes TermsAndConditions data and operations through a REST API
 */
@RestController
@RequestMapping("/terms-and-conditions")
public class TermsAndConditionsController {

    @Autowired
    TermsAndConditionsService termsAndConditionsService;

    @GetMapping("/getLatest")
    public RestResult<List<TermsAndConditionsResource>> getLatestTermsAndConditions() {
        return termsAndConditionsService.getLatestTermsAndConditions().toGetResponse();
    }

}