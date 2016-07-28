package com.worth.ifs.application.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.service.ServiceResult;

public interface AddressService {
    ServiceResult<AddressResource> getById(final long id);
}
