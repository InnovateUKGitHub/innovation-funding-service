package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.ContractResource;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.user.domain.Contract} data.
 */
public interface ContractService {
    ServiceResult<ContractResource> getCurrent();
}