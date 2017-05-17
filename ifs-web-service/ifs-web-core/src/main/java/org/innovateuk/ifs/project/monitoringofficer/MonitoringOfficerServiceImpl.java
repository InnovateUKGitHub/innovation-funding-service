package org.innovateuk.ifs.project.monitoringofficer;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.monitoringofficer.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service for dealing with Monitoring Officer ProjectResources via the appropriate Rest services
 */
@Service
public class MonitoringOfficerServiceImpl implements MonitoringOfficerService {

    @Autowired
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Override
    public ServiceResult<Void> updateMonitoringOfficer(Long projectId, String firstName, String lastName, String emailAddress, String phoneNumber) {
        return monitoringOfficerRestService.updateMonitoringOfficer(projectId, firstName, lastName, emailAddress, phoneNumber).toServiceResult();
    }

    @Override
    public Optional<MonitoringOfficerResource> getMonitoringOfficerForProject(Long projectId) {
        return monitoringOfficerRestService.getMonitoringOfficerForProject(projectId).toOptionalIfNotFound().
                getSuccessObjectOrThrowException();
    }
}
