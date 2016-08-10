package com.worth.ifs.finance.repository;

import com.worth.ifs.finance.domain.FinanceRow;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FinanceRowRepository extends PagingAndSortingRepository<FinanceRow, Long> {
    public List<FinanceRow> findByApplicationFinanceId(@Param("applicationFinanceId") Long applicationFinanceId);
    public FinanceRow findOneByApplicationFinanceIdAndNameAndQuestionId(Long applicationFinanceId, String name, Long questionId);
    public List<FinanceRow> findByApplicationFinanceIdAndNameAndQuestionId(Long applicationFinanceId, String name, Long questionId);
    public List<FinanceRow> findByApplicationFinanceIdAndQuestionId(Long applicationFinanceId, Long questionId);
}
