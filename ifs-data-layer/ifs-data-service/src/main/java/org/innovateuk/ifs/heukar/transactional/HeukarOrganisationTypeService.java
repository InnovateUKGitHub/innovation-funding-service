package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarOrganisationType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface HeukarOrganisationTypeService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<OrganisationTypeResource>> findByApplicationId(long applicationId);

    ServiceResult<HeukarOrganisationType> addNewOrgTypeToApplication(long applicationId, long organisationTypeId);

}
