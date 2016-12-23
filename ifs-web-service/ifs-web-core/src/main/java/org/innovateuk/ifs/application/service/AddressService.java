package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface AddressService {
    ServiceResult<AddressResource> getById(final long id);
}
