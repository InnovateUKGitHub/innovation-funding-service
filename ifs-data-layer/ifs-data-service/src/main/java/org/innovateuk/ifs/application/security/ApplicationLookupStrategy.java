package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Series of rules to look up an {@link Application} or {@link ApplicationResource} from a {@link Long} application id.
 * These can then be feed into methods marked with the {@link org.innovateuk.ifs.commons.security.PermissionRule}
 * annotation as part of the Spring security mechanism.
 */
@Component
@PermissionEntityLookupStrategies
public class ApplicationLookupStrategy {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @PermissionEntityLookupStrategy
    public Application getApplication(Long applicationId) {
        return applicationRepository.findById(applicationId).orElse(null);
    }

    @PermissionEntityLookupStrategy
    public ApplicationResource getApplicationResource(Long applicationId) {
        return applicationMapper.mapToResource(applicationRepository.findById(applicationId).orElse(null));
    }
}
