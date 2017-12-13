package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
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
    public boolean innovationLeadsCanViewMilestonesOnAssignedComps(CompetitionCompositeId competitionId, UserResource user) {
        return userIsInnovationLeadOnCompetition(competitionId.id(), user.getId());
    }

    @PermissionRule(value = "VIEW_MILESTONE", description = "Internal users (except innovation leads) can view milestones on any competition.")
    public boolean allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(CompetitionCompositeId competitionId, UserResource user) {
        return isInternal(user) && !isInnovationLead(user);
    }

    @PermissionRule(value = "VIEW_MILESTONE_BY_TYPE", description = "Internal users can view milestones, by type, on any competition.")
    public boolean allInternalUsersCanViewCompetitionMilestonesByType(CompetitionCompositeId competitionId, UserResource user) {
        return isInternal(user);
    }
}