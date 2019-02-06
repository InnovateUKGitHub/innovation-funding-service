package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.registration.viewmodel.StakeholderRegistrationViewModel;
import org.springframework.stereotype.Component;

@Component
public class MonitoringOfficerRegistrationModelPopulator {

    private final String MONITORING_OFFICER_ROLE = "Monitoring officer";

    public StakeholderRegistrationViewModel populateModel(String email){
        return new StakeholderRegistrationViewModel(email, MONITORING_OFFICER_ROLE);
    }
}
