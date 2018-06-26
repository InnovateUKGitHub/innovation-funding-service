package org.innovateuk.ifs.util;

import org.innovateuk.ifs.user.domain.User;

import static org.innovateuk.ifs.user.resource.Role.*;

public final class SecurityRuleUtil {

    private SecurityRuleUtil() {}

    public static boolean isCompAdmin(User user) {
        return user.hasRole(COMP_ADMIN);
    }

    public static boolean isInternal(User user) {
        return user.isInternalUser();
    }

    public static boolean isInternalAdmin(User user) {
        return user.hasRole(COMP_ADMIN) ||
                user.hasRole(PROJECT_FINANCE);
    }

    public static boolean isProjectFinanceUser(User user) {
        return user.hasRole(PROJECT_FINANCE);
    }

    public static boolean isSystemMaintenanceUser(User user) {
        return user.hasRole(SYSTEM_MAINTAINER);
    }

    public static boolean isSystemRegistrationUser(User user) {
        return user.hasRole(SYSTEM_REGISTRATION_USER);
    }

    public static boolean isAssessor(User user) {
        return user.hasRole(ASSESSOR);
    }

    public static boolean isSupport(User user) {
        return user.hasRole(SUPPORT); }

    public static boolean isInnovationLead(User user) {
        return user.hasRole(INNOVATION_LEAD); }

    public static boolean isIFSAdmin(User user) {
        return user.hasRole(IFS_ADMINISTRATOR); }
}
