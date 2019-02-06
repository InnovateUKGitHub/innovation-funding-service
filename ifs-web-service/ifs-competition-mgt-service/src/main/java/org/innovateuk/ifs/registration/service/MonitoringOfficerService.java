package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.form.MonitoringOfficerRegistrationForm;
import org.innovateuk.ifs.registration.form.StakeholderRegistrationForm;

/**
 * Interface for converting registration form into resource to be sent across via REST for creation of new monitoring officer users
 */
public interface MonitoringOfficerService {
    ServiceResult<Void> createMonitoringOfficer(String inviteHash, MonitoringOfficerRegistrationForm monitoringOfficerRegistrationForm);
}
