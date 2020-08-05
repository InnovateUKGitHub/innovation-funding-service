package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupExternalFinanceUsersRestService;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.resource.CompetitionFinanceRegistrationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalFinanceServiceImpl implements ExternalFinanceService {

    @Autowired
    private CompetitionSetupExternalFinanceUsersRestService competitionSetupExternalFinanceUsersRestService;

    @Override
    public ServiceResult<Void> createExternalFinanceUser(String inviteHash, RegistrationForm form) {
        CompetitionFinanceRegistrationResource competitionFinanceRegistrationResource = new CompetitionFinanceRegistrationResource();
        competitionFinanceRegistrationResource.setPassword(form.getPassword());
        competitionFinanceRegistrationResource.setFirstName(form.getFirstName());
        competitionFinanceRegistrationResource.setLastName(form.getLastName());
        return competitionSetupExternalFinanceUsersRestService.createExternalFinanceUser(inviteHash, competitionFinanceRegistrationResource).toServiceResult();
    }
}
