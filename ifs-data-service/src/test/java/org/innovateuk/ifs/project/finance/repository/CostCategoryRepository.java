package org.innovateuk.ifs.project.finance.repository;

import org.innovateuk.ifs.project.finance.domain.CostCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository to help with testing
 */
public interface CostCategoryRepository extends PagingAndSortingRepository<CostCategory, Long> {
}
