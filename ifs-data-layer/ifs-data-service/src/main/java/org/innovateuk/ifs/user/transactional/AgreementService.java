package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around {@link Agreement} data.
 */
public interface AgreementService {

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('assessor')")
    ServiceResult<AgreementResource> getCurrent();

}
