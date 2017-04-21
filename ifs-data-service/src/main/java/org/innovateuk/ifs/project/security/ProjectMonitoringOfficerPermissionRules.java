package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.isInternal;

@PermissionRules
@Component
public class ProjectMonitoringOfficerPermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "VIEW_MONITORING_OFFICER",
            description = "Internal users can view Monitoring Officers on any Project")
    public boolean internalUsersCanViewMonitoringOfficersForAnyProject(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "VIEW_MONITORING_OFFICER",
            description = "Partners can view monitoring officers on Projects that they are partners on")
    public boolean partnersCanViewMonitoringOfficersOnTheirProjects(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "ASSIGN_MONITORING_OFFICER",
            description = "Internal users can assign Monitoring Officers on any Project")
    public boolean internalUsersCanAssignMonitoringOfficersForAnyProject(ProjectResource project, UserResource user) {
        return isInternal(user);
    }
}
