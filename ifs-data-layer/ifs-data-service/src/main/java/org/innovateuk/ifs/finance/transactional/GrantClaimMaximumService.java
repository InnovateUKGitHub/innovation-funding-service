package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GrantClaimMaximumService {

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the GrantClaimMaximums")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<GrantClaimMaximumResource> getGrantClaimMaximumById(final Long id);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can update GrantClaimMaximums")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> update(GrantClaimMaximumResource grantClaimMaximumResource);
}
