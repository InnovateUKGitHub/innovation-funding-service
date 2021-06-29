package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface ApplicationExternalConfigService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ApplicationExternalConfigResource> findOneByApplicationId(long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> update(long applicationId, ApplicationExternalConfigResource applicationExternalConfigResource);
}
