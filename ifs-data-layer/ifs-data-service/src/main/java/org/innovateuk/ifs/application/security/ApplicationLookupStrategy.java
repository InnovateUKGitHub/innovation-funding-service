package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ApplicationLookupStrategy {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @PermissionEntityLookupStrategy
    public Application getApplication(Long applicationId) {
        return applicationRepository.findOne(applicationId);
    }

    @PermissionEntityLookupStrategy
    public ApplicationResource getApplicationResource(Long applicationId) {
        return applicationMapper.mapToResource(applicationRepository.findOne(applicationId));
    }
}
