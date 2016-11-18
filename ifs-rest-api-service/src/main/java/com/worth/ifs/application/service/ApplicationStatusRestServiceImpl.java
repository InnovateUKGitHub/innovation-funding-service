package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationStatusRestServiceImpl extends BaseRestService implements ApplicationStatusRestService {

    private String applicationStatusRestURL = "/applicationstatus";

    @Override
    public RestResult<ApplicationStatusResource> getApplicationStatusById(Long id) {
        return getWithRestResult(applicationStatusRestURL + "/" + id, ApplicationStatusResource.class);
    }
}
