package org.innovateuk.ifs.project.monitoring.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

@PermissionRules
@Component
public class MonitoringOfficerPermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "GET_MONITORING_OFFICER_PROJECTS",
            description = "Monitoring officers can get their own projects."
    )
    public boolean monitoringOfficerCanSeeTheirOwnProjects(UserResource monitoringOfficerUser, UserResource user) {
        return user.getId().equals(monitoringOfficerUser.getId()) && user.hasRole(Role.MONITORING_OFFICER);
    }

    @PermissionRule(
            value = "GET_MONITORING_OFFICER_PROJECTS",
            description = "Monitoring officers can get their own projects."
    )
    public boolean internalUsersCanSeeMonitoringOfficerProjects(UserResource monitoringOfficerUser, UserResource user) {
        return isInternal(user);
    }
}
