package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProjectFinanceRowRepository extends FinanceRowRepository<ProjectFinanceRow>, PagingAndSortingRepository<ProjectFinanceRow, Long> {
    List<ProjectFinanceRow> findByTargetId(Long targetId);
    ProjectFinanceRow findOneByTargetIdAndNameAndQuestionId(Long targetId, String name, Long questionId);
    List<ProjectFinanceRow> findByTargetIdAndNameAndQuestionId(Long targetId, String name, Long questionId);
    List<ProjectFinanceRow> findByTargetIdAndQuestionId(Long targetId, Long questionId);
    Optional<ProjectFinanceRow> findOneByApplicationRowId(Long applicationRowId);
}
