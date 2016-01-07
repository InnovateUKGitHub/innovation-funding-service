package com.worth.ifs.application.service;


import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;

public class ApplicationStatusRestServiceImpl extends BaseRestService implements ApplicationStatusRestService {

    @Value("${ifs.data.service.rest.applicationstatus}")
    String applicationStatusRestURL;

    @Override
    public ApplicationStatusResource getApplicationStatusById(Long id) {
        return restGet(applicationStatusRestURL+"/"+id, ApplicationStatusResource.class);
    }
}
