package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public RestResult<List<TermsAndConditionsResource>> getLatestVersionsForAllTermsAndConditions() {
        return termsAndConditionsService.getLatestVersionsForAllTermsAndConditions().toGetResponse();
    }

    @GetMapping("/getById/{id}")
    public RestResult<TermsAndConditionsResource> getById( @PathVariable("id") final Long id){
        return termsAndConditionsService.getById(id).toGetResponse();
    }
}