package com.worth.ifs.project.finance.repository;

import com.worth.ifs.project.finance.domain.CostCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository to help with testing
 */
public interface CostCategoryRepository extends PagingAndSortingRepository<CostCategory, Long> {
}
