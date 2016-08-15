package com.worth.ifs.project.finance.repository;

import com.worth.ifs.project.finance.domain.CostCategoryGroup;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository to help with testing
 */
public interface CostCategoryGroupRepository extends PagingAndSortingRepository<CostCategoryGroup, Long> {
}
