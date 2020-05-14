package org.innovateuk.ifs.project.monitoring.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

@PermissionRules
@Component
public class MonitoringOfficerPermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "GET_MONITORING_OFFICER_PROJECTS",
            description = "Monitoring officers can get their own projects."
    )
    public boolean monitoringOfficerCanSeeTheirOwnProjects(UserResource monitoringOfficerUser, UserResource user) {
        return user.getId().equals(monitoringOfficerUser.getId()) && user.hasRole(MONITORING_OFFICER);
    }

    @PermissionRule(
            value = "GET_MONITORING_OFFICER_PROJECTS",
            description = "Internal users can view monitoring officer projects."
    )
    public boolean internalUsersCanSeeMonitoringOfficerProjects(UserResource monitoringOfficerUser, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "VIEW_MONITORING_OFFICER",
            description = "Internal users can view Monitoring Officers on any Project")
    public boolean internalUsersCanViewMonitoringOfficersForAnyProject(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "VIEW_MONITORING_OFFICER",
            description = "Stakeholders can view Monitoring Officers on any Project in their competitions")
    public boolean stakeholdersCanViewMonitoringOfficersForAProjectOnTheirCompetitions(ProjectResource project, UserResource user) {
        return userIsStakeholderInCompetition(project.getCompetition(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_MONITORING_OFFICER",
            description = "Competition finance user can view Monitoring Officers on any Project in their competitions")
    public boolean competitionFinanceUsersCanViewMonitoringOfficersForAProjectOnTheirCompetitions(ProjectResource project, UserResource user) {
        return userIsExternalFinanceInCompetition(project.getCompetition(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_MONITORING_OFFICER",
            description = "Monitoring Officers can view themselves on any Project")
    public boolean monitoringOfficersCanViewThemselves(ProjectResource project, UserResource user) {
        return isMonitoringOfficer(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_MONITORING_OFFICER",
            description = "Partners can view Monitoring Officers on Projects that they are partners on")
    public boolean partnersCanViewMonitoringOfficersOnTheirProjects(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }
}
