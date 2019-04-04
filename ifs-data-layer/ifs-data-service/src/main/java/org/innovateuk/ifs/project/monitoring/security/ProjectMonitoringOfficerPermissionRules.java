package org.innovateuk.ifs.project.monitoring.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.SecurityRuleUtil;
import org.springframework.stereotype.Component;

@PermissionRules
@Component
public class ProjectMonitoringOfficerPermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "CHECK_MONITORING_OFFICER",
            description = "Users can check if they are a monitoring officer on a project")
    public boolean usersCanCheckIfTheyAreMonitoringOfficerOnProject(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId()) || SecurityRuleUtil.isMonitoringOfficer(user);
    }

}
