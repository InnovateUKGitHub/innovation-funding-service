package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationService {
    public TreeSet<Organisation> getApplicationOrganisations(ApplicationResource application);
    public Optional<Organisation> getApplicationLeadOrganisation(ApplicationResource application);
    public Optional<Organisation> getUserOrganisation(ApplicationResource application, Long userId);
    public Organisation getOrganisationById(Long organisationId);
    public OrganisationResource save(Organisation organisation);
    public OrganisationResource save(OrganisationResource organisation);
    public OrganisationSearchResult getCompanyHouseOrganisation(String organisationId);
    public List<OrganisationSearchResult> searchCompanyHouseOrganisations(String searchText);
    public OrganisationResource addAddress(OrganisationResource organisation, Address address, AddressType type);
}
