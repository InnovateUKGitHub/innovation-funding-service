package com.worth.ifs.bankdetails.repository;

import com.worth.ifs.bankdetails.domain.BankDetails;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BankDetailsRepository extends PagingAndSortingRepository<BankDetails, Long> {
    BankDetails findByProjectIdAndOrganisationId(Long projectId, Long organisationId);
    List<BankDetails> findByProjectId(Long projectId);
}
