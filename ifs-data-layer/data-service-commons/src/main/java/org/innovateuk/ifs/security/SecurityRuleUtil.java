package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.evaluator.DefaultPermissionMethodHandler;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

import static org.innovateuk.ifs.user.resource.UserRoleType.*;

public class SecurityRuleUtil {

    public static boolean isInternal(User user) {
        return user.hasRole(UserRoleType.COMP_ADMIN) || user.hasRole(UserRoleType.PROJECT_FINANCE) || user.hasRole(UserRoleType.SUPPORT) || user.hasRole(UserRoleType.INNOVATION_LEAD);
    }

    public static boolean isProjectFinanceUser(User user) {
        return user.hasRole(PROJECT_FINANCE);
    }

    public static boolean isSupport(User user) { return user.hasRole(SUPPORT); }

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

    public static boolean checkProcessRole(final UserResource user, final long applicationId, UserRoleType userRoleType, final ProcessRoleRepository processRoleRepository, final RoleRepository roleRepository) {
        return processRoleRepository.existsByUserIdAndApplicationIdAndRoleName(user.getId(), applicationId, userRoleType.getName());
    }

    public static boolean isAnonymous(final UserResource user) {
        return DefaultPermissionMethodHandler.isAnonymous(user);
    }

    public static UserResource getAnonymous() {
        return DefaultPermissionMethodHandler.getAnonymous();
    }

    public static boolean isInnovationLead(User user) { return user.hasRole(INNOVATION_LEAD); }
}
