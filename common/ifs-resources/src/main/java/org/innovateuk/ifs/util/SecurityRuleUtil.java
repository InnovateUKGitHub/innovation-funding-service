package org.innovateuk.ifs.util;

import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.user.resource.Role.*;

public final class SecurityRuleUtil {

    private SecurityRuleUtil() {}

    public static boolean isCompAdmin(UserResource user) {
        return user.hasRole(COMP_ADMIN);
    }

    public static boolean isInternal(UserResource user) {
        return user.isInternalUser();
    }

    public static boolean isInternalAdmin(UserResource user) {
        return user.hasAuthority(Authority.COMP_ADMIN);
    }

    public static boolean hasProjectFinanceAuthority(UserResource user) {
        return user.hasAuthority(Authority.PROJECT_FINANCE);
    }

    public static boolean isSystemMaintenanceUser(UserResource user) {
        return user.hasRole(SYSTEM_MAINTAINER);
    }

    public static boolean isSystemRegistrationUser(UserResource user) {
        return user.hasRole(SYSTEM_REGISTRATION_USER);
    }

    public static boolean hasAssessorAuthority(UserResource user) {
        return user.hasAuthority(Authority.ASSESSOR);
    }

    public static boolean hasIFSAdminAuthority(UserResource user) {
        return user.hasAuthority(Authority.IFS_ADMINISTRATOR);
    }

    public static boolean hasStakeholderAuthority(UserResource user) {
        return user.hasAuthority(Authority.STAKEHOLDER);
    }

    public static boolean hasSuperAdminAuthority(UserResource user) {
        return user.hasAuthority(Authority.SUPER_ADMIN_USER);
    }

    public static boolean hasAuditorAuthority(UserResource user) {
        return user.hasAuthority(Authority.AUDITOR);
    }

    public static boolean isSupport(UserResource user) {
        return user.hasRole(SUPPORT); }

    public static boolean isInnovationLead(UserResource user) {
        return user.hasRole(INNOVATION_LEAD); }

    public static boolean isStakeholder(UserResource user) {
        return user.hasRole(STAKEHOLDER);
    }

    public static boolean isAuditor(UserResource user) {
        return user.hasRole(AUDITOR);
    }

    public static boolean isExternalFinanceUser(UserResource user) {
        return user.hasRole(EXTERNAL_FINANCE);
    }

    public static boolean isMonitoringOfficer(UserResource user) {
        return user.hasRole(MONITORING_OFFICER);
    }

    public static boolean isKta(UserResource user) {
        return user.hasRole(KNOWLEDGE_TRANSFER_ADVISER);
    }

    public static boolean isSupporter(UserResource user) {
        return user.hasRole(SUPPORTER);
    }
}