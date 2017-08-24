package org.innovateuk.ifs.util;

import org.innovateuk.ifs.address.resource.AddressTypeEnum;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Optional;

/**
 * Util for common function in the profile section
 */
public class ProfileUtil {

    private static final Log LOG = LogFactory.getLog(ProfileUtil.class);

    public static Optional<OrganisationAddressResource> getAddress(final OrganisationResource organisation) {
        Optional<OrganisationAddressResource> registeredAddress = getAddress(organisation, AddressTypeEnum.OPERATING);
        if(registeredAddress.isPresent()) {
            return registeredAddress;
        }
        return getAddress(organisation, AddressTypeEnum.REGISTERED);
    }

    private static Optional<OrganisationAddressResource> getAddress(final OrganisationResource organisation, final AddressTypeEnum addressType) {
        return organisation.getAddresses().stream().filter(a -> addressType.equals(AddressTypeEnum.valueOf(a.getAddressType().getName()))).findFirst();
    }
}
