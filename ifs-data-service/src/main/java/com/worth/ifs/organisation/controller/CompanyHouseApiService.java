package com.worth.ifs.organisation.controller;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;

import java.util.List;

/**
 *
 */
public interface CompanyHouseApiService {

    ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText);

    ServiceResult<OrganisationSearchResult> getOrganisationById(String id);
}
