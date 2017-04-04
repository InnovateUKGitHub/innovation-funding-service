package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.transactional.PartnerOrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This controller handles calls for partner organisations
 */
@RestController
@RequestMapping("/project/{projectId}")
public class PartnerOrganisationController {
    @Autowired
    private PartnerOrganisationService partnerOrganisationService;

    @GetMapping("/partner-organisation")
    public RestResult<List<PartnerOrganisationResource>> getFinanceCheck(@PathVariable("projectId") final Long projectId) {
        return partnerOrganisationService.getProjectPartnerOrganisations(projectId).toGetResponse();
    }
}
