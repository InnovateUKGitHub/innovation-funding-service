package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationExternalConfigRestServiceImpl extends BaseRestService implements ApplicationExternalConfigRestService{

    private String applicationExternalConfigUrl = "/application-external-config";

    @Override
    public RestResult<ApplicationExternalConfigResource> findOneByApplicationId(long applicationId) {
        return getWithRestResult(applicationExternalConfigUrl + "/" + applicationId,
                ApplicationExternalConfigResource.class);
    }

    @Override
    public RestResult<ApplicationExternalConfigResource> update(long applicationId, ApplicationExternalConfigResource applicationExternalConfigResource) {
        return putWithRestResult(applicationExternalConfigUrl + "/" + applicationId, applicationExternalConfigUrl, ApplicationExternalConfigResource.class);
    }
}
