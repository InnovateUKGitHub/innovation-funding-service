package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationStatusRestServiceImpl extends BaseRestService implements ApplicationStatusRestService {

    private String applicationStatusRestURL = "/applicationstatus";

    @Override
    public RestResult<ApplicationStatusResource> getApplicationStatusById(Long id) {
        return getWithRestResult(applicationStatusRestURL + "/" + id, ApplicationStatusResource.class);
    }
}
