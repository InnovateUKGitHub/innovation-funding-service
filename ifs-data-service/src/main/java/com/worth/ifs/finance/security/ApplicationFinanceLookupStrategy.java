package com.worth.ifs.finance.security;

import com.worth.ifs.finance.mapper.ApplicationFinanceMapper;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ApplicationFinanceLookupStrategy {
    @Autowired
    ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    ApplicationFinanceMapper applicationMapper;

    @PermissionEntityLookupStrategy
    public ApplicationFinanceResource getApplicationFinance(final ApplicationFinanceResourceId id) {
        final ApplicationFinanceResource applicationFinanceResource = applicationMapper.mapToResource(applicationFinanceRepository.findByApplicationIdAndOrganisationId(id.getApplicationId(), id.getOrganisationId()));
        // If its new then this could be empty so fill in the fields we can
        applicationFinanceResource.setApplication(id.getApplicationId());
        applicationFinanceResource.setOrganisation(id.getOrganisationId());
        return  applicationFinanceResource;
    }


    @PermissionEntityLookupStrategy
    public ApplicationFinanceResource getApplicationFinance(final Long id) {
        return applicationMapper.mapToResource(applicationFinanceRepository.findOne(id));
    }
}
