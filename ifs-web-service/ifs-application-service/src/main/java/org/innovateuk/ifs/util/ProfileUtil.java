package org.innovateuk.ifs.util;

import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.util.Optional;

/**
 * Util for common function in the profile section
 */
public final class ProfileUtil {

    private ProfileUtil() {}

    public static Optional<OrganisationAddressResource> getAddress(final OrganisationResource organisation) {
        Optional<OrganisationAddressResource> registeredAddress = getAddress(organisation, OrganisationAddressType.OPERATING);
        if(registeredAddress.isPresent()) {
            return registeredAddress;
        }
        return getAddress(organisation, OrganisationAddressType.REGISTERED);
    }

    private static Optional<OrganisationAddressResource> getAddress(final OrganisationResource organisation, final OrganisationAddressType addressType) {
        return organisation.getAddresses().stream().filter(a -> addressType.equals(OrganisationAddressType.valueOf(a.getAddressType().getName()))).findFirst();
    }
}
