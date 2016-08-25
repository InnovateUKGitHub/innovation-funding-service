package com.worth.ifs.application.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.Optional;

/**
 * Interface for CRUD operations on {@link OrganisationResource} related data.
 */
public interface OrganisationService {

    OrganisationResource getOrganisationById(Long organisationId);

    OrganisationResource getOrganisationByIdForAnonymousUserFlow(Long organisationId);

    OrganisationResource saveForAnonymousUserFlow(OrganisationResource organisation);

    OrganisationResource save(OrganisationResource organisation);

    OrganisationResource updateNameAndRegistration(OrganisationResource organisation);

    OrganisationSearchResult getCompanyHouseOrganisation(String organisationId);

    OrganisationResource addAddress(OrganisationResource organisation, AddressResource address, OrganisationAddressType type);

    String getOrganisationType(Long userId, Long applicationId);

    Optional<OrganisationResource> getOrganisationForUser(Long userId, List<ProcessRoleResource> userApplicationRoles);
}
