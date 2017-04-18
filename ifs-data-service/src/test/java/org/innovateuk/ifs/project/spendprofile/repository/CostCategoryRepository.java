package org.innovateuk.ifs.project.spendprofile.repository;

import org.innovateuk.ifs.project.spendprofile.domain.CostCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository to help with testing
 */
public interface CostCategoryRepository extends PagingAndSortingRepository<CostCategory, Long> {
}
