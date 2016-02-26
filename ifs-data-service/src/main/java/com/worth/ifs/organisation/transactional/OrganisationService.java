package com.worth.ifs.organisation.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.Set;

/**
 * Represents operations surrounding the use of Organisations in the system
 */
public interface OrganisationService {

    @NotSecured("TODO DW - implement security when permissions matrix known")
    ServiceResult<Set<Organisation>> findByApplicationId(Long applicationId);

    @NotSecured("TODO DW - implement security when permissions matrix known")
    ServiceResult<Organisation> findById(Long organisationId);

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<OrganisationResource> create(Organisation organisation);

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<OrganisationResource> saveResource(OrganisationResource organisationResource);

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<OrganisationResource> addAddress(Long organisationId, AddressType addressType, AddressResource addressResource);
}
