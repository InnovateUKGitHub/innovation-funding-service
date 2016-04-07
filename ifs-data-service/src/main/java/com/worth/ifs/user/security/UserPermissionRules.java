package com.worth.ifs.user.security;

import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.user.domain.UserRoleType.SYSTEM_REGISTRATION_USER;

/**
 * Permission rules that determines who can perform CRUD operations based around Users.
 */
@Component
@PermissionRules
public class UserPermissionRules {

    @PermissionRule(value = "CREATE", description = "A System User can create new Users")
    public boolean systemUserCanCreateUsers(UserResource userToCreate, UserResource user) {
        return user.hasRole(SYSTEM_REGISTRATION_USER);
    }

}
