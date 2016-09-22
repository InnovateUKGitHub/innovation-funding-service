package com.worth.ifs.bankdetails.service;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.resource.ProjectBankDetailsStatusSummary;
import com.worth.ifs.commons.rest.RestResult;

public interface BankDetailsRestService {
    RestResult<BankDetailsResource> getByProjectIdAndBankDetailsId(final Long projectId, final Long bankDetailsId);
    RestResult<Void> submitBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource);
    RestResult<Void> updateBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource);
    RestResult<BankDetailsResource> getBankDetailsByProjectAndOrganisation(final Long projectId, final Long organisationId);
    RestResult<ProjectBankDetailsStatusSummary> getBankDetailsStatusSummaryByProject(final Long projectId);
}
