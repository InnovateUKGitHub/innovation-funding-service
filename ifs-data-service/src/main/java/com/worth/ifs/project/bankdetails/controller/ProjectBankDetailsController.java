package com.worth.ifs.project.bankdetails.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import com.worth.ifs.project.bankdetails.transactional.BankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/project/{projectId}/bank-details")
public class ProjectBankDetailsController {
    @Autowired
    private BankDetailsService bankDetailsService;

    @RequestMapping(method = PUT)
    public RestResult<Void> submitBanksDetail(@PathVariable("projectId") final Long projectId, @RequestBody @Valid final BankDetailsResource bankDetailsResource) {
        return bankDetailsService.submitBankDetails(bankDetailsResource).toPutResponse();
    }

    @RequestMapping(method = POST)
    public RestResult<Void> updateBanksDetail(@PathVariable("projectId") final Long projectId, @RequestBody @Valid final BankDetailsResource bankDetailsResource) {
        return bankDetailsService.updateBankDetails(bankDetailsResource).toPostResponse();
    }

    @RequestMapping(method = GET, params = "bankDetailsId")
    public RestResult<BankDetailsResource> getBankDetails(@PathVariable("projectId") final Long projectId, @RequestParam("bankDetailsId") final Long bankDetailsId) {
        return bankDetailsService.getById(bankDetailsId).toGetResponse();
    }

    @RequestMapping(method = GET, params = "organisationId")
    public RestResult<BankDetailsResource> getBankDetailsByOrganisationId(@PathVariable("projectId") final Long projectId,
                                                                          @RequestParam("organisationId") final Long organisationId){
        return bankDetailsService.getByProjectAndOrganisation(projectId, organisationId).toGetResponse();
    }

    @RequestMapping(method = GET, value = "/status-summary")
    public RestResult<ProjectBankDetailsStatusSummary> getBankDetailsProjectSummary(@PathVariable("projectId") final Long projectId) {
        return bankDetailsService.getProjectBankDetailsStatusSummary(projectId).toGetResponse();
    }
}
