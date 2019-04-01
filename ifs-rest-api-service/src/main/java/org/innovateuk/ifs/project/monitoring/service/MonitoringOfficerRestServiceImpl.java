package org.innovateuk.ifs.project.monitoring.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectMonitoringOfficerResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;

@Service
public class MonitoringOfficerRestServiceImpl extends BaseRestService implements MonitoringOfficerRestService {

    private static final String PROJECT_MONITORING_OFFICER_REST_URL = "/monitoring-officer";

    @Override
    public RestResult<MonitoringOfficerResource> getProjectMonitoringOfficer(long projectMonitoringOfficerId) {
        return getWithRestResult(format("%s/%d", PROJECT_MONITORING_OFFICER_REST_URL, projectMonitoringOfficerId), MonitoringOfficerResource.class);
    }

    @Override
    public RestResult<Void> assignMonitoringOfficerToProject(long projectMonitoringOfficerId, long projectId) {
        return postWithRestResult(format("%s/%d/%s/%d", PROJECT_MONITORING_OFFICER_REST_URL, projectMonitoringOfficerId, "assign", projectId));
    }

    @Override
    public RestResult<Void> unassignMonitoringOfficerFromProject(long projectMonitoringOfficerId, long projectId) {
        return postWithRestResult(format("%s/%d/%s/%d", PROJECT_MONITORING_OFFICER_REST_URL, projectMonitoringOfficerId, "unassign", projectId));
    }

    @Override
    public RestResult<List<MonitoringOfficerResource>> findAll() {
        return getWithRestResult(format("%s/%s", PROJECT_MONITORING_OFFICER_REST_URL, "find-all"), projectMonitoringOfficerResourceListType());
    }

    @Override
    public RestResult<List<ProjectResource>> getProjectsForMonitoringOfficer(long userId) {
        return getWithRestResult(String.format("%s/%d/projects", PROJECT_MONITORING_OFFICER_REST_URL, userId), projectResourceListType());
    }
}
