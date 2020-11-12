package org.innovateuk.ifs.application.creation.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@PermissionRules
@Component
public class ApplicationCreationPermissionRules {

    @PermissionRule(value = "APPLICATION_CREATION", description = "Users such as assessor, stakeholder, monitoring_officer, live_projects_user and " +
            "supporter can create application")
    public boolean applicationCreationAuthentication(CompetitionCompositeId competitionCompositeId, UserResource user) {
          return user.hasAnyRoles(Role.multiDashboardRoles());
    }

}
