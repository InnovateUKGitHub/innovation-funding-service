package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectPartnerChangeService {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PROJECT')")
    ServiceResult<Void> updateProjectWhenPartnersChange(long projectId);
}
