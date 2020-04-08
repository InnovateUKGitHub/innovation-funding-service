package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupFinanceUsersRestService;
import org.innovateuk.ifs.management.registration.form.CompetitionFinanceRegistrationForm;
import org.innovateuk.ifs.registration.resource.CompetitionFinanceRegistrationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompetitionFinanceServiceImpl implements CompetitionFinanceService {

    @Autowired
    private CompetitionSetupFinanceUsersRestService competitionSetupFinanceUsersRestService;

    @Override
    public ServiceResult<Void> createCompetitionFinance(String inviteHash, CompetitionFinanceRegistrationForm competitionFinanceRegistrationForm) {
        CompetitionFinanceRegistrationResource competitionFinanceRegistrationResource = new CompetitionFinanceRegistrationResource();
        competitionFinanceRegistrationResource.setPassword(competitionFinanceRegistrationForm.getPassword());
        competitionFinanceRegistrationResource.setFirstName(competitionFinanceRegistrationForm.getFirstName());
        competitionFinanceRegistrationResource.setLastName(competitionFinanceRegistrationForm.getLastName());
        return competitionSetupFinanceUsersRestService.createFinanceUser(inviteHash, competitionFinanceRegistrationResource).toServiceResult();
    }
}
