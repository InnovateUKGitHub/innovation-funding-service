package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ProjectFinanceController exposes Project finance data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectFinanceController {

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @GetMapping("/{projectId}/project-finances")
    public RestResult<List<ProjectFinanceResource>> getProjectFinances(@PathVariable("projectId") final Long projectId) {
        return financeCheckService.getProjectFinances(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/organisation/{organisationId}/finance-details")
    public RestResult<ProjectFinanceResource> financeDetails(@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") final Long organisationId) {
        return projectFinanceService.financeChecksDetails(projectId, organisationId).toGetResponse();
    }

    @GetMapping("/{projectId}/finance/has-organisation-size-changed")
    public RestResult<Boolean> hasAnyProjectOrganisationSizeChangedFromApplication(@PathVariable long projectId) {
        return projectFinanceService.hasAnyProjectOrganisationSizeChangedFromApplication(projectId).toGetResponse();
    }
}