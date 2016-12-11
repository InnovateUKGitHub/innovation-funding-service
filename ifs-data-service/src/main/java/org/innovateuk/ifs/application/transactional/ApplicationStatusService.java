package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

import org.springframework.security.access.prepost.PreAuthorize;

public interface ApplicationStatusService {

    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.application.resource.ApplicationStatusResource', 'READ')" )
    ServiceResult<ApplicationStatusResource> getById(Long id);
}
