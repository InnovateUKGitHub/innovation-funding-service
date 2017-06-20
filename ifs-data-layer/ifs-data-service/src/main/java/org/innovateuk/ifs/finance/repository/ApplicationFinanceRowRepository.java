package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationFinanceRowRepository extends FinanceRowRepository<ApplicationFinanceRow>, PagingAndSortingRepository<ApplicationFinanceRow, Long> {
    List<ApplicationFinanceRow> findByTargetId(Long targetId);
    ApplicationFinanceRow findOneByTargetIdAndNameAndQuestionId(Long targetId, String name, Long questionId);
    List<ApplicationFinanceRow> findByTargetIdAndNameAndQuestionId(Long targetId, String name, Long questionId);
    List<ApplicationFinanceRow> findByTargetIdAndQuestionId(Long targetId, Long questionId);
}
