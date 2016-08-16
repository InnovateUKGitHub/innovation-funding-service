package com.worth.ifs.competition.security;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD for Competitions
 */
@Component
@PermissionRules
public class CompetitionPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "Anyone can view finished competitions. Only comp admin can see competitions in setup")
    public boolean anyoneCanViewOpenCompetitions(CompetitionResource competition, UserResource user) {
        if (!CompetitionResource.Status.COMPETITION_SETUP.equals(competition.getCompetitionStatus())) {
            return true;
        } else {
            return user != null && user.hasRole(UserRoleType.COMP_ADMIN);
        }

    }
}
