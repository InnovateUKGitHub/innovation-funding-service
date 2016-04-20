package com.worth.ifs.finance.security;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class CostLookupStrategy {
    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private CostRepository costRepository;

    @PermissionEntityLookupStrategy
    public Cost getCost(final Long costId) {
        return costRepository.findOne(costId);
    }
}
