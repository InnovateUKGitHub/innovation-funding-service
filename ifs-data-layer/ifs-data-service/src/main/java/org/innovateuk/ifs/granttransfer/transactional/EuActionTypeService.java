package org.innovateuk.ifs.granttransfer.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for retreiving eu action types.
 */
public interface EuActionTypeService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'applicant')")
    @SecuredBySpring(value = "ACTION_TYPES",
            description = "Competition Admins, Project Finance users and applicants can view action types")
    ServiceResult<List<EuActionTypeResource>> findAll();

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'applicant')")
    @SecuredBySpring(value = "ACTION_TYPE",
            description = "Competition Admins, Project Finance users and applicants can view action types")
    ServiceResult<EuActionTypeResource> getById(long id);
}
