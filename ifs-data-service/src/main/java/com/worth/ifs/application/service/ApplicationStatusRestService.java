package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationStatusResource;

public interface ApplicationStatusRestService {
    ApplicationStatusResource getApplicationStatusById(Long id);
}
