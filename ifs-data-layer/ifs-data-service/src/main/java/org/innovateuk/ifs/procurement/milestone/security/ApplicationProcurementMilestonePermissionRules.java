package org.innovateuk.ifs.procurement.milestone.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD for ApplicationProcurementMilestones
 */
@Component
@PermissionRules
public class ApplicationProcurementMilestonePermissionRules extends BasePermissionRules {

    @Autowired
    private CompetitionRepository competitionRepository;

    @PermissionRule(value = "EDIT", description = "Applicants attached to applications can create")
    public boolean membersOfTheProjectTeamCanCRUDMilestones(ApplicationProcurementMilestoneResource applicationProcurementMilestone, UserResource user) {
        return isMemberOfProjectTeamForOrganisation(applicationProcurementMilestone.getApplicationId(), applicationProcurementMilestone.getOrganisationId(), user);
    }
}