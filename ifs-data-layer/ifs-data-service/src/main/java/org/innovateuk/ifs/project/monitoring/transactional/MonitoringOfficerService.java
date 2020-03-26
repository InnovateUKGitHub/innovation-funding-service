package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignmentResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static org.innovateuk.ifs.activitylog.resource.ActivityType.MONITORING_OFFICER_ASSIGNED;

public interface MonitoringOfficerService {

    @SecuredBySpring(value = "GET_MONITORING_OFFICERS",
            description = "Only comp admin, project finance and ifs administrators can get a project list of monitoring officers")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<List<SimpleUserResource>> findAll();

    @SecuredBySpring(value = "GET_MONITORING_OFFICER",
            description = "Only comp admin, project finance and ifs administrators can get a project monitoring officer")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<MonitoringOfficerAssignmentResource> getProjectMonitoringOfficer(long userId);

    @SecuredBySpring(value = "ASSIGN_MONITORING_OFFICER",
            description = "Only comp admin, project finance and ifs administrators can assign projects to a monitoring officer")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    @Activity(type = MONITORING_OFFICER_ASSIGNED, projectId ="projectId")
    ServiceResult<Void> assignProjectToMonitoringOfficer(long userId, long projectId);

    @SecuredBySpring(value = "UNASSIGN_MONITORING_OFFICER",
            description = "Only comp admin, project finance and ifs administrators can unassign projects from a monitoring officer")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> unassignProjectFromMonitoringOfficer(long userId, long projectId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'GET_MONITORING_OFFICER_PROJECTS')")
    ServiceResult<List<ProjectResource>> getMonitoringOfficerProjects(long userId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_MONITORING_OFFICER')")
    ServiceResult<MonitoringOfficerResource> findMonitoringOfficerForProject(long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_MONITORING_OFFICER')")
    ServiceResult<Boolean> isMonitoringOfficerOnProject(long projectId, long userId);
}