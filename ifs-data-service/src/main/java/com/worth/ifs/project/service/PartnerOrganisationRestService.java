package com.worth.ifs.project.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.PartnerOrganisationResource;

import java.util.List;

public interface PartnerOrganisationRestService {
    public RestResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId);
}
