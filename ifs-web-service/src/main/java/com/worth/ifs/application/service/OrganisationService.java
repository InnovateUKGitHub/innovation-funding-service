package com.worth.ifs.application.service;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationService {
    public SortedSet<Organisation> getApplicationOrganisations(ApplicationResource application);
    public Optional<Organisation> getApplicationLeadOrganisation(ApplicationResource application);
    public Optional<Organisation> getUserOrganisation(ApplicationResource application, Long userId);
    public Organisation getOrganisationById(Long organisationId);
    public OrganisationResource save(Organisation organisation);
    public OrganisationResource save(OrganisationResource organisation);
    public OrganisationSearchResult getCompanyHouseOrganisation(String organisationId);
    public List<OrganisationSearchResult> searchCompanyHouseOrganisations(String searchText);
    public OrganisationResource addAddress(OrganisationResource organisation, AddressResource address, AddressType type);
    public String getOrganisationType(Long userId, Long applicationId);
}
