package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Interface for retrieving {@link OrganisationSizeResource}s.
 */
public interface OrganisationSizeService {

    @PreAuthorize("hasAnyAuthority('applicant', 'comp_admin', 'project_finance', 'support')")
    @SecuredBySpring(value = "READ", securedType = OrganisationSizeResource.class, description = "Only applicants and internal competition administrator, project finance and support users can see the organisation size options.")
    ServiceResult<List<OrganisationSizeResource>> getOrganisationSizes();

}
