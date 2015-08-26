package com.worth.ifs.repository;

import com.worth.ifs.domain.CostCategory;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "costCategory", path = "costcategory")
public interface CostCategoryRepository extends PagingAndSortingRepository<CostCategory, Long> {
    List<CostCategory> findByApplicationFinanceId(@Param("applicationFinanceId") Long applicationFinanceId);

}
