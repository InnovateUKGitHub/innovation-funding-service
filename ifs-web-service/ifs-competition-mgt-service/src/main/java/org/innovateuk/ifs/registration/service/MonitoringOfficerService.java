package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.form.MonitoringOfficerRegistrationForm;

/**
 * Interface for converting registration form into resource to be sent across via REST for activation of new monitoring officer users
 */
public interface MonitoringOfficerService {
    ServiceResult<Void> activateAndUpdateMonitoringOfficer(String inviteHash, MonitoringOfficerRegistrationForm monitoringOfficerRegistrationForm);
}
