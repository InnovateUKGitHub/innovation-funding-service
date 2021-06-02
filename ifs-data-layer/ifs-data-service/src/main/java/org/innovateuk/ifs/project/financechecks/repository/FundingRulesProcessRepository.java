package org.innovateuk.ifs.project.financechecks.repository;

import org.innovateuk.ifs.project.financechecks.domain.FundingRulesProcess;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FundingRulesProcessRepository extends ProcessRepository<FundingRulesProcess>, PagingAndSortingRepository<FundingRulesProcess, Long> {

}

