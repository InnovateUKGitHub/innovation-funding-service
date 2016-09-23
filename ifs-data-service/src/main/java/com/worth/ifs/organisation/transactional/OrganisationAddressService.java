package com.worth.ifs.organisation.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.commons.security.NotSecured;

public interface OrganisationAddressService {

    @NotSecured(value = "Anyone can see an OrganisationAddress", mustBeSecuredByOtherServices = false)
    ServiceResult<OrganisationAddressResource> findOne(Long id);
}