package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.AccUserRegistrationRestService;
import org.innovateuk.ifs.competition.service.MonitoringOfficerRegistrationRestService;
import org.innovateuk.ifs.invite.resource.AccMonitoringOfficerInviteResource;
import org.innovateuk.ifs.management.registration.form.MonitoringOfficerRegistrationForm;
import org.innovateuk.ifs.registration.resource.AccUserRegistrationResource;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccUserServiceImpl implements AccUserService {

    @Autowired
    private AccUserRegistrationRestService accUserRegistrationRestService;

    @Override
    public ServiceResult<Void> activateAndUpdateMonitoringOfficer(String inviteHash, MonitoringOfficerRegistrationForm monitoringOfficerRegistrationForm) {
        AccUserRegistrationResource accUserRegistrationResource = new AccUserRegistrationResource(
                monitoringOfficerRegistrationForm.getFirstName(),
                monitoringOfficerRegistrationForm.getLastName(),
                monitoringOfficerRegistrationForm.getPhoneNumber(),
                monitoringOfficerRegistrationForm.getPassword()
        );
        return accUserRegistrationRestService.createAccMonitoringOfficer(inviteHash, accUserRegistrationResource).toServiceResult();
    }
}
