package com.worth.ifs.finance.repository;

import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.domain.CostValueId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CostValueRepository extends PagingAndSortingRepository<CostValue, CostValueId> {
    List<CostValue> findAll();
    void deleteByCostId(@Param("costId") Long costId);
}
