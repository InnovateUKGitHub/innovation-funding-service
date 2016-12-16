package org.innovateuk.ifs.project;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;

import java.util.List;

public interface PartnerOrganisationService {
    ServiceResult<List<PartnerOrganisationResource>> getPartnerOrganisations(Long projectId);
}
