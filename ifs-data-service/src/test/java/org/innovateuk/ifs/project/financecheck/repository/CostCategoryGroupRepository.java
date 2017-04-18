package org.innovateuk.ifs.project.financecheck.repository;

import org.innovateuk.ifs.project.spendprofile.domain.CostCategoryGroup;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository to help with testing
 */
public interface CostCategoryGroupRepository extends PagingAndSortingRepository<CostCategoryGroup, Long> {
}
