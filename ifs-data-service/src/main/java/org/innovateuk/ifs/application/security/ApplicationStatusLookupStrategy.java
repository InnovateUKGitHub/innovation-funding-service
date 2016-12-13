package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.domain.ApplicationStatus;
import org.innovateuk.ifs.application.mapper.ApplicationStatusMapper;
import org.innovateuk.ifs.application.repository.ApplicationStatusRepository;
import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ApplicationStatusLookupStrategy {
    @Autowired
    private ApplicationStatusRepository applicationStatusRepository;

    @Autowired
    private ApplicationStatusMapper applicationStatusMapper;

    @PermissionEntityLookupStrategy
    public ApplicationStatus getApplicationStatus(Long id){
        return applicationStatusRepository.findOne(id);
    }

    @PermissionEntityLookupStrategy
    public ApplicationStatusResource getApplicationStatusResource(Long id){
        return applicationStatusMapper.mapToResource(applicationStatusRepository.findOne(id));
    }

}
