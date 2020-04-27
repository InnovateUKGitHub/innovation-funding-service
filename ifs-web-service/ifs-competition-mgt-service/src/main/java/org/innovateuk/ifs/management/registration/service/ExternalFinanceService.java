package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.management.registration.form.CompetitionFinanceRegistrationForm;

public interface ExternalFinanceService {
    ServiceResult<Void> createExternalFinanceUser(String inviteHash, CompetitionFinanceRegistrationForm competitionFinanceRegistrationForm);
}
