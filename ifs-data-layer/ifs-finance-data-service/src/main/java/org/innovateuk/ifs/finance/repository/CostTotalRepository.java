package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.CostTotal;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CostTotalRepository extends CrudRepository<CostTotal, Long> {

    CostTotal findByFinanceId(Long financeId);

    List<CostTotal> findAllByFinanceId(Long financeId);
}
