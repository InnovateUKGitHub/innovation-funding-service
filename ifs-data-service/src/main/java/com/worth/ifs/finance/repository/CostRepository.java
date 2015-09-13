package com.worth.ifs.finance.repository;

import com.worth.ifs.finance.domain.Cost;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CostRepository extends PagingAndSortingRepository<Cost, Long> {
    public List<Cost> findByApplicationFinanceId(@Param("applicationFinanceId") Long applicationFinanceId);
    Cost findById(@Param("id") Long id);
}
