package org.innovateuk.ifs.project.finance.repository;

import org.innovateuk.ifs.project.finance.domain.CostCategoryGroup;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository to help with testing
 */
public interface CostCategoryGroupRepository extends PagingAndSortingRepository<CostCategoryGroup, Long> {
}
