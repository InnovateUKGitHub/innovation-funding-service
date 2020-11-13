package org.innovateuk.ifs.application.creation.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PermissionRules
@Component
public class ApplicationCreationPermissionRules {

    @PermissionRule(value = "APPLICATION_CREATION", description = "Users such as assessor, stakeholder, monitoring_officer, live_projects_user and " +
            "supporter can create application")
    public boolean applicationCreationAuthentication(long competitionId, UserResource user) {
             return user.hasAnyRoles(Role.multiDashboardRoles());
    }

    @PermissionRule(value = "APPLICATION_CREATION", description = "Users such as assessor, stakeholder, monitoring_officer, live_projects_user and " +
            "supporter can accept invite and select organisation to create application")
    public boolean acceptInviteAndSelectOrganisationAuthentication(UserResource userToAccept, UserResource user) {
        return user.hasAnyRoles(Role.multiDashboardRoles());
    }

}
