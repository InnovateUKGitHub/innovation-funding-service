package com.worth.ifs.address.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.security.NotSecured;

public interface AddressService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<AddressResource> findOne(Long id);
}