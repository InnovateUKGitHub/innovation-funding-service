package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GrantClaimMaximumService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<GrantClaimMaximumResource> getGrantClaimMaximumById(final Long id);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can update grantClaimMaximums")
    @PreAuthorize("hasAnyAuthority('comp_admin')")
    ServiceResult<GrantClaimMaximumResource> save(Long id, GrantClaimMaximumResource grantClaimMaximumResource);
}
