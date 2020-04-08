package org.innovateuk.ifs.management.registration.populator;

import org.innovateuk.ifs.management.registration.viewmodel.CompetitionFinanceRegistrationViewModel;
import org.springframework.stereotype.Component;

@Component
public class CompetitionFinanceRegistrationModelPopulator {

    private final String COMPETITION_FINANCE_ROLE = "Competition_finance";

    public CompetitionFinanceRegistrationViewModel populateModel(String email){

        return new CompetitionFinanceRegistrationViewModel(email, COMPETITION_FINANCE_ROLE);
    }
}
