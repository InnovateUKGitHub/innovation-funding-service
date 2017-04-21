package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApplicationProcessRepository extends ProcessRepository<ApplicationProcess>, PagingAndSortingRepository<ApplicationProcess, Long> {
}
