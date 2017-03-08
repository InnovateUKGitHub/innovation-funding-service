package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface OrganisationSizeService {

    @PreAuthorize("hasAnyAuthority('applicant')")
    @SecuredBySpring(value = "READ", securedType = OrganisationSizeResource.class, description = "Only applicants can see the organisation size options.")
    ServiceResult<List<OrganisationSizeResource>> getOrganisationSizes();

}
