package org.innovateuk.ifs.project.monitoring.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringAssignmentOfficerResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;

public interface MonitoringOfficerRestService {

    RestResult<MonitoringAssignmentOfficerResource> getProjectMonitoringOfficer(long projectMonitoringOfficerId);

    RestResult<Void> assignMonitoringOfficerToProject(long projectMonitoringOfficerId, long projectId);

    RestResult<Void> unassignMonitoringOfficerFromProject(long monitoringOfficerId, long projectId);

    RestResult<List<MonitoringAssignmentOfficerResource>> findAll();

    RestResult<List<ProjectResource>> getProjectsForMonitoringOfficer(long userId);

    RestResult<MonitoringOfficerResource> findMonitoringOfficerForProject(long projectId);
}