package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.registration.viewmodel.StakeholderRegistrationViewModel;
import org.springframework.stereotype.Service;

@Service
public class StakeholderRegistrationModelPopulator {

    public StakeholderRegistrationViewModel populateModel(String email){

        return new StakeholderRegistrationViewModel();
    }
}
