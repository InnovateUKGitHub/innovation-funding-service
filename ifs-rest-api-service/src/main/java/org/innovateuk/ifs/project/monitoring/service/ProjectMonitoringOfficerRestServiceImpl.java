package org.innovateuk.ifs.project.monitoring.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class ProjectMonitoringOfficerRestServiceImpl extends BaseRestService implements ProjectMonitoringOfficerRestService {

    private static final String PROJECT_MONITORING_OFFICER_REST_URL = "/project-monitoring-officer";

    @Override
    public RestResult<ProjectMonitoringOfficerResource> getProjectMonitoringOfficer(long projectMonitoringOfficerId) {
        return getWithRestResult(format("%s/%d", PROJECT_MONITORING_OFFICER_REST_URL, projectMonitoringOfficerId), ProjectMonitoringOfficerResource.class);
    }

    @Override
    public RestResult<Void> assignMonitoringOfficerToProject(long projectMonitoringOfficerId, long projectId) {
        return postWithRestResult(format("%s/%d/%s/%d", PROJECT_MONITORING_OFFICER_REST_URL, projectMonitoringOfficerId, "assign", projectId));
    }

    @Override
    public RestResult<Void> unassignMonitoringOfficerFromProject(long projectMonitoringOfficerId, long projectId) {
        return postWithRestResult(format("%s/%d/%s/%d", PROJECT_MONITORING_OFFICER_REST_URL, projectMonitoringOfficerId, "unassign", projectId));
    }
}
