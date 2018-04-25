package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller concerned with handling {@link org.innovateuk.ifs.competition.domain.TermsAndConditions}s.
 * <p>
 * Typically these will be received as {@link org.innovateuk.ifs.competition.resource.TermsAndConditionsResource}s
 */
@RestController
@RequestMapping("/terms-and-conditions")
public class TermsAndConditionsController {

    @Autowired
    private TermsAndConditionsService termsAndConditionsService;

    @GetMapping("/site")
    public RestResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions() {
        return termsAndConditionsService.getLatestSiteTermsAndConditions().toGetResponse();
    }

}
