package org.innovateuk.ifs.application.team.security;

import org.innovateuk.ifs.application.resource.ApplicationCompositeId;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ApplicationLookupStrategy {

    @PermissionEntityLookupStrategy
    public ApplicationCompositeId getApplicationCompositeId(Long applicationId) {
        return ApplicationCompositeId.id(applicationId);
    }
}
