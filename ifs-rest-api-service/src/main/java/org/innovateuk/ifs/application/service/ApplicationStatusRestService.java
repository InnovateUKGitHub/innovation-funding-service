package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface ApplicationStatusRestService {

    RestResult<ApplicationStatusResource> getApplicationStatusById(Long id);
}
