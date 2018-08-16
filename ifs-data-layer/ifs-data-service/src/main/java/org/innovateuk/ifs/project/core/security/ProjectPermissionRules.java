package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isSystemRegistrationUser;

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

    //TODO - This needs to be changed to see that the MO belongs to the project. To do this see the isPartner() method in the method above it.
    @PermissionRule(value = "READ", description = "A user can see projects that they are partners on")
    public boolean monitoringOfficerOnProjectCanView(final ProjectResource project, final UserResource user) {
        return project != null && isMonitoringOfficer(project.getId(), user.getId());
        //return user.hasRole(Role.MONITORING_OFFICER);
    }

    @PermissionRule(value = "ADD_PARTNER", description = "The System Registration user can add a partner to a project")
    public boolean systemRegistrarCanAddPartnersToProject(final ProjectResource project, final UserResource user) {
        return isSystemRegistrationUser(user)
                && isProjectInSetup(project.getId());
    }
}
