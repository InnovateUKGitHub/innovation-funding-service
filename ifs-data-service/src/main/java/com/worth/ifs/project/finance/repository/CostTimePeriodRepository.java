package com.worth.ifs.project.finance.repository;

import com.worth.ifs.project.finance.domain.CostTimePeriod;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CostTimePeriodRepository extends PagingAndSortingRepository<CostTimePeriod, Long> {
}
