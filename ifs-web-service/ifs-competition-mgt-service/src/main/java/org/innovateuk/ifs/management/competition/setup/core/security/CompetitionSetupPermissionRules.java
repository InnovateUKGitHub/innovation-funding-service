package org.innovateuk.ifs.management.competition.setup.core.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Permission checker around the access to Competition Setup
 */
@PermissionRules
@Component
public class CompetitionSetupPermissionRules {

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @PermissionRule(value = "MANAGE_INNOVATION_LEAD", description = "Allowed to manage innovation leads")
    public boolean manageInnovationLead(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        return competitionInitialDetailsSet(competitionCompositeId);
    }

    @PermissionRule(value = "MANAGE_STAKEHOLDERS", description = "Allowed to manage stakeholders")
    public boolean manageStakeholders(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        return competitionInitialDetailsSet(competitionCompositeId);
    }

    @PermissionRule(value = "MANAGE_COMP_FINANCE_USERS", description = "Allowed to manage competition finance")
    public boolean manageCompetitionFinance(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        return competitionInitialDetailsSet(competitionCompositeId);
    }

    @PermissionRule(value = "CHOOSE_POST_AWARD_SERVICE", description = "Allowed to choose post award service")
    public boolean choosePostAwardService(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        return competitionInitialDetailsSet(competitionCompositeId);
    }

    private boolean competitionInitialDetailsSet(CompetitionCompositeId competitionCompositeId) {
        return competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionCompositeId.id());
    }
}
