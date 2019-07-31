package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FinanceRowRepository<T> {
    List<T> findByTargetId(Long targetId);
    List<T> findByTargetIdAndType(Long targetId, FinanceRowType type);
}
