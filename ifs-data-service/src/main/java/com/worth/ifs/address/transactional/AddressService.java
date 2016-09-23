package com.worth.ifs.address.transactional;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.commons.security.NotSecured;

public interface AddressService {
    @NotSecured(value = "Everyone should be able to lookup an address by id", mustBeSecuredByOtherServices = false)
    ServiceResult<AddressResource> getById(final Long id);
}
