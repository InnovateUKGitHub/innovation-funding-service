package org.innovateuk.ifs.project.bankdetails.repository;

import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BankDetailsRepository extends PagingAndSortingRepository<BankDetails, Long> {
    BankDetails findByProjectIdAndOrganisationId(Long projectId, Long organisationId);
    List<BankDetails> findByProjectId(Long projectId);
    List<BankDetails> findByProjectApplicationCompetitionId(Long competitionId);
}
