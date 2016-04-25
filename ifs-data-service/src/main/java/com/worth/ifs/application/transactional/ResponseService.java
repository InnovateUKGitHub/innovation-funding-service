package com.worth.ifs.application.transactional;


import java.util.List;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.resource.ResponseResource;
import com.worth.ifs.commons.service.ServiceResult;

import org.springframework.security.access.prepost.PreAuthorize;

public interface ResponseService {

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<ResponseResource>> findResponseResourcesByApplication(final Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<Response>> findResponsesByApplication(final Long applicationId);
}
