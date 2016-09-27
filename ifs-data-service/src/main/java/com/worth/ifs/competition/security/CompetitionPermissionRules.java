package com.worth.ifs.competition.security;

import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;
import static com.worth.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

/**
 * Provides the permissions around CRUD for Competitions
 */
@Component
@PermissionRules
public class CompetitionPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "External users cannot view competitions in setup")
    public boolean externalUsersCannotViewCompetitionsInSetup(CompetitionResource competition, UserResource user) {
        return !CompetitionResource.Status.COMPETITION_SETUP.equals(competition.getCompetitionStatus());
    }

    @PermissionRule(value = "READ", description = "Competition Admininstrators can see all competitions")
    public boolean compAdminUserCanViewOpenCompetitions(CompetitionResource competition, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(value = "READ", description = "Project finance users can see all competitions")
    public boolean projectFinanceUserCanViewOpenCompetitions(CompetitionResource competition, UserResource user) {
        return isProjectFinanceUser(user);
    }
}
