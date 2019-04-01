package org.innovateuk.ifs.project.monitoring.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;

public interface ProjectMonitoringOfficerRestService {

    RestResult<ProjectMonitoringOfficerResource> getProjectMonitoringOfficer(long projectMonitoringOfficerId);

    RestResult<Void> assignMonitoringOfficerToProject(long projectMonitoringOfficerId, long projectId);

    RestResult<Void> unassignMonitoringOfficerFromProject(long monitoringOfficerId, long projectId);

    RestResult<List<ProjectMonitoringOfficerResource>> findAll();

    RestResult<List<ProjectResource>> getProjectsForMonitoringOfficer(long userId);
}