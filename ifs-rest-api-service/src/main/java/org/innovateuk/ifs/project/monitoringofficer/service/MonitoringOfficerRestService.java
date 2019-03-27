package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;

public interface MonitoringOfficerRestService {

    RestResult<MonitoringOfficerResource> getMonitoringOfficerForProject(long projectId);

    RestResult<Void> updateMonitoringOfficer(long projectId, String firstName, String lastName, String emailAddress, String phoneNumber);

    RestResult<List<ProjectResource>> getProjectsForMonitoringOfficer(long userId);
}
