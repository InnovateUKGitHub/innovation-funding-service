package org.innovateuk.ifs.util;

import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Optional;

/**
 * Util for common function in the profile section
 */
public class ProfileUtil {

    private static final Log LOG = LogFactory.getLog(ProfileUtil.class);

    public static Optional<OrganisationAddressResource> getAddress(final OrganisationResource organisation) {
        Optional<OrganisationAddressResource> registeredAddress = getAddress(organisation, OrganisationAddressType.OPERATING);
        if(registeredAddress.isPresent()) {
            return registeredAddress;
        }
        return getAddress(organisation, OrganisationAddressType.REGISTERED);
    }

    public static Long getUserOrganisationId(final UserResource userResource) {
        if(!userResource.getOrganisations().isEmpty()) {
            Long organisationId = userResource.getOrganisations().get(0);
            return organisationId;
        } else {
            LOG.warn("No organisation associated with user" + userResource.getId());
            return null;
        }
    }

    private static Optional<OrganisationAddressResource> getAddress(final OrganisationResource organisation, final OrganisationAddressType addressType) {
        return organisation.getAddresses().stream().filter(a -> addressType.equals(OrganisationAddressType.valueOf(a.getAddressType().getName()))).findFirst();
    }


}
