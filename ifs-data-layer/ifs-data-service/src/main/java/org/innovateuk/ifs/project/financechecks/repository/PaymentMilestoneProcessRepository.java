package org.innovateuk.ifs.project.financechecks.repository;

import org.innovateuk.ifs.project.financechecks.domain.PaymentMilestoneProcess;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PaymentMilestoneProcessRepository extends ProcessRepository<PaymentMilestoneProcess>, PagingAndSortingRepository<PaymentMilestoneProcess, Long> {
}
