package com.worth.ifs.project.finance.repository;

import com.worth.ifs.project.finance.domain.FinanceCheck;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FinanceCheckRepository extends PagingAndSortingRepository<FinanceCheck, Long> {
}
