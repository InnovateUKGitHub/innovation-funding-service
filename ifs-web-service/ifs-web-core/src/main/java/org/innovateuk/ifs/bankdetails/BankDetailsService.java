package org.innovateuk.ifs.bankdetails;

import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.core.io.ByteArrayResource;

/**
 * A service for dealing with project bank details via the appropriate Rest services
 */
public interface BankDetailsService {
    BankDetailsResource getByProjectIdAndBankDetailsId(final Long projectId, final Long bankDetailsId);
    ServiceResult<Void> submitBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource);
    ServiceResult<Void> updateBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource);
    BankDetailsResource getBankDetailsByProjectAndOrganisation(final Long projectId, final Long organisationId);
    ProjectBankDetailsStatusSummary getBankDetailsByProject(final Long projectId);
    ByteArrayResource downloadByCompetition(Long competitionId);
}
