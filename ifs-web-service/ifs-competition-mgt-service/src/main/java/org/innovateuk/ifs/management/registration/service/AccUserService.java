package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.management.registration.form.MonitoringOfficerRegistrationForm;

public interface AccUserService {
    ServiceResult<Void> activateAndUpdateMonitoringOfficer(String inviteHash, MonitoringOfficerRegistrationForm monitoringOfficerRegistrationForm);
}
