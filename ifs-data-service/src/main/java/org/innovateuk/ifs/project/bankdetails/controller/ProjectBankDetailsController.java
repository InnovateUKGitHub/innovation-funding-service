package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/project/{projectId}/bank-details")
public class ProjectBankDetailsController {
    @Autowired
    private BankDetailsService bankDetailsService;

    @PutMapping
    public RestResult<Void> submitBanksDetail(@PathVariable("projectId") final Long projectId, @RequestBody @Valid final BankDetailsResource bankDetailsResource) {
        return bankDetailsService.submitBankDetails(bankDetailsResource).toPutResponse();
    }

    @PostMapping
    public RestResult<Void> updateBanksDetail(@PathVariable("projectId") final Long projectId, @RequestBody @Valid final BankDetailsResource bankDetailsResource) {
        return bankDetailsService.updateBankDetails(bankDetailsResource).toPostResponse();
    }

    @GetMapping(params = "bankDetailsId")
    public RestResult<BankDetailsResource> getBankDetails(@PathVariable("projectId") final Long projectId, @RequestParam("bankDetailsId") final Long bankDetailsId) {
        return bankDetailsService.getById(bankDetailsId).toGetResponse();
    }

    @GetMapping(params = "organisationId")
    public RestResult<BankDetailsResource> getBankDetailsByOrganisationId(@PathVariable("projectId") final Long projectId,
                                                                          @RequestParam("organisationId") final Long organisationId){
        return bankDetailsService.getByProjectAndOrganisation(projectId, organisationId).toGetResponse();
    }

    @GetMapping("/status-summary")
    public RestResult<ProjectBankDetailsStatusSummary> getBankDetailsProjectSummary(@PathVariable("projectId") final Long projectId) {
        return bankDetailsService.getProjectBankDetailsStatusSummary(projectId).toGetResponse();
    }
}
