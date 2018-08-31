package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;

import java.util.List;

public interface OrganisationSearchRestService {
    RestResult<List<OrganisationSearchResult>> searchOrganisation(Long organisationTypeId, String organisationSearchText);

    RestResult<OrganisationSearchResult> getOrganisation(Long organisationTypeId, String searchOrganisationId);

    RestResult<List<OrganisationSearchResult>> searchOrganisation(Enum<?> organisationType, String organisationSearchText);

    RestResult<OrganisationSearchResult> getOrganisation(Enum<?> organisationType, String searchOrganisationId);


}
