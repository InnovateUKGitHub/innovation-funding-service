package com.worth.ifs.finance.security;

import com.worth.ifs.finance.mapper.ApplicationFinanceMapper;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class CostLookupStrategy {
    @Autowired
    ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    ApplicationFinanceMapper applicationMapper;

    @PermissionEntityLookupStrategy
    public ApplicationFinanceResource getApplicationFinance(final ApplicationFinanceResourceId id) {
        return applicationMapper.mapToResource(applicationFinanceRepository.findByApplicationIdAndOrganisationId(id.getApplicationId(), id.getOrganisationId()));
    }
}
