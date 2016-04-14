package com.worth.ifs.security;

import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.resource.UserResource;

public class SecurityRuleUtil {
    public static boolean isCompAdmin(UserResource user) {
        return user.getRoles().stream()
                .anyMatch(r -> UserRoleType.COMP_ADMIN.getName().equals(r.getName()));
    }
}