package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarOrganisationType;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

public interface HeukarOrganisationTypeService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Set<HeukarOrganisationType>> findByApplicationId(long applicationId);

}
