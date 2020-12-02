package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.form.RegistrationForm;

public interface ExternalFinanceService {
    ServiceResult<Void> createExternalFinanceUser(String inviteHash, RegistrationForm competitionFinanceRegistrationForm);
}
