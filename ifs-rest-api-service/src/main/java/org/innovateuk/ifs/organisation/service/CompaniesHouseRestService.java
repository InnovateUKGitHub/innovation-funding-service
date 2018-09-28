package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;

import java.util.List;

/**
 * Interface for communication with the companies house services
 */
public interface CompaniesHouseRestService {
    RestResult<List<OrganisationSearchResult>> searchOrganisations(String searchText);
    RestResult<OrganisationSearchResult> getOrganisationById(String organisationId);
}
