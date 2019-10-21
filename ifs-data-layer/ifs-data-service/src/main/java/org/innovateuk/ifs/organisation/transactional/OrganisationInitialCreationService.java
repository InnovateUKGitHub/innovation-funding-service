package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OrganisationInitialCreationService {
    @PreAuthorize("hasPermission(#organisation, 'CREATE')")
    ServiceResult<OrganisationResource> createOrMatch(@P("organisation") OrganisationResource organisation);
}
