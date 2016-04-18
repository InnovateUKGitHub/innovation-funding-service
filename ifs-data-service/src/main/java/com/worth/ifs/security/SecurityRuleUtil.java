package com.worth.ifs.security;

import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.resource.UserResource;

import static com.worth.ifs.user.domain.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.domain.UserRoleType.SYSTEM_REGISTRATION_USER;

public class SecurityRuleUtil {

    public static boolean isCompAdmin(UserResource user) {
        return hasRole(user, COMP_ADMIN);
    }

    public static boolean isSystemRegistrationUser(UserResource user) {
        return hasRole(user, SYSTEM_REGISTRATION_USER);
    }

    private static boolean hasRole(UserResource user, UserRoleType type) {
        return user.hasRole(type);
    }
}