package com.worth.ifs.security;

import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;

import java.util.List;

import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.resource.UserRoleType.SYSTEM_MAINTAINER;
import static com.worth.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;

public class SecurityRuleUtil {

    public static boolean isCompAdmin(UserResource user) {
        return hasRole(user, COMP_ADMIN);
    }

    public static boolean isSystemMaintenanceUser(UserResource user) {
        return hasRole(user, SYSTEM_MAINTAINER);
    }

    public static boolean isSystemRegistrationUser(UserResource user) {
        return hasRole(user, SYSTEM_REGISTRATION_USER);
    }

    private static boolean hasRole(UserResource user, UserRoleType type) {
        return user.hasRole(type);
    }


    public static boolean checkRole(final UserResource user,
                              final Long applicationId,
                              final Long organisationId,
                              final UserRoleType userRoleType,
                              final RoleRepository roleRepository,
                              final ProcessRoleRepository processRoleRepository) {
        final List<Role> roles = roleRepository.findByName(userRoleType.getName());
        final Role role = roles.get(0);
        final ProcessRole processRole = processRoleRepository.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(user.getId(), role.getId(), applicationId, organisationId);
        return processRole != null;
    }

    public static boolean checkRole(final UserResource user, final Long applicationId, UserRoleType userRoleType, final ProcessRoleRepository processRoleRepository) {
        final ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(), applicationId);
        return processRole != null && processRole.getRole().getName().equals(userRoleType.getName());
    }

    public static boolean isAnonymous(final UserResource user){
        return CustomPermissionEvaluator.isAnonymous(user);
    }

}