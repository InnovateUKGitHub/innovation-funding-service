package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.ContractResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.user.domain.Contract} data.
 */
public interface ContractService {

    @PreAuthorize("hasAuthority('assessor')")
    ServiceResult<ContractResource> getCurrent();

}