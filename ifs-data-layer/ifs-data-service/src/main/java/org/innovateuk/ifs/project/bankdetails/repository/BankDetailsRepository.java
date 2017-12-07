package org.innovateuk.ifs.project.bankdetails.repository;

import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BankDetailsRepository extends PagingAndSortingRepository<BankDetails, Long> {

    //TODO - This query will need to be modified once IFS-468 is completed. IFS-468 is about having a workflow in place for the Bank Details process.
    String PENDING_BANK_DETAILS_APPROVALS_QUERY = " SELECT NEW org.innovateuk.ifs.competition.resource.BankDetailsReviewResource("
            + " p.application.id, c.id, c.name, p.id, p.name, bd.organisation.id, bd.organisation.name)"
            + " FROM Competition c, Project p, BankDetails bd"
            + " WHERE p.application.competition.id = c.id"
            + " AND bd.project.id = p.id"
            + " AND bd.manualApproval = FALSE";

    BankDetails findByProjectIdAndOrganisationId(Long projectId, Long organisationId);
    List<BankDetails> findByProjectId(Long projectId);
    List<BankDetails> findByProjectApplicationCompetitionId(Long competitionId);

    @Query(PENDING_BANK_DETAILS_APPROVALS_QUERY)
    List<BankDetailsReviewResource> getPendingBankDetailsApprovals();
}
