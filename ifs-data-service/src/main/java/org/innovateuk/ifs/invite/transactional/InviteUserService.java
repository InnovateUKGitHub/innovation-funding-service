package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.AdminRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around invites for users.
 */
public interface InviteUserService {

    @PreAuthorize("hasPermission(#invitedUser, 'SAVE_USER_INVITE')")
    ServiceResult<Void> saveUserInvite(UserResource invitedUser, AdminRoleType adminRoleType);
}
