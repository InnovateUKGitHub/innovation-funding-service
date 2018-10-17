package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.registration.viewmodel.StakeholderRegistrationViewModel;
import org.springframework.stereotype.Component;

@Component
public class StakeholderRegistrationModelPopulator {

    private final String STAKEHOLDER_ROLE = "Stakeholder";

    public StakeholderRegistrationViewModel populateModel(String email){

        return new StakeholderRegistrationViewModel(email, STAKEHOLDER_ROLE);
    }
}
