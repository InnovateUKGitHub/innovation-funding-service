package org.innovateuk.ifs.project.financechecks.repository;

import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CostCategoryTypeRepository extends PagingAndSortingRepository<CostCategoryType, Long> {

    @Override
    List<CostCategoryType> findAll();

    CostCategoryType findByCostCategoryGroupId(Long costCategoryGroupId);


}
