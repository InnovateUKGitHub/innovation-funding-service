package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OrganisationRestServiceImpl is a utility for CRUD operations on {@link com.worth.ifs.user.resource.OrganisationResource}.
 * This class connects to the {com.worth.ifs.user.controller.OrganisationController}
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
