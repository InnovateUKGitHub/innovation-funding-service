package com.worth.ifs.security;

import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SecurityRuleUtil {
    private static final Log LOG = LogFactory.getLog(SecurityRuleUtil.class);

    public static boolean isCompAdmin(UserResource user) {
        return user.getRoles().stream()
                .anyMatch(r -> UserRoleType.COMP_ADMIN.getName().equals(r.getName()));
    }
}