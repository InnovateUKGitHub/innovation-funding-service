package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.monitoringofficer.resource.LegacyMonitoringOfficerResource;
import org.springframework.stereotype.Service;

@Service
public class LegacyMonitoringOfficerRestServiceImpl extends BaseRestService implements LegacyMonitoringOfficerRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<Void> updateMonitoringOfficer(long projectId, String firstName, String lastName, String emailAddress, String phoneNumber) {
        LegacyMonitoringOfficerResource monitoringOfficerData = new LegacyMonitoringOfficerResource(firstName, lastName, emailAddress, phoneNumber, projectId);
        return putWithRestResult(projectRestURL + "/" + projectId + "/monitoring-officer", monitoringOfficerData, Void.class);
    }

    @Override
    public RestResult<LegacyMonitoringOfficerResource> getMonitoringOfficerForProject(long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/monitoring-officer", LegacyMonitoringOfficerResource.class);
    }
}