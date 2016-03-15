package com.worth.ifs.organisation.service;

import com.worth.ifs.organisation.resource.OrganisationSearchResult;

import java.util.List;

/**
 * Interface for communication with the company house services
 */
public interface CompanyHouseRestService {
    List<OrganisationSearchResult> searchOrganisations(String searchText);
    OrganisationSearchResult getOrganisationById(String organisationId);
}
