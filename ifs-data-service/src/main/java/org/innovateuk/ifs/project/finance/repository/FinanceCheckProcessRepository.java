package org.innovateuk.ifs.project.finance.repository;

import org.innovateuk.ifs.project.finance.domain.FinanceCheckProcess;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for the Finance Check subclass of Process
 */
public interface FinanceCheckProcessRepository extends ProcessRepository<FinanceCheckProcess>, PagingAndSortingRepository<FinanceCheckProcess, Long> {
}
