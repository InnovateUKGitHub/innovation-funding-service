package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProjectFinanceRepository extends PagingAndSortingRepository<ProjectFinance, Long> {
    ProjectFinance findByProjectIdAndOrganisationId(Long projectId, Long organisationId);
    List<ProjectFinance> findByProjectId(Long projectId);
}
