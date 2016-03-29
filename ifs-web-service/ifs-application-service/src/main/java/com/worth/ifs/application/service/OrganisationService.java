package com.worth.ifs.application.service;

import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationService {
    SortedSet<Organisation> getApplicationOrganisations(ApplicationResource application);
    Optional<Organisation> getApplicationLeadOrganisation(ApplicationResource application);
    Optional<Organisation> getUserOrganisation(ApplicationResource application, Long userId);
    Organisation getOrganisationById(Long organisationId);
    OrganisationResource save(Organisation organisation);
    OrganisationResource save(OrganisationResource organisation);
    OrganisationSearchResult getCompanyHouseOrganisation(String organisationId);
    List<OrganisationSearchResult> searchCompanyHouseOrganisations(String searchText);
    OrganisationResource addAddress(OrganisationResource organisation, AddressResource address, AddressType type);
    String getOrganisationType(Long userId, Long applicationId);
}
