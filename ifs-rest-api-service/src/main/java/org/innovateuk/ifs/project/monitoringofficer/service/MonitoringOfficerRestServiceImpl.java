package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;

@Service
public class MonitoringOfficerRestServiceImpl extends BaseRestService implements MonitoringOfficerRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<Void> updateMonitoringOfficer(long projectId, String firstName, String lastName, String emailAddress, String phoneNumber) {
        MonitoringOfficerResource monitoringOfficerData = new MonitoringOfficerResource(firstName, lastName, emailAddress, phoneNumber, projectId);
        return putWithRestResult(projectRestURL + "/" + projectId + "/monitoring-officer", monitoringOfficerData, Void.class);
    }

    @Override
    public RestResult<MonitoringOfficerResource> getMonitoringOfficerForProject(long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/monitoring-officer", MonitoringOfficerResource.class);
    }

    @Override
    public RestResult<List<ProjectResource>> getProjectsForMonitoringOfficer(long userId) {
        return getWithRestResult(projectRestURL + "/monitoring-officer/projects/" + userId, projectResourceListType());
    }
}