package org.innovateuk.ifs.admin.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Permission checker around the access to user admin
 */
@PermissionRules
@Component
public class UserPermissionRules {

    @Autowired
    private UserService userService;

    @PermissionRule(value = "ACCESS_USER_EDIT", description = "Only active users can be edited")
    public boolean activeUsersCanBeEdited(Long userId, UserResource user) {
        UserResource editUser = userService.findById(userId);
        return user != null && UserStatus.ACTIVE.equals(editUser.getStatus());
    }

    // TODO: review when IFS-1370 is implemented - RB
    // IFS-644 ideally use SecurityRuleUtil.isInternal() but this would mean moving a lot of classes into commons
    @PermissionRule(value = "ACCESS_INTERNAL_USER", description = "Only internal users can be accessed")
    public boolean internalUser(Long userId, UserResource user) {
        UserResource editUser = userService.findById(userId);
        return editUser.hasRole(UserRoleType.COMP_ADMIN) || editUser.hasRole(UserRoleType.PROJECT_FINANCE) || editUser.hasRole(UserRoleType.INNOVATION_LEAD) || editUser.hasRole(UserRoleType.SUPPORT);
    }

    @PermissionRule(value = "EDIT_INTERNAL_USER", description = "Only active, internal users can be edited")
    public boolean canEditInternalUser(Long userId, UserResource user) {
        return activeUsersCanBeEdited(userId, user) && internalUser(userId, user);
    }
}
