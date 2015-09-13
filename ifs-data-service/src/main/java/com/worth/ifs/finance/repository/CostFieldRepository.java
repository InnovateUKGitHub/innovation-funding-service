package com.worth.ifs.finance.repository;

import com.worth.ifs.finance.domain.CostField;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CostFieldRepository extends PagingAndSortingRepository<CostField, Long> {
    List<CostField> findAll();
}
