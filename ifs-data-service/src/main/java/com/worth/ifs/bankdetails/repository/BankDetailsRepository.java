package com.worth.ifs.bankdetails.repository;

import com.worth.ifs.bankdetails.domain.BankDetails;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BankDetailsRepository extends PagingAndSortingRepository<BankDetails, Long> {
}
