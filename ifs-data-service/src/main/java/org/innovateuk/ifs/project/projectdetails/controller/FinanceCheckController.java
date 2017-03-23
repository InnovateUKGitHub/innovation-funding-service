package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.finance.transactional.FinanceCheckService;
import org.innovateuk.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * FinanceCheckController exposes {@link org.innovateuk.ifs.project.finance.domain.FinanceCheck} data and operations through a REST API.
 */
@RestController
@RequestMapping(FinanceCheckURIs.BASE_URL)
public class FinanceCheckController {

    @Autowired
    private FinanceCheckService financeCheckService;

    @RequestMapping("/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH)
    public RestResult<FinanceCheckResource> getFinanceCheck(@PathVariable("projectId") final Long projectId,
                                                            @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.getByProjectAndOrganisation(projectOrganisationCompositeId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH + "/status", method = GET)
    public RestResult<FinanceCheckProcessResource> getFinanceCheckApprovalStatus(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId) {
        return financeCheckService.getFinanceCheckApprovalStatus(projectId, organisationId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}" + FinanceCheckURIs.PATH, method = GET)
    public RestResult<FinanceCheckSummaryResource> getFinanceCheckSummary(@PathVariable("projectId") Long projectId){
        return financeCheckService.getFinanceCheckSummary(projectId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}" + FinanceCheckURIs.PATH + "/overview", method = GET)
    public RestResult<FinanceCheckOverviewResource> getFinanceCheckOverview(@PathVariable("projectId") Long projectId){
        return financeCheckService.getFinanceCheckOverview(projectId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH + "/eligibility", method = GET)
    public RestResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId){
        return financeCheckService.getFinanceCheckEligibilityDetails(projectId, organisationId).toGetResponse();
    }

}
