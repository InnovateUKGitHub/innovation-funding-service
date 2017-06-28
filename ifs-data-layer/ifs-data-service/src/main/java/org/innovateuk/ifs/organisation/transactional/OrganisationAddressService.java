package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.commons.security.NotSecured;

public interface OrganisationAddressService {

    @NotSecured(value = "Anyone can see an OrganisationAddress", mustBeSecuredByOtherServices = false)
    ServiceResult<OrganisationAddressResource> findOne(Long id);
}
