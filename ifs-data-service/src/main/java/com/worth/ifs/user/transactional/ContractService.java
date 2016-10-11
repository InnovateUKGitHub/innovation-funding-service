package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.ContractResource;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.user.domain.Contract} data.
 */
public interface ContractService {
    //TODO: review security annotation
    @NotSecured(value = "Still needs to be secured", mustBeSecuredByOtherServices = false)
    ServiceResult<ContractResource> getCurrent();
}