package org.innovateuk.ifs.competitionsetup.security;

import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.springframework.stereotype.Component;

/**
 * Permission rules for Stakeholder Service
 */
@Component
@PermissionRules
//TODO - XXX - Delete this class
public class StakeholderPermissionRules extends BasePermissionRules {

/*    @PermissionRule(value = "SAVE_STAKEHOLDER_INVITE", description = "A comp admin can save a stakeholder invite")
    public boolean compAdminCanSaveNewStakeholderInvite(final UserResource invitedUser, UserResource user) {
        return user.hasRole(COMP_ADMIN);
    }

    @PermissionRule(value = "SAVE_STAKEHOLDER_INVITE", description = "A project finance can save a stakeholder invite")
    public boolean projectFinanceCanSaveNewStakeholderInvite(final UserResource invitedUser, UserResource user) {
        return user.hasRole(PROJECT_FINANCE);
    }

    @PermissionRule(value = "SAVE_STAKEHOLDER_INVITE", description = "An ifs admin can save a stakeholder invite")
    public boolean ifsAdminCanSaveNewStakeholderInvite(final UserResource invitedUser, UserResource user) {
        return user.hasRole(IFS_ADMINISTRATOR);
    }*/
}

