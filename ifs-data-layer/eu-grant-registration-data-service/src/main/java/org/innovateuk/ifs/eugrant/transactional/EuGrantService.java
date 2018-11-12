package org.innovateuk.ifs.eugrant.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

/**
 * Service for saving and getting eu grant registrations.
 */
public interface EuGrantService {

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "EU_GRANT_ANONYMOUS_USER", description = "Anonymous users can submit grant registration")
    ServiceResult<Void> update(UUID id, EuGrantResource externalFundingResource);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "EU_GRANT_ANONYMOUS_USER", description = "Anonymous users can submit grant registration")
    ServiceResult<EuGrantResource> findById(UUID id);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "EU_GRANT_ANONYMOUS_USER", description = "Anonymous users can submit grant registration")
    ServiceResult<EuGrantResource> create();

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "EU_GRANT_ANONYMOUS_USER", description = "Anonymous users can submit grant registration")
    ServiceResult<EuGrantResource> submit(UUID uuid, boolean sendEmail);
}
