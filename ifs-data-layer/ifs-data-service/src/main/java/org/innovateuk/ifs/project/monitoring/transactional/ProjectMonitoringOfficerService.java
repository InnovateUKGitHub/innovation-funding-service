package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectMonitoringOfficerService {

    @SecuredBySpring(value = "GET_MONITORING_OFFICER", description = "Only comp admin and project finance can get a project monitoring officer")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<ProjectMonitoringOfficerResource> getProjectMonitoringOfficer(long userId);

    @SecuredBySpring(value = "ASSIGN_MONITORING_OFFICER", description = "Only comp admin and project finance can assign projects to a monitoring officer")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> assignProjectToMonitoringOfficer(long userId, long projectId);

    @SecuredBySpring(value = "UNASSIGN_MONITORING_OFFICER", description = "Only comp admin and project finance can unassign projects from a monitoring officer")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> unassignProjectFromMonitoringOfficer(long userId, long projectId);
}