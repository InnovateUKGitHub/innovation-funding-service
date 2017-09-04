package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeEnum;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Contains static matching logic used in @{OrganisationMatchingServiceImpl}.
 */
@Service
public class OrganisationPatternMatcher {
    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean organisationAddressMatches(Organisation organisation, OrganisationResource organisationResource, AddressTypeEnum addressType, boolean required) {
        Optional<OrganisationAddress> organisationOperatingAddress = getOrganisationAddressByType(organisation, addressType);
        Optional<OrganisationAddressResource> submittedOrganisationAddress = getOrganisationResourceAddressByType(organisationResource, addressType);

        if(organisationOperatingAddress.isPresent() && submittedOrganisationAddress.isPresent()) {
            return addressesMatch(organisationOperatingAddress.get(), submittedOrganisationAddress.get());
        } else if(!organisationOperatingAddress.isPresent() && !submittedOrganisationAddress.isPresent() && !required) {
            return true;
        }

        return false;
    }

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean organisationTypeMatches(Organisation organisation, OrganisationResource organisationResource) {
        try {
            return organisation.getOrganisationType().getId().equals(organisationResource.getOrganisationType());
        }
        catch(Exception e) {
            return false;
        }
    }

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean organisationTypeIsResearch(Organisation organisation) {
        try {
            return OrganisationTypeEnum.isResearch(organisation.getOrganisationType().getId());
        }
        catch(Exception e) {
            return false;
        }
    }

    private boolean addressesMatch(OrganisationAddress existingOrganisationAddress, OrganisationAddressResource submittedOrganisationAddress) {
        Address existingAddress = existingOrganisationAddress.getAddress();
        AddressResource submittedAddress = submittedOrganisationAddress.getAddress();

        return stringsAreEqualWhenTrimmedAndLowercase(existingAddress.getAddressLine1(), submittedAddress.getAddressLine1()) &&
                stringsAreEqualWhenTrimmedAndLowercase(existingAddress.getAddressLine2(), submittedAddress.getAddressLine2()) &&
                stringsAreEqualWhenTrimmedAndLowercase(existingAddress.getAddressLine3(), submittedAddress.getAddressLine3()) &&
                stringsAreEqualWhenTrimmedAndLowercase(existingAddress.getTown(), submittedAddress.getTown()) &&
                stringsAreEqualWhenTrimmedAndLowercase(existingAddress.getCounty(), submittedAddress.getCounty()) &&
                stringsAreEqualWhenTrimmedAndLowercase(existingAddress.getPostcode(), submittedAddress.getPostcode());
    }

    private boolean stringsAreEqualWhenTrimmedAndLowercase(String string1, String string2) {
        if(string1 == null && string2 == null) {
            return true;
        }

        try {
            return string1.trim().equalsIgnoreCase(string2.trim());
        } catch(Exception e) {
            return false;
        }
    }

    private Optional<OrganisationAddress> getOrganisationAddressByType(Organisation organisation, AddressTypeEnum addressType) {
        try {
            return organisation.getAddresses().stream()
                    .filter(findAddress -> findAddress.getAddressType().getId().equals(addressType.getOrdinal()))
                    .findFirst();
        } catch(Exception e) {
            return Optional.empty();
        }
    }

    private Optional<OrganisationAddressResource> getOrganisationResourceAddressByType(OrganisationResource organisationResource, AddressTypeEnum addressType) {
        try {
            return organisationResource.getAddresses().stream()
                .filter(findAddress -> findAddress.getAddressType().getId().equals(addressType.getOrdinal()))
                .findFirst();
        } catch(Exception e) {
            return Optional.empty();
        }
    }
}
