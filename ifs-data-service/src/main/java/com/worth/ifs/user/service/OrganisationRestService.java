package com.worth.ifs.user.service;

import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationRestService {
    public List<Organisation> getOrganisationsByApplicationId(Long applicationId);
    public Organisation getOrganisationById(Long organisationId);
    public OrganisationResource save(Organisation organisation);
    public OrganisationResource addAddress(OrganisationResource organisation, Address address, AddressType type);


}
