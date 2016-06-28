package com.worth.ifs.bankdetails.controller;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.transactional.BankDetailsService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/project/{projectId}/bankdetails")
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
}
