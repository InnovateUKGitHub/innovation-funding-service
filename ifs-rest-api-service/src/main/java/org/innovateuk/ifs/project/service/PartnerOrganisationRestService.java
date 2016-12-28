package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;

import java.util.List;

public interface PartnerOrganisationRestService {
    public RestResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId);
}
