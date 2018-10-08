package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.form.StakeholderRegistrationForm;

public interface StakeholderService {
    ServiceResult<Void> createStakeholder(String inviteHash, StakeholderRegistrationForm stakeholderRegistrationForm);
}
