package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;

public interface MonitoringOfficerRestService {

    RestResult<MonitoringOfficerResource> getMonitoringOfficerForProject(Long projectId);

    RestResult<Void> updateMonitoringOfficer(Long projectId, String firstName, String lastName, String emailAddress, String phoneNumber);
}
