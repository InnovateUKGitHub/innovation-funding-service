package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationRestService {

    RestResult<List<Organisation>> getOrganisationsByApplicationId(Long applicationId);
    RestResult<Organisation> getOrganisationById(Long organisationId);
    RestResult<OrganisationResource> save(Organisation organisation);
    RestResult<OrganisationResource> save(OrganisationResource organisation);
    RestResult<OrganisationResource> addAddress(OrganisationResource organisation, AddressResource address, AddressType type);
}
