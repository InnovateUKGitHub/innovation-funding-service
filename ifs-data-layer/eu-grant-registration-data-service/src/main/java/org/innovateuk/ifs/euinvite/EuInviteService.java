package org.innovateuk.ifs.euinvite;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for sending out invites to eu registrants
 */
public interface EuInviteService {

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "EU_INVITE_ANONYMOUS_USER", description = "The system registrar can create an invite for an eu registrant")
    ServiceResult<Void> sendInvites(List<Long> euContactIds);
}
