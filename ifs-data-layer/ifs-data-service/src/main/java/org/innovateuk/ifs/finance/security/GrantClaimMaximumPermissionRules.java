package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isProjectFinanceUser;

/**
 * Permission rules for {@link GrantClaimMaximum} for permissioning
 */
@Component
@PermissionRules
public class GrantClaimMaximumPermissionRules extends BasePermissionRules {

    @Autowired
    private UsersRolesService usersRolesService;

    @PermissionRule(value = "MAX_FUNDING_LEVEL_OVERRIDDEN",
            description = "A user can see the grant claim maximums if they are an internal admin user")
    public boolean internalAdminCanCheckMaxFundingLevelOverridden
            (CompetitionResource competition, UserResource user) {
        return isInternalAdmin(user) || isProjectFinanceUser(user);
    }

    @PermissionRule(value = "MAX_FUNDING_LEVEL_OVERRIDDEN",
        description = "A user can see the grant claim maximums if they have an application for the competition")
    public boolean usersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden
        (CompetitionResource competition, UserResource user) {
        return usersRolesService.userHasApplicationForCompetition(user.getId(), competition.getId()).getSuccess();
    }

    @PermissionRule(value = "MAX_FUNDING_LEVEL_OVERRIDDEN",
        description = "A user can see the grant claim maximums if they are in a project for the competition")
    public boolean userInAProjectCanCheckMaxFundingLevelOverridden
        (CompetitionResource competition, UserResource user) {
        return projectUserRepository.existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId());
    }
}