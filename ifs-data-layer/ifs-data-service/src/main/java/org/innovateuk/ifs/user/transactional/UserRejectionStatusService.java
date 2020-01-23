package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserRejection;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.user.domain.UserRejectionStatus} data.
 */
public interface UserRejectionStatusService {

    @SecuredBySpring(value = "UPDATE_USER_STATUS", description = "Only comp admin, project finance or IFS admin can update a users status")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> updateUserStatus(long userId, UserRejection userRejection);
}
