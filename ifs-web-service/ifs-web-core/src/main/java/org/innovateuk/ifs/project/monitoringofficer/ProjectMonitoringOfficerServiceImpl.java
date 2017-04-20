package org.innovateuk.ifs.project.monitoringofficer;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.monitoringofficer.service.ProjectMonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service for dealing with Monitoring Officer ProjectResources via the appropriate Rest services
 */
@Service
public class ProjectMonitoringOfficerServiceImpl implements ProjectMonitoringOfficerService {

    @Autowired
    private ProjectMonitoringOfficerRestService projectMonitoringOfficerRestService;

    @Override
    public ServiceResult<Void> updateMonitoringOfficer(Long projectId, String firstName, String lastName, String emailAddress, String phoneNumber) {
        return projectMonitoringOfficerRestService.updateMonitoringOfficer(projectId, firstName, lastName, emailAddress, phoneNumber).toServiceResult();
    }

    @Override
    public Optional<MonitoringOfficerResource> getMonitoringOfficerForProject(Long projectId) {
        return projectMonitoringOfficerRestService.getMonitoringOfficerForProject(projectId).toOptionalIfNotFound().
                getSuccessObjectOrThrowException();
    }
}
