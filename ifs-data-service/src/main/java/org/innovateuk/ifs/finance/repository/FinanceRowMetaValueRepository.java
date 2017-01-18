package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FinanceRowMetaValueRepository extends PagingAndSortingRepository<FinanceRowMetaValue, Long> {
    @Override
    List<FinanceRowMetaValue> findAll();
    FinanceRowMetaValue financeRowIdAndFinanceRowMetaFieldId(Long financeRowId, Long financeRowMetaFieldId);
    void deleteByFinanceRowId(Long financeRowId);
}
