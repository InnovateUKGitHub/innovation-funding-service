package com.worth.ifs.repository;

import com.worth.ifs.domain.Cost;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "cost", path = "cost")
public interface CostRepository extends PagingAndSortingRepository<Cost, Long> {
}
