package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link OrganisationResource} related data.
 */
public interface OrganisationRestService {

    RestResult<List<OrganisationResource>> getOrganisationsByApplicationId(Long applicationId);
    RestResult<OrganisationResource> getOrganisationById(Long organisationId);
    RestResult<OrganisationResource> getOrganisationByIdForAnonymousUserFlow(Long organisationId);
    RestResult<OrganisationResource> create(OrganisationResource organisation);
    RestResult<OrganisationResource> update(OrganisationResource organisation);
    RestResult<OrganisationResource> updateNameAndRegistration(OrganisationResource organisation);
    RestResult<OrganisationResource> updateByIdForAnonymousUserFlow(OrganisationResource organisation);
    RestResult<OrganisationResource> addAddress(OrganisationResource organisation, AddressResource address, OrganisationAddressType type);
}
