package org.innovateuk.ifs.project.financechecks.repository;

import org.innovateuk.ifs.project.financechecks.domain.FinanceCheck;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface FinanceCheckRepository extends PagingAndSortingRepository<FinanceCheck, Long> {
    FinanceCheck findByProjectIdAndOrganisationId(Long projectId, Long organisationId);
    List<FinanceCheck> findByProjectId(Long projectId);
}
