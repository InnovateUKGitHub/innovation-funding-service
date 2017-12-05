package org.innovateuk.ifs.application.team.security;

import org.innovateuk.ifs.application.resource.ApplicationCompositeId;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.stereotype.Component;

/**
 * Rule to look up an {@link ApplicationCompositeId} from a {@link Long} application id. This can then be feed into
 * methods marked with the {@link org.innovateuk.ifs.commons.security.PermissionRule} annotation as part of the Spring
 * security mechanism. Note that the reason we don't simply feed the {@link Long} id into permission rules is that
 * there would then be no way of distinguishing between entity types in a given rule.
 */
@Component
@PermissionEntityLookupStrategies
public class ApplicationLookupStrategy {

    @PermissionEntityLookupStrategy
    public ApplicationCompositeId getApplicationCompositeId(Long applicationId) {
        return ApplicationCompositeId.id(applicationId);
    }
}
