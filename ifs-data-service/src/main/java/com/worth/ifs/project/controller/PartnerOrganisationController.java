package com.worth.ifs.project.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import com.worth.ifs.project.transactional.PartnerOrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project/{projectId}")
public class PartnerOrganisationController {
    @Autowired
    private PartnerOrganisationService partnerOrganisationService;

    @RequestMapping("/partner-organisation")
    public RestResult<List<PartnerOrganisationResource>> getFinanceCheck(@PathVariable("projectId") final Long projectId) {
        return partnerOrganisationService.getProjectPartnerOrganisations(projectId).toGetResponse();
    }
}
