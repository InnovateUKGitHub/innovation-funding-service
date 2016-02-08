package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OrganisationRestServiceImpl is a utility for CRUD operations on {@link Organisation}.
 * This class connects to the {@link com.worth.ifs.user.controller.OrganisationController}
 * through a REST call.
 */
@Service
public class OrganisationRestServiceImpl extends BaseRestService implements OrganisationRestService {

    @Value("${ifs.data.service.rest.organisation}")
    String organisationRestURL;

    @Override
    public RestResult<List<Organisation>> getOrganisationsByApplicationId(Long applicationId) {
        return getWithRestResult(organisationRestURL + "/findByApplicationId/" + applicationId, new ParameterizedTypeReference<List<Organisation>>() {});
    }

    @Override
    public RestResult<Organisation> getOrganisationById(Long organisationId) {
        return getWithRestResult(organisationRestURL + "/findById/"+organisationId, Organisation.class);
    }

    @Override
    public RestResult<OrganisationResource> save(Organisation organisation) {
        return postWithRestResult(organisationRestURL + "/save", organisation, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> save(OrganisationResource organisation) {
        return postWithRestResult(organisationRestURL + "/saveResource", organisation, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> addAddress(OrganisationResource organisation, Address address, AddressType type) {
        return postWithRestResult(organisationRestURL + "/addAddress/"+organisation.getId()+"?addressType="+type.name(), address, OrganisationResource.class);
    }
}
