package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;

public interface OrganisationApplicationAddressService {


    @NotSecured(value = "Anyone can see an OrganisationAddress", mustBeSecuredByOtherServices = false)
    ServiceResult<OrganisationAddressResource> findByOrganisationIdAndApplicationIdAndAddressId(long organisationId, long applicationId, long addressId);
}
