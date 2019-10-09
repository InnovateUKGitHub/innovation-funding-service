package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.domain.FinanceCheck;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FinanceCheckController exposes {@link FinanceCheck} data and operations through a REST API.
 */
@RestController
@RequestMapping(FinanceCheckURIs.BASE_URL)
public class FinanceCheckController {

    @Autowired
    private FinanceCheckService financeCheckService;

    @GetMapping("/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH)
    public RestResult<FinanceCheckResource> getFinanceCheck(@PathVariable("projectId") final Long projectId,
                                                            @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.getByProjectAndOrganisation(projectOrganisationCompositeId).toGetResponse();
    }

    @GetMapping("/{projectId}" + FinanceCheckURIs.PATH)
    public RestResult<FinanceCheckSummaryResource> getFinanceCheckSummary(@PathVariable("projectId") Long projectId){
        return financeCheckService.getFinanceCheckSummary(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}" + FinanceCheckURIs.PATH + "/overview")
    public RestResult<FinanceCheckOverviewResource> getFinanceCheckOverview(@PathVariable("projectId") Long projectId){
        return financeCheckService.getFinanceCheckOverview(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH + "/eligibility")
    public RestResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId){
        return financeCheckService.getFinanceCheckEligibilityDetails(projectId, organisationId).toGetResponse();
    }
}
