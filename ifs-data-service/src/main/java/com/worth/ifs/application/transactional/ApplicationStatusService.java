package com.worth.ifs.application.transactional;

import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.service.ServiceResult;

import org.springframework.security.access.prepost.PreAuthorize;

public interface ApplicationStatusService {

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.application.resource.ApplicationStatus', 'READ')" )
    ServiceResult<ApplicationStatusResource> getById(Long id);
}
