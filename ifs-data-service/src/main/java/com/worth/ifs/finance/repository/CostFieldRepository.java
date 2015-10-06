package com.worth.ifs.finance.repository;

import com.worth.ifs.finance.domain.CostField;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CostFieldRepository extends PagingAndSortingRepository<CostField, Long> {
    List<CostField> findAll();
}
