package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.evaluator.DefaultPermissionMethodHandler;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.resource.Role.*;

public final class SecurityRuleUtil {

    private SecurityRuleUtil() {}

    public static boolean hasProjectFinanceAuthority(User user) {
        return user.hasAuthority(Authority.PROJECT_FINANCE);
    }

    public static boolean isSupport(User user) { return user.hasRole(SUPPORT); }

    public static boolean checkProcessRole(final UserResource user,
                                           final Long applicationId,
                                           final Long organisationId,
                                           final ProcessRoleType userRoleType,
                                           final ProcessRoleRepository processRoleRepository) {
        return processRoleRepository.existsByUserIdAndRoleAndApplicationIdAndOrganisationId(user.getId(), userRoleType, applicationId, organisationId);
    }

    public static boolean checkHasAnyProcessRole(final UserResource user,
                                                 final Long applicationId,
                                                 final Long organisationId,
                                                 final ProcessRoleRepository processRoleRepository,
                                                 final ProcessRoleType... roles) {
        return processRoleRepository.existsByUserIdAndRoleInAndApplicationIdAndOrganisationId(user.getId(), new HashSet<>(asList(roles)), applicationId, organisationId);
    }

    public static boolean checkHasAnyProcessRole(final UserResource user,
                                                 final Long applicationId,
                                                 final ProcessRoleRepository processRoleRepository,
                                                 final ProcessRoleType... roles) {
        return processRoleRepository.existsByUserIdAndRoleInAndApplicationId(user.getId(), new HashSet<>(asList(roles)), applicationId);
    }

    public static boolean checkProcessRole(final UserResource user, final long applicationId, ProcessRoleType userRoleType, final ProcessRoleRepository processRoleRepository) {
        return user.getId() != null && processRoleRepository.existsByUserIdAndApplicationIdAndRole(user.getId(), applicationId, userRoleType);
    }

    public static boolean checkHasAnyProcessRole(final UserResource user,
                                                 final Long applicationId,
                                                 final ProcessRoleRepository processRoleRepository) {
        return processRoleRepository.existsByUserIdAndApplicationId(user.getId(), applicationId);
    }


    public static boolean isAnonymous(final UserResource user) {
        return DefaultPermissionMethodHandler.isAnonymous(user);
    }

    public static UserResource getAnonymous() {
        return DefaultPermissionMethodHandler.getAnonymous();
    }

    public static boolean isInnovationLead(User user) { return user.hasRole(INNOVATION_LEAD); }

    public static boolean isStakeholder(User user) { return user.hasRole(STAKEHOLDER); }

    public static boolean isCompetitionFinance(User user) { return user.hasRole(EXTERNAL_FINANCE); }
}
