package com.worth.ifs.application.security;

import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.mapper.ApplicationStatusMapper;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
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
