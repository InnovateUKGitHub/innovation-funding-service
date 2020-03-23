package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

/**
 * Permission rules that determines who can perform CRUD operations based around RoleProfileStatus.
 */
@Component
@PermissionRules
public class RoleProfileStatusPermissionRoles {

    @PermissionRule(value = "RETRIEVE_USER_ROLE_PROFILE", description = "Comp admin, project finance or support can retrieve a users status")
    public boolean adminsAndSupportCanRetrieveUserRoleProfile(UserResource userToCheck, UserResource user) {
        return isInternalAdmin(user) || isSupport(user);
    }

    @PermissionRule(value = "RETRIEVE_USER_ROLE_PROFILE", description = "Assessors can retrieve their own status")
    public boolean usersCanRetrieveTheirOwnUserRoleProfile(UserResource userToCheck, UserResource user) {
        return userToCheck.equals(user);
    }
}
