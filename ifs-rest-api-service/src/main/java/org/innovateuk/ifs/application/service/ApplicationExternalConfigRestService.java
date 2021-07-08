package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface ApplicationExternalConfigRestService {
    RestResult<ApplicationExternalConfigResource> findOneByApplicationId(long applicationId);
    RestResult<ApplicationExternalConfigResource> update(long applicationId, ApplicationExternalConfigResource applicationExternalConfigResource);
}
