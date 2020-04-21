package org.innovateuk.ifs.management.registration.populator;

import org.innovateuk.ifs.management.registration.viewmodel.CompetitionFinanceRegistrationViewModel;
import org.springframework.stereotype.Component;

@Component
public class ExternalFinanceRegistrationModelPopulator {

    private final String EXTERNAL_FINANCE_ROLE = "External_finance";

    public CompetitionFinanceRegistrationViewModel populateModel(String email){

        return new CompetitionFinanceRegistrationViewModel(email, EXTERNAL_FINANCE_ROLE);
    }
}
