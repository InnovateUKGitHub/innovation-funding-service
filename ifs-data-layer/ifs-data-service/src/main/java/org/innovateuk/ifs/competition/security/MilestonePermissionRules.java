package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.COMPETITION_SETUP;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

/**
 * Provides the permissions around CRUD for Milestones
 */
@Component
@PermissionRules
public class MilestonePermissionRules extends BasePermissionRules {

    @Autowired
    private CompetitionRepository competitionRepository;

    @PermissionRule(value = "VIEW_MILESTONE", description = "Innovation lead users can view milestones on competitions assigned to them.")
    public boolean innovationLeadsCanViewMilestonesOnAssignedComps(CompetitionCompositeId competitionId, UserResource user) {
        return userIsInnovationLeadOnCompetition(competitionId.id(), user.getId());
    }

    @PermissionRule(value = "VIEW_MILESTONE", description = "Stakeholders can view milestones on competitions assigned to them.")
    public boolean stakeholdersCanViewMilestonesOnAssignedComps(CompetitionCompositeId competitionId, UserResource user) {
        return userIsStakeholderInCompetition(competitionId.id(), user.getId());
    }

    @PermissionRule(value = "VIEW_MILESTONE", description = "Internal users (except innovation leads and stakeholders) can view milestones on any competition.")
    public boolean allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(CompetitionCompositeId competitionId, UserResource user) {
        return isInternal(user) && !isInnovationLead(user) && !isStakeholder(user);
    }

    @PermissionRule(value = "VIEW_MILESTONE_BY_TYPE", description = "Internal users can view milestones, by type, on any competition.")
    public boolean allInternalUsersCanViewCompetitionMilestonesByType(CompetitionCompositeId competitionId, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "UPDATE_COMPLETION_STAGE", description = "Comp admins and Project Finance users can update the " +
            "Completion stage of a Competition during Competition Setup but not after it is live")
    public boolean compAdminsAndProjectFinanceUserCanUpdateCompletionStageDuringCompetitionSetup(
            CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {

        if (!(loggedInUser.hasAnyRoles(COMP_ADMIN, PROJECT_FINANCE))) {
            return false;
        }

        Competition competition = competitionRepository.findById(competitionCompositeId.id()).get();
        return COMPETITION_SETUP.equals(competition.getCompetitionStatus());
    }
}