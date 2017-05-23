package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.*;

@PermissionRules
@Component
public class ProjectPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "A user can see projects that they are partners on")
    public boolean partnersOnProjectCanView(ProjectResource project, UserResource user) {
        return project != null && isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Internal users can see project resources")
    public boolean internalUsersCanViewProjects(final ProjectResource project, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "VIEW_TEAM_STATUS",
            description = "All partners can view team status")
    public boolean partnersCanViewTeamStatus(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_TEAM_STATUS",
            description = "Internal users can see a team's status")
    public boolean internalUsersCanViewTeamStatus(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "VIEW_STATUS",
            description = "All partners can view the project status")
    public boolean partnersCanViewStatus(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_STATUS",
            description = "Internal users can see the project status")
    public boolean internalUsersCanViewStatus(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

}
