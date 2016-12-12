package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;

import java.util.List;

/**
 * Interface for communication with the company house services
 */
public interface CompanyHouseRestService {
    List<OrganisationSearchResult> searchOrganisations(String searchText);
    OrganisationSearchResult getOrganisationById(String organisationId);
}
