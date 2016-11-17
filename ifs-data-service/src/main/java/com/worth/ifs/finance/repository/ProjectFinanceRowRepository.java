package com.worth.ifs.finance.repository;

import com.worth.ifs.finance.domain.ProjectFinanceRow;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProjectFinanceRowRepository extends FinanceRowRepository<ProjectFinanceRow>, PagingAndSortingRepository<ProjectFinanceRow, Long> {
    List<ProjectFinanceRow> findByTargetId(@Param("targetId") Long targetId);
    ProjectFinanceRow findOneByTargetIdAndNameAndQuestionId(Long targetId, String name, Long questionId);
    List<ProjectFinanceRow> findByTargetIdAndNameAndQuestionId(Long targetId, String name, Long questionId);
    List<ProjectFinanceRow> findByTargetIdAndQuestionId(Long targetId, Long questionId);
}
