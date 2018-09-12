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

/**
 * Permission rules for {@link GrantClaimMaximum} for permissioning
 */
@Component
@PermissionRules
public class GrantClaimMaximumPermissionRules extends BasePermissionRules {

    @Autowired
    private UsersRolesService usersRolesService;

    @PermissionRule(value = "MAX_FUNDING_LEVEL_OVERRIDDEN",
            description = "A user can see the grant claim maximums if they are an internal admin user or they have an" +
                    " application for the competition")
    public boolean internalAdminAndUsersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden
            (CompetitionResource competition, UserResource user) {
        return isInternalAdmin(user) ||
                usersRolesService.userHasApplicationForCompetition(user.getId(), competition.getId()).getSuccess();
    }
}
