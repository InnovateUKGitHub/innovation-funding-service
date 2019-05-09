package org.innovateuk.ifs.project.projectteam.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@Component
@PermissionRules
public class ProjectTeamPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "REMOVE_PROJECT_USER", description = "A user can remove users from their own organisation on a project")
    public boolean partnersOnProjectCanRemoveUsersFromTheirOrganisation(final ProjectUserCompositeId composite, UserResource user) {
        return isProjectInSetup(composite.getProjectId())
                && isSameProjectOrganisation(composite.getProjectId(), composite.getUserId(), user.getId());
    }
}
