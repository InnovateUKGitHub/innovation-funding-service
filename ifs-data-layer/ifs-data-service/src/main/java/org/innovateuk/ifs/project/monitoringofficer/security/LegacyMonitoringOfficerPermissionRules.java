package org.innovateuk.ifs.project.monitoringofficer.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

@PermissionRules
@Component
public class LegacyMonitoringOfficerPermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "ASSIGN_MONITORING_OFFICER",
            description = "Internal users can assign Monitoring Officers on any Project")
    public boolean internalUsersCanAssignMonitoringOfficersForAnyProject(ProjectResource project, UserResource user) {
        return isInternal(user) && isProjectActive(project.getId());
    }
}
