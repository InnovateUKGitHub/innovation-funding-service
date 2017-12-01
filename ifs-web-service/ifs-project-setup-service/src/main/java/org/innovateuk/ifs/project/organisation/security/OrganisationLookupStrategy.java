package org.innovateuk.ifs.project.organisation.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.organisation.resource.OrganisationCompositeId;
import org.springframework.stereotype.Component;

/**
 * Rule to look up an {@link OrganisationCompositeId} from a {@link Long} project group id. This can then be feed into
 * methods marked with the {@link org.innovateuk.ifs.commons.security.PermissionRule} annotation as part of the Spring
 * security mechanism. Note that the reason we don't simply feed the {@link Long} id into permission rules is that
 * there would then be no way of distinguishing between entity types in a given rule.
 */
@Component
@PermissionEntityLookupStrategies
public class OrganisationLookupStrategy {

    @PermissionEntityLookupStrategy
    public OrganisationCompositeId getProjectCompositeId(Long organisationId) {
        return OrganisationCompositeId.id(organisationId);
    }
}
