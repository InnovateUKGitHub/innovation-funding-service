package com.worth.ifs.project.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.finance.transactional.ProjectFinanceService;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * ProjectFinanceController exposes Project finance data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectFinanceController {

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @RequestMapping(value = "/{projectId}/spend-profile/generate", method = POST)
    public RestResult<Void> generateSpendProfile(@PathVariable("projectId") final Long projectId) {
        return projectFinanceService.generateSpendProfile(projectId).toPostCreateResponse();
    }

    @RequestMapping("/{projectId}/partner-organisation/{organisationId}/spend-profile-table")
    public RestResult<SpendProfileTableResource> getSpendProfileTable(@PathVariable("projectId") final Long projectId,
                                                                 @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        return projectFinanceService.getSpendProfileTable(projectOrganisationCompositeId).toGetResponse();
    }

    @RequestMapping("/{projectId}/partner-organisation/{organisationId}/spend-profile")
    public RestResult<SpendProfileResource> getSpendProfile(@PathVariable("projectId") final Long projectId,
                                                            @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return projectFinanceService.getSpendProfile(projectOrganisationCompositeId).toGetResponse();
    }
}
