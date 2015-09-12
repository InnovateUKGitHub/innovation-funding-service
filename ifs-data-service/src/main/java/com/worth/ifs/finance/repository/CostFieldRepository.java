package com.worth.ifs.finance.repository;

import com.worth.ifs.finance.domain.CostField;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "costField", path = "costfield")
public interface CostFieldRepository extends PagingAndSortingRepository<CostField, Long> {
    List<CostField> findAll();
}
