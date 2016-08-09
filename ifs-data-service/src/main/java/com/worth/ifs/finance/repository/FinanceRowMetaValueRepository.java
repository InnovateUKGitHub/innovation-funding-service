package com.worth.ifs.finance.repository;

import com.worth.ifs.finance.domain.FinanceRowMetaValue;
import com.worth.ifs.finance.resource.FinanceRowMetaValueId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FinanceRowMetaValueRepository extends PagingAndSortingRepository<FinanceRowMetaValue, FinanceRowMetaValueId> {
	@Override
    List<FinanceRowMetaValue> findAll();
    void deleteByFinanceRowId(@Param("financeRowId") Long financeRowId);
}
