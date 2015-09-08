package com.worth.ifs.repository;

import com.worth.ifs.domain.CostField;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "costField", path = "costfield")
public interface CostFieldRepository extends PagingAndSortingRepository<CostField, Long> {
    List<CostField> findAll();
}
