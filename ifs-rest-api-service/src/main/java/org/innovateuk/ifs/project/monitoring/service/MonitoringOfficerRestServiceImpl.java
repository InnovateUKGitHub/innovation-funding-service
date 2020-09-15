package org.innovateuk.ifs.project.monitoring.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignmentResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.simpleUserListType;

@Service
public class MonitoringOfficerRestServiceImpl extends BaseRestService implements MonitoringOfficerRestService {

    private static final String PROJECT_MONITORING_OFFICER_REST_URL = "/monitoring-officer";

    @Override
    public RestResult<MonitoringOfficerAssignmentResource> getProjectMonitoringOfficer(long projectMonitoringOfficerId) {
        return getWithRestResult(format("%s/%d", PROJECT_MONITORING_OFFICER_REST_URL, projectMonitoringOfficerId), MonitoringOfficerAssignmentResource.class);
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
    public RestResult<List<SimpleUserResource>> findAll() {
        return getWithRestResult(format("%s/%s", PROJECT_MONITORING_OFFICER_REST_URL, "find-all"), simpleUserListType());
    }

    @Override
    public RestResult<List<ProjectResource>> getProjectsForMonitoringOfficer(long userId) {
        return getWithRestResult(String.format("%s/%d/projects", PROJECT_MONITORING_OFFICER_REST_URL, userId), projectResourceListType());
    }

    @Override
    public RestResult<MonitoringOfficerResource> findMonitoringOfficerForProject(long projectId) {
        return getWithRestResult(String.format("%s/project/%d/", PROJECT_MONITORING_OFFICER_REST_URL, projectId), MonitoringOfficerResource.class);
    }

    @Override
    public RestResult<Boolean> isMonitoringOfficerOnProject(long projectId, long userId) {
        return getWithRestResult(String.format("%s/project/%d/%s/%d", PROJECT_MONITORING_OFFICER_REST_URL, projectId, "is-monitoring-officer", userId), Boolean.class);
    }

    @Override
    public RestResult<Boolean> isMonitoringOfficer(long userId) {
        return getWithRestResult(String.format("%s/%s/%d", PROJECT_MONITORING_OFFICER_REST_URL, "is-monitoring-officer", userId), Boolean.class);
    }
}
