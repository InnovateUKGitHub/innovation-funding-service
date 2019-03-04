package org.innovateuk.ifs.project.monitoring.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;

public interface ProjectMonitoringOfficerRestService {

    RestResult<ProjectMonitoringOfficerResource> getProjectMonitoringOfficer(long projectMonitoringOfficerId);
}