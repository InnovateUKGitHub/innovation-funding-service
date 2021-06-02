package org.innovateuk.ifs.project.core.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.core.transactional.PartnerOrganisationService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId.id;

/**
 * This controller handles calls for partner organisations
 */
@RestController
@RequestMapping("/project/{projectId}")
public class PartnerOrganisationController {
    @Autowired
    private PartnerOrganisationService partnerOrganisationService;

    @GetMapping("/partner-organisation")
    public RestResult<List<PartnerOrganisationResource>> getFinanceCheck(@PathVariable final long projectId) {
        return partnerOrganisationService.getProjectPartnerOrganisations(projectId).toGetResponse();
    }

    @GetMapping("/partner/{organisationId}")
    public RestResult<PartnerOrganisationResource> getPartnerOrganisation(@PathVariable long projectId,
                                                                          @PathVariable long organisationId) {
        return partnerOrganisationService.getPartnerOrganisation(projectId, organisationId).toGetResponse();
    }

    @PostMapping("/remove-organisation/{organisationId}")
    public RestResult<Void> removeOrganisation(@PathVariable long projectId,
                                               @PathVariable long organisationId) {
        return partnerOrganisationService.removePartnerOrganisation(id(projectId, organisationId)).toPostResponse();
    }
}
