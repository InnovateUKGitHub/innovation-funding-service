package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.PermissionMethodHandler;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

import static org.innovateuk.ifs.user.resource.UserRoleType.*;

public class SecurityRuleUtil {

    public static boolean isCompAdmin(UserResource user) {
        return user.hasRole( COMP_ADMIN);
    }


    public static boolean isInternal(User user) {
        return user.hasRole(UserRoleType.COMP_ADMIN) || user.hasRole(UserRoleType.PROJECT_FINANCE);
    }

    public static boolean isInternal(UserResource user) {
        return user.hasRole(UserRoleType.COMP_ADMIN) || user.hasRole(UserRoleType.PROJECT_FINANCE);
    }

    public static boolean isProjectFinanceUser(UserResource user) {
        return user.hasRole(PROJECT_FINANCE);
    }

    public static boolean isProjectFinanceUser(User user) {
        return user.hasRole(PROJECT_FINANCE);
    }

    public static boolean isSystemMaintenanceUser(UserResource user) {
        return user.hasRole( SYSTEM_MAINTAINER);
    }

    public static boolean isSystemRegistrationUser(UserResource user) {
        return user.hasRole( SYSTEM_REGISTRATION_USER);
    }

    public static boolean isAssessor(UserResource user) {
        return user.hasRole( ASSESSOR);
    }

    public static boolean checkProcessRole(final UserResource user,
                                           final Long applicationId,
                                           final Long organisationId,
                                           final UserRoleType userRoleType,
                                           final RoleRepository roleRepository,
                                           final ProcessRoleRepository processRoleRepository)
    {
        final Role role = roleRepository.findOneByName(userRoleType.getName());
        final ProcessRole processRole = processRoleRepository.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(user.getId(), role.getId(), applicationId, organisationId);
        return processRole != null;
    }

    public static boolean checkProcessRole(final UserResource user, final Long applicationId, UserRoleType userRoleType, final ProcessRoleRepository processRoleRepository) {
        final ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(), applicationId);
        return processRole != null && processRole.getRole().getName().equals(userRoleType.getName());
    }

    public static boolean isAnonymous(final UserResource user) {
        return PermissionMethodHandler.isAnonymous(user);
    }

    public static UserResource getAnonymous() {
        return PermissionMethodHandler.getAnonymous();
    }

}
