package com.worth.ifs.application.service;

import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.Optional;
import java.util.SortedSet;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationService {
    SortedSet<OrganisationResource> getApplicationOrganisations(ApplicationResource application);
    Optional<OrganisationResource> getApplicationLeadOrganisation(ApplicationResource application);
    Optional<OrganisationResource> getUserOrganisation(ApplicationResource application, Long userId);
    OrganisationResource getOrganisationById(Long organisationId);

    OrganisationResource getOrganisationByIdForAnonymousUserFlow(Long organisationId);

    OrganisationResource saveForAnonymousUserFlow(OrganisationResource organisation);
    OrganisationResource save(OrganisationResource organisation);
    OrganisationSearchResult getCompanyHouseOrganisation(String organisationId);
    OrganisationResource addAddress(OrganisationResource organisation, AddressResource address, AddressType type);
    String getOrganisationType(Long userId, Long applicationId);
}
