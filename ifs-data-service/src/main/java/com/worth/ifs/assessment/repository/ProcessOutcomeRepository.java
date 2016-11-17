package com.worth.ifs.assessment.repository;

import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface ProcessOutcomeRepository extends PagingAndSortingRepository<ProcessOutcome, Long> {
    ProcessOutcome findTopByProcessIdOrderByIdAsc(Long processId);

    ProcessOutcome findTopByProcessIdAndOutcomeTypeOrderByIdAsc(Long processId, String outcomeType);
}
