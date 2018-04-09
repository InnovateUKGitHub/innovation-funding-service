package org.innovateuk.ifs.project.bankdetails.repository;

import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import static org.innovateuk.ifs.project.constant.ProjectConstants.EXPERIAN_AUTOMATIC_APPROVAL_THRESHOLD_ADDRESS;
import static org.innovateuk.ifs.project.constant.ProjectConstants.EXPERIAN_AUTOMATIC_APPROVAL_THRESHOLD_COMPANY_NAME;

public interface BankDetailsRepository extends PagingAndSortingRepository<BankDetails, Long> {

    String PENDING_BANK_DETAILS_APPROVALS_FROM_CLAUSE = " FROM Competition c, Project p, BankDetails bd"
            + " WHERE p.application.competition.id = c.id"
            + " AND bd.project.id = p.id"
            + " AND bd.manualApproval = FALSE"
            + " AND (bd.verified = FALSE OR bd.registrationNumberMatched = FALSE"
            + " OR bd.companyNameScore <= " + EXPERIAN_AUTOMATIC_APPROVAL_THRESHOLD_COMPANY_NAME
            + " OR bd.addressScore <= " + EXPERIAN_AUTOMATIC_APPROVAL_THRESHOLD_ADDRESS
            + ")";

    //TODO - This query will need to be modified once IFS-468 is completed. IFS-468 is about having a workflow in place for the Bank Details process.
    String PENDING_BANK_DETAILS_APPROVALS_QUERY = " SELECT NEW org.innovateuk.ifs.competition.resource.BankDetailsReviewResource("
            + " p.application.id, c.id, c.name, p.id, p.name, bd.organisation.id, bd.organisation.name)"
            + PENDING_BANK_DETAILS_APPROVALS_FROM_CLAUSE;

    //TODO - This query will need to be modified once IFS-468 is completed. IFS-468 is about having a workflow in place for the Bank Details process.
    String COUNT_PENDING_BANK_DETAILS_APPROVALS_QUERY = " SELECT COUNT(DISTINCT bd.organisation.id)"
            + PENDING_BANK_DETAILS_APPROVALS_FROM_CLAUSE;


    BankDetails findByProjectIdAndOrganisationId(Long projectId, Long organisationId);
    List<BankDetails> findByProjectId(Long projectId);
    List<BankDetails> findByProjectApplicationCompetitionId(Long competitionId);

    @Query(PENDING_BANK_DETAILS_APPROVALS_QUERY)
    List<BankDetailsReviewResource> getPendingBankDetailsApprovals();

    @Query(COUNT_PENDING_BANK_DETAILS_APPROVALS_QUERY)
    Long countPendingBankDetailsApprovals();
}
