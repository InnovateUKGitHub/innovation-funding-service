package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.rest.RestResult;

public interface ApplicationStatusRestService {

    RestResult<ApplicationStatusResource> getApplicationStatusById(Long id);
}
