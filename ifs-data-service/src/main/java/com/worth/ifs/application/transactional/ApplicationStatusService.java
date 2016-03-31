package com.worth.ifs.application.transactional;

import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

public interface ApplicationStatusService {

    @NotSecured("TODO")
    ServiceResult<ApplicationStatusResource> getById(Long id);
}
