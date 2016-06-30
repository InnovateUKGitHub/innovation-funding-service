package com.worth.ifs.bankdetails.transactional;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

public interface BankDetailsService {
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<BankDetailsResource> getById(final Long id);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateBankDetails(final BankDetailsResource bankDetailsResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<BankDetailsResource> getByProjectAndOrganisation(final Long projectId, final Long organisationId);
}
