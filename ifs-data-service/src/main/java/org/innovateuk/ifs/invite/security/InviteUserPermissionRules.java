package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.stereotype.Component;

/**
 * Permission rules for Invite User Service
 */
@Component
@PermissionRules
public class InviteUserPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "SAVE_USER_INVITE", description = "Only an IFS Administrator can save a new user invite")
    public boolean ifsAdminCanSaveNewUserInvite(final UserResource invitedUser, UserResource user) {
        return user.hasRole(UserRoleType.IFS_ADMINISTRATOR);
    }
}
