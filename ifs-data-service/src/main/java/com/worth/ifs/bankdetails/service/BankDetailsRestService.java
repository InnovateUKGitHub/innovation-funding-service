package com.worth.ifs.bankdetails.service;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.security.NotSecured;

public interface BankDetailsRestService {
    @NotSecured("TODO")
    RestResult<BankDetailsResource> getById(final Long id);

    @NotSecured("TODO")
    RestResult<Void> updateBankDetails(final BankDetailsResource bankDetailsResource);
}
