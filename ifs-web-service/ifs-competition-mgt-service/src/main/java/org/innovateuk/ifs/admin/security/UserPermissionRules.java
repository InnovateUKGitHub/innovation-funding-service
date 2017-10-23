package org.innovateuk.ifs.admin.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isIFSAdmin;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

/**
 * Permission checker around the access to user admin
 */
@PermissionRules
@Component
public class UserPermissionRules {

    @Autowired
    private UserService userService;

    @PermissionRule(value = "ACCESS_INTERNAL_USER", description = "Only internal users can be accessed")
    public boolean internalUser(Long userId, UserResource user) {
        UserResource editUser = userService.findById(userId);
        return isInternal(editUser) && isIFSAdmin(user);
    }

    @PermissionRule(value = "EDIT_INTERNAL_USER", description = "Only active, internal users can be edited")
    public boolean canEditInternalUser(Long userId, UserResource user) {
        UserResource editUser = userService.findById(userId);
        return editUser != null && UserStatus.ACTIVE.equals(editUser.getStatus()) && internalUser(userId, user);
    }
}
