package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressTypeEnum;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapper;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Determines if a registering user has to become part of an already existing organisation Companies House or Je-s organisations by specific organisation details.
 */
@Service
public class OrganisationMatchingService {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationAddressMapper organisationAddressMapper;

    public Optional<Organisation> findOrganisationMatch(OrganisationResource organisationResource) {
        List<Organisation> organisations;

        if(OrganisationTypeEnum.isResearch(organisationResource.getOrganisationType())) {
            organisations = findOrganisationMatchByName(organisationResource);
        } else {
            organisations = findCompaniesHouseIdOrganisationMatch(organisationResource);
        }

        return organisations.stream().filter(foundOrganisation -> organisationAddressAndTypeMatch(foundOrganisation, organisationResource)).findFirst();
    }

    private boolean organisationAddressAndTypeMatch(Organisation organisation, OrganisationResource organisationResource) {
        return organisationOperatingAddressMatches(organisation, organisationResource)
                && organisationTypeMatches(organisation, organisationResource);
    }

    private boolean organisationOperatingAddressMatches(Organisation organisation, OrganisationResource organisationResource) {
        Optional<OrganisationAddress> organisationOperatingAddress = organisation.getAddresses().stream()
                .filter(findAddress -> findAddress.getAddressType().getId().equals(AddressTypeEnum.OPERATING.getOrdinal()))
                .findFirst();
        OrganisationAddress submittedOrganisationAddress = organisationAddressMapper.mapToDomain(organisationResource.getAddresses().get(0));

        return organisationOperatingAddress.filter(organisationAddress -> matchAddresses(organisationAddress, submittedOrganisationAddress)).isPresent();
    }

    private boolean matchAddresses(OrganisationAddress existingOrganisationAddress, OrganisationAddress submittedOrganisationAddress) {
        Address existingAddress = existingOrganisationAddress.getAddress();
        Address submittedAddress = submittedOrganisationAddress.getAddress();

        return trimmedLowercaseStringsAreEquals(existingAddress.getAddressLine1(), submittedAddress.getAddressLine1()) &&
                trimmedLowercaseStringsAreEquals(existingAddress.getAddressLine2(), submittedAddress.getAddressLine2()) &&
                trimmedLowercaseStringsAreEquals(existingAddress.getAddressLine3(), submittedAddress.getAddressLine3()) &&
                trimmedLowercaseStringsAreEquals(existingAddress.getTown(), submittedAddress.getTown()) &&
                trimmedLowercaseStringsAreEquals(existingAddress.getCounty(), submittedAddress.getCounty()) &&
                trimmedLowercaseStringsAreEquals(existingAddress.getPostcode(), submittedAddress.getPostcode());
    }

    private boolean trimmedLowercaseStringsAreEquals(String string1, String string2) {
        return string1.toLowerCase().trim().equals(string2.toLowerCase().trim());
    }

    private boolean organisationTypeMatches(Organisation organisation, OrganisationResource organisationResource) {
        return organisation.getOrganisationType().getId().equals(organisationResource.getOrganisationType());
    }

    private List<Organisation> findOrganisationMatchByName(OrganisationResource organisationResource) {
        return find(organisationRepository.findByName(organisationResource.getName()), notFoundError(OrganisationResource.class, organisationResource)).getSuccessObject();
    }

    private List<Organisation> findCompaniesHouseIdOrganisationMatch(OrganisationResource organisationResource) {
        return find(organisationRepository.findByCompaniesHouseNumber(organisationResource.getCompanyHouseNumber()), notFoundError(OrganisationResource.class, organisationResource)).getSuccessObject();
    }
}
