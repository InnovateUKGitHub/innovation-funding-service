package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.projectteam.transactional.PendingPartnerProgressService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/{projectId}/organisation/{organisationId}/pending-partner-progress")
public class PendingPartnerProgressController {

    @Autowired
    private PendingPartnerProgressService pendingPartnerProgressService;

    @GetMapping
    public RestResult<PendingPartnerProgressResource> getPendingPartnerProgress(@PathVariable long projectId, @PathVariable long organisationId) {
        return pendingPartnerProgressService.getPendingPartnerProgress(projectId, organisationId).toGetResponse();
    }

    @PostMapping("/your-organisation-complete")
    public RestResult<Void> markYourOrganisationComplete(@PathVariable long projectId, @PathVariable long organisationId) {
        return pendingPartnerProgressService.markYourOrganisationComplete(projectId, organisationId).toPostResponse();
    }

    @PostMapping("/your-funding-complete")
    public RestResult<Void> markYourFundingComplete(@PathVariable long projectId, @PathVariable long organisationId) {
        return pendingPartnerProgressService.markYourFundingComplete(projectId, organisationId).toPostResponse();
    }

    @PostMapping("/terms-and-conditions-complete")
    public RestResult<Void> markTermsAndConditionsComplete(@PathVariable long projectId, @PathVariable long organisationId) {
        return pendingPartnerProgressService.markTermsAndConditionsComplete(projectId, organisationId).toPostResponse();
    }

    @PostMapping("/your-organisation-incomplete")
    public RestResult<Void> markYourOrganisationIncomplete(@PathVariable long projectId, @PathVariable long organisationId) {
        return pendingPartnerProgressService.markYourOrganisationIncomplete(projectId, organisationId).toPostResponse();
    }

    @PostMapping("/your-funding-incomplete")
    public RestResult<Void> markYourFundingIncomplete(@PathVariable long projectId, @PathVariable long organisationId) {
        return pendingPartnerProgressService.markYourFundingIncomplete(projectId, organisationId).toPostResponse();
    }

    @PostMapping("/terms-and-conditions-incomplete")
    public RestResult<Void> markTermsAndConditionsIncomplete(@PathVariable long projectId, @PathVariable long organisationId) {
        return pendingPartnerProgressService.markTermsAndConditionsIncomplete(projectId, organisationId).toPostResponse();
    }

    @PostMapping
    public RestResult<Void> completePartnerSetup(@PathVariable long projectId, @PathVariable long organisationId) {
        return pendingPartnerProgressService.completePartnerSetup(projectId, organisationId).toPostResponse();
    }
}
