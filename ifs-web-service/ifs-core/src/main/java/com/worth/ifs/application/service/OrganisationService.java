package com.worth.ifs.application.service;

import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationService {

    OrganisationResource getOrganisationById(Long organisationId);

    OrganisationResource getOrganisationByIdForAnonymousUserFlow(Long organisationId);

    OrganisationResource saveForAnonymousUserFlow(OrganisationResource organisation);

    OrganisationResource save(OrganisationResource organisation);

    OrganisationSearchResult getCompanyHouseOrganisation(String organisationId);

    OrganisationResource addAddress(OrganisationResource organisation, AddressResource address, AddressType type);

    String getOrganisationType(Long userId, Long applicationId);

}
