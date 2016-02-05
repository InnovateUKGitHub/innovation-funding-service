package com.worth.ifs.application.service;

import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.organisation.service.CompanyHouseRestService;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link Organisation} related data,
 * through the RestService {@link com.worth.ifs.user.service.OrganisationRestService}.
 */
@Service
public class OrganisationServiceImpl implements OrganisationService {
    @Autowired
    OrganisationRestService organisationRestService;
    @Autowired
    CompanyHouseRestService companyHouseRestService;

    @Override
    public CompanyHouseBusiness getCompanyHouseOrganisation(String organisationId) {
        return  companyHouseRestService.getOrganisationById(organisationId);
    }

    @Override
    public List<CompanyHouseBusiness> searchCompanyHouseOrganisations(String searchText) {
        return  companyHouseRestService.searchOrganisations(searchText);
    }


    public Organisation getOrganisationById(Long organisationId) {
        return organisationRestService.getOrganisationById(organisationId);
    }

    @Override
    public OrganisationResource save(Organisation organisation) {
        return organisationRestService.save(organisation);
    }

    @Override
    public OrganisationResource save(OrganisationResource organisation) {
        return organisationRestService.save(organisation);
    }

    @Override
    public OrganisationResource addAddress(OrganisationResource organisation, Address address, AddressType addressType) {
        return organisationRestService.addAddress(organisation, address, addressType);
    }

}
