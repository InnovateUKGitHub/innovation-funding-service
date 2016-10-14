package com.worth.ifs.project;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.PartnerOrganisationResource;

import java.util.List;

public interface PartnerOrganisationService {
    ServiceResult<List<PartnerOrganisationResource>> getPartnerOrganisations(Long projectId);
}
