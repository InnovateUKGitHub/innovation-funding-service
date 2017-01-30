package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.user.domain.Contract} data.
 */
public interface ContractService {

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('assessor')")
    ServiceResult<ContractResource> getCurrent();

}
