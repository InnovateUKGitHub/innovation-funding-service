package com.worth.ifs.bankdetails.controller;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.transactional.BankDetailsService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project/{projectId}/bank-details")
public class BankDetailsController {
    @Autowired
    BankDetailsService bankDetailsService;

    @RequestMapping(method = RequestMethod.POST)
    public RestResult<Void> updateBanksDetail(@PathVariable("projectId") final Long projectId,
                                              @RequestBody BankDetailsResource bankDetailsResource){
        return bankDetailsService.updateBankDetails(bankDetailsResource).toPostResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public RestResult<BankDetailsResource> getBankDetails(@PathVariable("projectId") final Long projectId,
                                                          @PathVariable("bankDetailsId") final Long bankDetailsId){
        return bankDetailsService.getById(bankDetailsId).toGetResponse();
    }

    @RequestMapping(method = RequestMethod.GET, params = "organisationId")
    public RestResult<BankDetailsResource> getBankDetailsByOrganisationId(@PathVariable("projectId") final Long projectId,
                                                          @RequestParam("organisationId") final Long organisationId){
        return bankDetailsService.getByProjectAndOrganisation(projectId, organisationId).toGetResponse();
    }
}
