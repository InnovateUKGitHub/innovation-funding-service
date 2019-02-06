package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupMonitoringOfficerRestService;
import org.innovateuk.ifs.registration.form.MonitoringOfficerRegistrationForm;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Web layer service here converts registration form into resource to be sent across via REST for creation of new monitoring officer users
 */
@Service
public class MonitoringOfficerServiceImpl implements MonitoringOfficerService {

    @Autowired
    private CompetitionSetupMonitoringOfficerRestService competitionSetupMonitoringOfficerRestService;

    @Override
    public ServiceResult<Void> createMonitoringOfficer(String inviteHash, MonitoringOfficerRegistrationForm monitoringOfficerRegistrationForm) {
        MonitoringOfficerRegistrationResource monitoringOfficerRegistrationResource =
                new MonitoringOfficerRegistrationResource(
                        monitoringOfficerRegistrationForm.getPassword(),
                        monitoringOfficerRegistrationForm.getFirstName(),
                        monitoringOfficerRegistrationForm.getLastName()
                );
        return competitionSetupMonitoringOfficerRestService.createMonitoringOfficer(inviteHash, monitoringOfficerRegistrationResource).toServiceResult();
    }
}