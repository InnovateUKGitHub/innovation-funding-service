package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OrganisationRestServiceImpl is a utility for CRUD operations on {@link OrganisationResource}.
 * This class connects to the {org.innovateuk.ifs.user.controller.OrganisationController}
 * through a REST call.
 */
@Service
public class OrganisationSearchRestServiceImpl extends BaseRestService implements OrganisationSearchRestService {

    private String organisationRestURL = "/organisationsearch";

    @Override
    public RestResult<List<OrganisationSearchResult>> searchOrganisation(Long organisationTypeId, String organisationSearchText) {
        return getWithRestResultAnonymous(organisationRestURL + "/searchOrganisations/" + organisationTypeId + "?organisationSearchText="+ organisationSearchText, new ParameterizedTypeReference<List<OrganisationSearchResult>>() {});
    }

    @Override
    public RestResult<OrganisationSearchResult> getOrganisation(Long organisationTypeId, String searchOrganisationId) {
        return getWithRestResultAnonymous(organisationRestURL + "/getOrganisation/" + organisationTypeId + "/" + searchOrganisationId, new ParameterizedTypeReference<OrganisationSearchResult>() {});
    }
}
