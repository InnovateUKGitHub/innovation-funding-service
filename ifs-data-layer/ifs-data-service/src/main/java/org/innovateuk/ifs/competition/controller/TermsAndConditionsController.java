package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.VersionedTemplateResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller concerned with handling {@link VersionedTemplate}s.
 * <p>
 * Typically these will be received as {@link VersionedTemplateResource}s
 */
@RestController
@RequestMapping("/terms-and-conditions")
public class TermsAndConditionsController {

    @Autowired
    private TermsAndConditionsService termsAndConditionsService;

    @GetMapping("/get-latest")
    public RestResult<List<GrantTermsAndConditionsResource>> getLatestVersionsForAllTermsAndConditions() {
        return termsAndConditionsService.getLatestVersionsForAllTermsAndConditions().toGetResponse();
    }

    @GetMapping("get-by-id/{id}")
    public RestResult<GrantTermsAndConditionsResource> getById(@PathVariable("id") final Long id) {
        return termsAndConditionsService.getById(id).toGetResponse();
    }

    @GetMapping("/site")
    public RestResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions() {
        return termsAndConditionsService.getLatestSiteTermsAndConditions().toGetResponse();
    }

}
