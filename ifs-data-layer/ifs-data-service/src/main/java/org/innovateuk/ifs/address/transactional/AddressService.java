package org.innovateuk.ifs.address.transactional;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.commons.security.NotSecured;

public interface AddressService {
    @NotSecured(value = "Everyone should be able to lookup an address by id", mustBeSecuredByOtherServices = false)
    ServiceResult<AddressResource> getById(final Long id);
}
