package org.innovateuk.ifs.management.registration.populator;

import org.innovateuk.ifs.management.registration.viewmodel.MonitoringOfficerRegistrationViewModel;
import org.springframework.stereotype.Component;

@Component
public class MonitoringOfficerRegistrationModelPopulator {

    public MonitoringOfficerRegistrationViewModel populateModel(String email){
        return new MonitoringOfficerRegistrationViewModel(email);
    }
}
