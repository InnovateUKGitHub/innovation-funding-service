package com.worth.ifs.application.service;

import java.util.List;
import java.util.Optional;

import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;

/**
 * Interface for CRUD operations on {@link OrganisationResource} related data.
 */
public interface OrganisationService {

    OrganisationResource getOrganisationById(Long organisationId);

    OrganisationResource getOrganisationByIdForAnonymousUserFlow(Long organisationId);

    OrganisationResource saveForAnonymousUserFlow(OrganisationResource organisation);

    OrganisationResource save(OrganisationResource organisation);

    OrganisationSearchResult getCompanyHouseOrganisation(String organisationId);

    OrganisationResource addAddress(OrganisationResource organisation, AddressResource address, AddressType type);

    String getOrganisationType(Long userId, Long applicationId);

    Optional<OrganisationResource> getUserForOrganisation(Long userId, List<ProcessRoleResource> userApplicationRoles);
}
