package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.management.registration.form.StakeholderRegistrationForm;

/**
 * Interface for converting registration form into resource to be sent across via REST for creation of new stakeholder users
 */
public interface StakeholderService {
    ServiceResult<Void> createStakeholder(String inviteHash, StakeholderRegistrationForm stakeholderRegistrationForm);
}
