package org.innovateuk.ifs.project.monitoring.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignmentResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public interface MonitoringOfficerRestService {

    RestResult<MonitoringOfficerAssignmentResource> getProjectMonitoringOfficer(long projectMonitoringOfficerId);

    RestResult<Void> assignMonitoringOfficerToProject(long projectMonitoringOfficerId, long projectId);

    RestResult<Void> unassignMonitoringOfficerFromProject(long monitoringOfficerId, long projectId);

    RestResult<List<UserResource>> findAll();

    RestResult<List<ProjectResource>> getProjectsForMonitoringOfficer(long userId);

    RestResult<MonitoringOfficerResource> findMonitoringOfficerForProject(long projectId);
}