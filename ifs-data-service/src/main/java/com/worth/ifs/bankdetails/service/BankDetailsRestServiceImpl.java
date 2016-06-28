package com.worth.ifs.bankdetails.service;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;

public class BankDetailsRestServiceImpl extends BaseRestService implements BankDetailsRestService {

    private String bankDetailsRestUrl = "/bankdetails";

    public RestResult<BankDetailsResource> getById(final Long id){
        return getWithRestResult(bankDetailsRestUrl + "/" + id, BankDetailsResource.class);
    }

    public RestResult<Void> updateBankDetails(final BankDetailsResource bankDetailsResource){
        return postWithRestResult(bankDetailsRestUrl, bankDetailsResource, Void.class);
    }
}
