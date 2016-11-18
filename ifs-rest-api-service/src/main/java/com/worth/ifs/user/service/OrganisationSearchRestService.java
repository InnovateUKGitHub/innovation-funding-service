package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;

import java.util.List;

public interface OrganisationSearchRestService {
    RestResult<List<OrganisationSearchResult>> searchOrganisation(Long organisationTypeId, String organisationSearchText);

    RestResult<OrganisationSearchResult> getOrganisation(Long organisationTypeId, String searchOrganisationId);
}
