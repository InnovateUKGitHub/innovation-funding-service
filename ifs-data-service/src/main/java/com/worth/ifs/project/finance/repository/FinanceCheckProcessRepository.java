package com.worth.ifs.project.finance.repository;

import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.finance.domain.FinanceCheckProcess;
import com.worth.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for the Finance Check subclass of Process
 */
public interface FinanceCheckProcessRepository extends ProcessRepository<FinanceCheckProcess>, PagingAndSortingRepository<FinanceCheckProcess, Long> {
}
