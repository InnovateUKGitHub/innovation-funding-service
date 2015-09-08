package com.worth.ifs.repository;

import com.worth.ifs.domain.CostField;
import com.worth.ifs.domain.CostValue;
import com.worth.ifs.domain.CostValueId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "costValue", path = "costvalue")
public interface CostValueRepository extends PagingAndSortingRepository<CostValue, CostValueId> {
    List<CostValue> findAll();
}
