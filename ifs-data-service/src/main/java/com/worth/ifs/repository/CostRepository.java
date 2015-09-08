package com.worth.ifs.repository;

import com.worth.ifs.domain.Cost;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "cost", path = "cost")
public interface CostRepository extends PagingAndSortingRepository<Cost, Long> {
    public List<Cost> findByApplicationFinanceId(@Param("applicationFinanceId") Long applicationFinanceId);
    Cost findById(@Param("id") Long id);
}
