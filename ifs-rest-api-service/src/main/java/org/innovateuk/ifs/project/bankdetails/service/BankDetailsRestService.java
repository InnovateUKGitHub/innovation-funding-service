package org.innovateuk.ifs.project.bankdetails.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface BankDetailsRestService {
    RestResult<BankDetailsResource> getByProjectIdAndBankDetailsId(final Long projectId, final Long bankDetailsId);
    RestResult<Void> submitBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource);
    RestResult<Void> updateBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource);
    RestResult<BankDetailsResource> getBankDetailsByProjectAndOrganisation(final Long projectId, final Long organisationId);
    RestResult<ProjectBankDetailsStatusSummary> getBankDetailsStatusSummaryByProject(final Long projectId);
    RestResult<ByteArrayResource> downloadByCompetition(Long competitionId);
    RestResult<List<BankDetailsReviewResource>> getPendingBankDetailsApprovals();
    RestResult<Long> countPendingBankDetailsApprovals();
}
