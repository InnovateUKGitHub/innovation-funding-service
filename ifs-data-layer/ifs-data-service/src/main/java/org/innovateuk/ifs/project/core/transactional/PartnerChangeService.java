package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PartnerChangeService {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PROJECT')")
    ServiceResult<Void> updateProjectAfterChangingPartners(long projectId);
}
