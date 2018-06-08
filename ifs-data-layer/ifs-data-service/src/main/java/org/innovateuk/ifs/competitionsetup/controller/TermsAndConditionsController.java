package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competitionsetup.transactional.TermsAndConditionsService;
import org.innovateuk.ifs.competitionsetup.domain.TermsAndConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller concerned with handling {@link TermsAndConditions}s.
 * <p>
 * Typically these will be received as {@link org.innovateuk.ifs.competition.resource.TermsAndConditionsResource}s
 */
@RestController
@RequestMapping("/terms-and-conditions")
public class TermsAndConditionsController {

    @Autowired
    private TermsAndConditionsService termsAndConditionsService;

    @GetMapping("/getLatest")
    public RestResult<List<GrantTermsAndConditionsResource>> getLatestVersionsForAllTermsAndConditions() {
        return termsAndConditionsService.getLatestVersionsForAllTermsAndConditions().toGetResponse();
    }

    @GetMapping("/getById/{id}")
    public RestResult<GrantTermsAndConditionsResource> getById(@PathVariable("id") final Long id) {
        return termsAndConditionsService.getById(id).toGetResponse();
    }

    @GetMapping("/site")
    public RestResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions() {
        return termsAndConditionsService.getLatestSiteTermsAndConditions().toGetResponse();
    }

}
