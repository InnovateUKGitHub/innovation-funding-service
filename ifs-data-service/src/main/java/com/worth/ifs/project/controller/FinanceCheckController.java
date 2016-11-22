package com.worth.ifs.project.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.finance.resource.FinanceCheckURIs;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;
import com.worth.ifs.project.finance.transactional.FinanceCheckService;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
/**
 * FinanceCheckController exposes {@link com.worth.ifs.project.finance.domain.FinanceCheck} data and operations through a REST API.
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

    @RequestMapping(value = FinanceCheckURIs.PATH, method = POST)
    public RestResult<Void> updateFinanceCheck(@RequestBody FinanceCheckResource financeCheckResource) {
        return financeCheckService.save(financeCheckResource).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH + "/approve", method = POST)
    public RestResult<Void> approveFinanceCheck(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId) {
        return financeCheckService.approve(projectId, organisationId).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH + "/status", method = GET)
    public RestResult<FinanceCheckProcessResource> getFinanceCheckApprovalStatus(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId) {
        return financeCheckService.getFinanceCheckApprovalStatus(projectId, organisationId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}" + FinanceCheckURIs.PATH)
    public RestResult<FinanceCheckSummaryResource> getFinanceCheckSummary(@PathVariable("projectId") Long projectId){
        return financeCheckService.getFinanceCheckSummary(projectId).toGetResponse();
    }
}
