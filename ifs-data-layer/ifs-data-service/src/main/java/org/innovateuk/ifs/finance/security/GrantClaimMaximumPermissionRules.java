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

/**
 * Permission rules for {@link GrantClaimMaximum} for permissioning
 */
@Component
@PermissionRules
public class GrantClaimMaximumPermissionRules extends BasePermissionRules {

    @Autowired
    private UsersRolesService usersRolesService;

    @PermissionRule(value = "MAX_FUNDING_LEVEL_OVERRIDDEN",
            description = "A user can see the grant claim maximums for if they have an application for the competition")
    public boolean usersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden(CompetitionResource competition,
                                                                                       UserResource user) {
        return usersRolesService.userHasApplicationForCompetition(user.getId(), competition.getId())
                .getSuccess();
    }
}
