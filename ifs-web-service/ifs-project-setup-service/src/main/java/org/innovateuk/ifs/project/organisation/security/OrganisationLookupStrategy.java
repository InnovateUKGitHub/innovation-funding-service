package org.innovateuk.ifs.project.organisation.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.organisation.resource.OrganisationCompositeId;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class OrganisationLookupStrategy {

    @PermissionEntityLookupStrategy
    public OrganisationCompositeId getProjectCompositeId(Long organisationId) {
        return OrganisationCompositeId.id(organisationId);
    }
}
