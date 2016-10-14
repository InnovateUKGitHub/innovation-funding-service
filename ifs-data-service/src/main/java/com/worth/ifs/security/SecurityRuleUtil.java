package com.worth.ifs.security;

import com.worth.ifs.commons.security.CustomPermissionEvaluator;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;

import static com.worth.ifs.user.resource.UserRoleType.*;

public class SecurityRuleUtil {

    public static boolean isCompAdmin(UserResource user) {
        return hasRole(user, COMP_ADMIN);
    }

    public static boolean isProjectFinanceUser(UserResource user) {
        return hasRole(user, PROJECT_FINANCE);
    }

    public static boolean isProjectPartnerUser(UserResource user) {
        return hasRole(user, APPLICANT);
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

    public static boolean isAnonymous(final UserResource user){
        return CustomPermissionEvaluator.isAnonymous(user);
    }

    public static UserResource getAnonymous(){
        return CustomPermissionEvaluator.getAnonymous();
    }

}