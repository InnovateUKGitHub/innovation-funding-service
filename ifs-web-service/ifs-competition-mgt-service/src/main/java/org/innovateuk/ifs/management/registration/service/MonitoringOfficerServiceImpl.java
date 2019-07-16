package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.MonitoringOfficerRegistrationRestService;
import org.innovateuk.ifs.management.registration.form.MonitoringOfficerRegistrationForm;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Web layer service here converts registration form into resource to be sent across via REST for creation of new monitoring officer users
 */
@Service
public class MonitoringOfficerServiceImpl implements MonitoringOfficerService {

    @Autowired
    private MonitoringOfficerRegistrationRestService competitionSetupMonitoringOfficerRestService;

    @Override
    public ServiceResult<Void> activateAndUpdateMonitoringOfficer(String inviteHash, MonitoringOfficerRegistrationForm monitoringOfficerRegistrationForm) {
        MonitoringOfficerRegistrationResource monitoringOfficerRegistrationResource = new MonitoringOfficerRegistrationResource(
                monitoringOfficerRegistrationForm.getFirstName(),
                monitoringOfficerRegistrationForm.getLastName(),
                monitoringOfficerRegistrationForm.getPhoneNumber(),
                monitoringOfficerRegistrationForm.getPassword()
        );
        return competitionSetupMonitoringOfficerRestService.createMonitoringOfficer(inviteHash, monitoringOfficerRegistrationResource).toServiceResult();
    }
}