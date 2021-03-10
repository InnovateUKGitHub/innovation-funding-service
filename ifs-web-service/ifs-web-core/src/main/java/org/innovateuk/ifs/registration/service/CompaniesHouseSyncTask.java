package org.innovateuk.ifs.registration.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.service.CompaniesHouseRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CompaniesHouseSyncTask implements Runnable {

    private long organisationId;

    private static final String DATE_OF_CREATION = "date_of_creation";

    private final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private OrganisationRestService organisationRestService;

    private CompaniesHouseRestService companiesHouseRestService;

    private static final Log LOG = LogFactory.getLog(CompaniesHouseSyncTask.class);

    CompaniesHouseSyncTask(long organisationId, OrganisationRestService organisationRestService,CompaniesHouseRestService companiesHouseRestService ){
        this.organisationId = organisationId;
        this.organisationRestService = organisationRestService;
        this.companiesHouseRestService = companiesHouseRestService;
    }

    @Override
    public void run() {
            RestResult<OrganisationResource> org = organisationRestService.getOrganisationById(this.organisationId);
            org.getOptionalSuccessObject().ifPresent(theOrg -> getOrganisationFromCompaniesHouse(theOrg));
    }

    private void getOrganisationFromCompaniesHouse(final OrganisationResource theOrg) {
        RestResult<OrganisationSearchResult> organisationWithNewCompaniesHouseData = companiesHouseRestService.getOrganisationById(theOrg.getCompaniesHouseNumber());
        organisationWithNewCompaniesHouseData.getOptionalSuccessObject().ifPresent(theOrgWithCHData -> updateOrganisationWithCompaniesHouseData(theOrgWithCHData,theOrg));
    }

    private void updateOrganisationWithCompaniesHouseData(OrganisationSearchResult org, OrganisationResource orgResource) {
        orgResource.setSicCodes(org.getOrganisationSicCodes());
        orgResource.setExecutiveOfficers(org.getOrganisationExecutiveOfficers());
        List<OrganisationAddressResource> addressList = new ArrayList<>();
        OrganisationAddressResource organisationAddress = new OrganisationAddressResource();
        organisationAddress.setOrganisation(orgResource.getId());
        organisationAddress.setAddress(org.getOrganisationAddress());
        organisationAddress.setAddressType(new AddressTypeResource(1L, OrganisationAddressType.REGISTERED.name()));
        addressList.add(organisationAddress);
        orgResource.setAddresses(addressList);
        String localDateString = (String) org.getExtraAttributes().get(DATE_OF_CREATION);
        if (localDateString != null) {
            orgResource.setDateOfIncorporation(LocalDate.parse(localDateString, DATE_PATTERN));
        }
        RequestContextHolder.setRequestAttributes(new CustomRequestScopeAttr());
        RestResult<OrganisationResource> result = organisationRestService.syncCompaniesHouseDetails(orgResource);
        RequestContextHolder.resetRequestAttributes();
        if (result.isFailure()) {
            LOG.error("Failed to update organisation with companies house data : " + result.getFailure());
        }
    }
}
