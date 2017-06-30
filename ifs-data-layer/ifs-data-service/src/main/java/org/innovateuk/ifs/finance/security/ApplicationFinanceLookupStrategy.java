package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
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
