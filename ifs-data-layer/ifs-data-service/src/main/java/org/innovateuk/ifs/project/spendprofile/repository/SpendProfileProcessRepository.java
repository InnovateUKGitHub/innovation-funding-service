package org.innovateuk.ifs.project.spendprofile.repository;

import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileProcess;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SpendProfileProcessRepository extends ProcessRepository<SpendProfileProcess>, PagingAndSortingRepository<SpendProfileProcess, Long> {
}
