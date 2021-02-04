package org.innovateuk.ifs.registration.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestFailure;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.service.CompaniesHouseRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


public class CompaniesHouseSyncTask implements Runnable {

    private long organistionId;

    private static final String DATE_OF_CREATION = "date_of_creation";

    private final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private OrganisationRestService organisationRestService;


    private CompaniesHouseRestService companiesHouseRestService;

    private static final Log LOG = LogFactory.getLog(CompaniesHouseSyncTask.class);



    CompaniesHouseSyncTask(long organisationId,OrganisationRestService organisationRestService,CompaniesHouseRestService companiesHouseRestService ){
        this.organistionId = organisationId;
        this.organisationRestService = organisationRestService;
        this.companiesHouseRestService = companiesHouseRestService;
    }

    @Override
    public void run() {
            RestResult<OrganisationResource> org = organisationRestService.getOrganisationById(this.organistionId);
            org.getOptionalSuccessObject().ifPresent(theOrg -> getOrganisationfromCompaniesHouse(theOrg));
    }

    private void getOrganisationfromCompaniesHouse(final OrganisationResource theOrg) {
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
        RestResult<OrganisationResource> result = organisationRestService.createOrMatch(orgResource);
        if (result.isFailure()) {
            LOG.error("Failed to update organiation with companies house data : " + result.getFailure());
        }
    }
}
