package com.worth.ifs.bankdetails;

import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import com.worth.ifs.commons.service.ServiceResult;

/**
 * A service for dealing with project bank details via the appropriate Rest services
 */
public interface BankDetailsService {
    BankDetailsResource getByProjectIdAndBankDetailsId(final Long projectId, final Long bankDetailsId);
    ServiceResult<Void> submitBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource);
    ServiceResult<Void> updateBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource);
    BankDetailsResource getBankDetailsByProjectAndOrganisation(final Long projectId, final Long organisationId);
    ProjectBankDetailsStatusSummary getBankDetailsByProject(final Long projectId);
}
