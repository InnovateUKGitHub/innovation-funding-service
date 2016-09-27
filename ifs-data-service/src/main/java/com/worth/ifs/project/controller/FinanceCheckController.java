package com.worth.ifs.project.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.finance.domain.FinanceCheck;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.transactional.FinanceCheckService;
import com.worth.ifs.project.finance.transactional.ProjectFinanceService;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * FinanceCheckController exposes {@link com.worth.ifs.project.finance.domain.FinanceCheck} data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class FinanceCheckController {

    @Autowired
    private FinanceCheckService financeCheckService;

    @RequestMapping("/{projectId}/partner-organisation/{organisationId}/finance-check")
    public RestResult<FinanceCheckResource> getFinanceCheck(@PathVariable("projectId") final Long projectId,
                                                            @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.getByProjectAndOrganisation(projectOrganisationCompositeId).toGetResponse();
    }

    @RequestMapping(value = "/finance-check", method = POST)
    public RestResult<FinanceCheckResource> updateSpendProfile(@RequestBody FinanceCheckResource financeCheckResource) {
        return financeCheckService.save(financeCheckResource).toPostCreateResponse();
    }
}
