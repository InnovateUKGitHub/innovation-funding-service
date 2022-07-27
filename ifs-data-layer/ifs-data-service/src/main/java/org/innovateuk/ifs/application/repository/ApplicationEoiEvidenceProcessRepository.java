package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationEoiEvidenceProcess;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApplicationEoiEvidenceProcessRepository extends ProcessRepository<ApplicationEoiEvidenceProcess>, PagingAndSortingRepository<ApplicationEoiEvidenceProcess, Long>  {
}
