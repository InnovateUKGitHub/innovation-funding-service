package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.CostTotal;
import org.springframework.data.repository.CrudRepository;

public interface CostTotalRepository extends CrudRepository<CostTotal, Long> {

    CostTotal findByFinanceId(Long financeId);
}
