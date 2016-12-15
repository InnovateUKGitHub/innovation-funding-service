package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.CustomPermissionEvaluator;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

import static org.innovateuk.ifs.user.resource.UserRoleType.*;

public class SecurityRuleUtil {

    public static boolean isCompAdmin(UserResource user) {
        return hasRole(user, COMP_ADMIN);
    }

    private boolean isInternal(UserResource user) {
        return hasRole(user, UserRoleType.COMP_ADMIN) || hasRole(user, UserRoleType.PROJECT_FINANCE);
    }

    public static boolean isProjectFinanceUser(UserResource user) {
        return hasRole(user, PROJECT_FINANCE);
    }

    public static boolean isSystemMaintenanceUser(UserResource user) {
        return hasRole(user, SYSTEM_MAINTAINER);
    }

    public static boolean isSystemRegistrationUser(UserResource user) {
        return hasRole(user, SYSTEM_REGISTRATION_USER);
    }

    public static boolean isAssessor(UserResource user) {
        return hasRole(user, ASSESSOR);
    }

    public static boolean isCompExec(UserResource user) {
        return hasRole(user, COMP_EXEC);
    }

    private static boolean hasRole(UserResource user, UserRoleType type) {
        return user.hasRole(type);
    }


    public static boolean checkProcessRole(final UserResource user,
                                           final Long applicationId,
                                           final Long organisationId,
                                           final UserRoleType userRoleType,
                                           final RoleRepository roleRepository,
                                           final ProcessRoleRepository processRoleRepository) {
        final Role role = roleRepository.findOneByName(userRoleType.getName());
        final ProcessRole processRole = processRoleRepository.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(user.getId(), role.getId(), applicationId, organisationId);
        return processRole != null;
    }

    public static boolean checkProcessRole(final UserResource user, final Long applicationId, UserRoleType userRoleType, final ProcessRoleRepository processRoleRepository) {
        final ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(), applicationId);
        return processRole != null && processRole.getRole().getName().equals(userRoleType.getName());
    }

    public static boolean isAnonymous(final UserResource user) {
        return CustomPermissionEvaluator.isAnonymous(user);
    }

    public static UserResource getAnonymous() {
        return CustomPermissionEvaluator.getAnonymous();
    }

}
