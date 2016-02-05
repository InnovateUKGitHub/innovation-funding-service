package com.worth.ifs.organisation.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.Set;

/**
 * Represents operations surrounding the use of Organisations in the system
 */
public interface OrganisationService {
    ServiceResult<Set<Organisation>> findByApplicationId(Long applicationId);

    ServiceResult<Organisation> findById(Long organisationId);

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<OrganisationResource> create(Organisation organisation);

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<OrganisationResource> saveResource(OrganisationResource organisationResource);

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<OrganisationResource> addAddress(Long organisationId, AddressType addressType, Address address);
}
