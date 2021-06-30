package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ApplicationExternalConfigService {

    @SecuredBySpring(value = "READ", description = "A system maintainer can see the application")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<ApplicationExternalConfigResource> findOneByApplicationId(long applicationId);

    @SecuredBySpring(value = "UPDATE_EXTERNAL_APP", securedType= ApplicationExternalConfigResource.class, description = "A system maintainer can update the external application data ")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<Void> update(long applicationId, ApplicationExternalConfigResource applicationExternalConfigResource);
}
