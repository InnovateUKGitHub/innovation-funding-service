package com.worth.ifs.bankdetails.transactional;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

public interface BankDetailsService {
    @NotSecured("TODO")
    ServiceResult<BankDetailsResource> getById(final Long id);

    @NotSecured("TODO")
    ServiceResult<Void> updateBankDetails(final BankDetailsResource bankDetailsResource);
}
