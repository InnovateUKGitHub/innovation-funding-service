package org.innovateuk.ifs.competitionsetup.security;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
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
    private CompetitionService competitionService;

    @PermissionRule(value = "MANAGE_INNOVATION_LEAD", description = "Allowed to manage innovation leads")
    public boolean manageInnovationLead(Long competitionId, UserResource loggedInUser) {
        return competitionInitialDetailsSet(competitionId);
    }

    private boolean competitionInitialDetailsSet(Long competitionId) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        return competitionResource.isInitialDetailsComplete();
    }
}
