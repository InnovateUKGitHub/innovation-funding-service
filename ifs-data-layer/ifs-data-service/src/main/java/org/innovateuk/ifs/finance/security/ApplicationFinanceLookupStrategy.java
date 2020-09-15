package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@PermissionEntityLookupStrategies
public class ApplicationFinanceLookupStrategy {
    @Autowired
    ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    ApplicationFinanceMapper applicationMapper;

    @PermissionEntityLookupStrategy
    public ApplicationFinanceResource getApplicationFinance(final ApplicationFinanceResourceId id) {
        Optional<ApplicationFinance> applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(id.getApplicationId(), id.getOrganisationId());

        if (applicationFinance.isPresent()) {
            return applicationMapper.mapToResource(applicationFinance.get());
        }

        ApplicationFinanceResource applicationFinanceResource = new ApplicationFinanceResource();
        applicationFinanceResource.setApplication(id.getApplicationId());
        applicationFinanceResource.setOrganisation(id.getOrganisationId());
        return applicationFinanceResource;
    }


    @PermissionEntityLookupStrategy
    public ApplicationFinanceResource getApplicationFinance(final Long id) {
        return applicationMapper.mapToResource(applicationFinanceRepository.findById(id).orElse(null));
    }
}
