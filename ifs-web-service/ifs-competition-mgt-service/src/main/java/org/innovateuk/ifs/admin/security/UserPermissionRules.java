package org.innovateuk.ifs.admin.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
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
    UserService userService;
    @PermissionRule(value = "ACCESS_USER_EDIT", description = "Only active users can be edited")
    public boolean activeUsersCanBeEdited(Long userId, UserResource user) {
        UserResource editUser = userService.findById(userId);
        return user != null ? UserStatus.ACTIVE.equals(editUser.getStatus()): false;
    }
}
