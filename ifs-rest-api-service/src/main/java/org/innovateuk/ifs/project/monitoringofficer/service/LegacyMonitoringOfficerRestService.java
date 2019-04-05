package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoringofficer.resource.LegacyMonitoringOfficerResource;

public interface LegacyMonitoringOfficerRestService {

    RestResult<LegacyMonitoringOfficerResource> getMonitoringOfficerForProject(long projectId);

    RestResult<Void> updateMonitoringOfficer(long projectId, String firstName, String lastName, String emailAddress, String phoneNumber);

}
