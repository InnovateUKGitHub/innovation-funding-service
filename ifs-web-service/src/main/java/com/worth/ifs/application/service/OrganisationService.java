package com.worth.ifs.application.service;

import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationService {
    public Organisation getOrganisationById(Long organisationId);
    public OrganisationResource save(Organisation organisation);
    public OrganisationResource save(OrganisationResource organisation);
    public CompanyHouseBusiness getCompanyHouseOrganisation(String organisationId);
    public List<CompanyHouseBusiness> searchCompanyHouseOrganisations(String searchText);
    public OrganisationResource addAddress(OrganisationResource organisation, Address address, AddressType type);
}
