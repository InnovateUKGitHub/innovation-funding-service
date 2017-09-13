package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInnovationLead;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

/**
 * Provides the permissions around CRUD for Milestones
 */
@Component
@PermissionRules
public class MilestonePermissionRules extends BasePermissionRules {

    @PermissionRule(value = "VIEW_MILESTONE", description = "Innovation lead users can view milestones on competitions assigned to them.")
    public boolean innovationLeadsCanViewMilestonesOnAssginedComps(Long competitionId, UserResource user) {
        return userIsInnovationLeadOnCompetition(competitionId, user.getId());
    }

    @PermissionRule(value = "VIEW_MILESTONE", description = "Internal users (except innovation leads) can view milestones on any competition.")
    public boolean allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(Long competitionId, UserResource user) {
        return isInternal(user) && !isInnovationLead(user);
    }
}