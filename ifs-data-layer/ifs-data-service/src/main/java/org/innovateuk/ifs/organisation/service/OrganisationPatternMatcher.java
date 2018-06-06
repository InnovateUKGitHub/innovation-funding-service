package org.innovateuk.ifs.organisation.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Contains static matching logic used in @{OrganisationMatchingServiceImpl}.
 */
@Service
public class OrganisationPatternMatcher {

    private static final Log LOG = LogFactory.getLog(OrganisationPatternMatcher.class);

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean organisationAddressMatches(Organisation existingOrganisation, OrganisationResource submittedOrganisation, OrganisationAddressType addressType, boolean required) {
        Optional<OrganisationAddress> organisationOperatingAddress = getOrganisationAddressByType(existingOrganisation, addressType);
        Optional<OrganisationAddressResource> submittedOrganisationAddress = getOrganisationResourceAddressByType(submittedOrganisation, addressType);

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
        catch(NullPointerException e) {
            LOG.trace("NPE when checking organisation type match", e);
            return false;
        }
    }

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean organisationTypeIsResearch(Organisation organisation) {
        try {
            return OrganisationTypeEnum.isResearch(organisation.getOrganisationType().getId());
        }
        catch(NullPointerException e) {
            LOG.trace("NPE when checking organisation type is research", e);
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

        if(string1 == null || string2 == null) {
            return false;
        }
        return string1.trim().equalsIgnoreCase(string2.trim());
    }

    private Optional<OrganisationAddress> getOrganisationAddressByType(Organisation organisation, OrganisationAddressType addressType) {
        try {
            return organisation.getAddresses().stream()
                    .filter(findAddress -> findAddress.getAddressType().getId().equals(addressType.getOrdinal()))
                    .findAny();
        } catch(NullPointerException e) {
            LOG.trace("NPE when getting organisation address by type", e);
            return Optional.empty();
        }
    }

    private Optional<OrganisationAddressResource> getOrganisationResourceAddressByType(OrganisationResource organisationResource, OrganisationAddressType addressType) {
        try {
            return organisationResource.getAddresses().stream()
                .filter(findAddress -> findAddress.getAddressType().getId().equals(addressType.getOrdinal()))
                .findAny();
        } catch(NullPointerException e) {
            LOG.trace("NPE when getting organisation resource address by type", e);
            return Optional.empty();
        }
    }
}
