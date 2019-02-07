package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.registration.viewmodel.MonitoringOfficerRegistrationViewModel;
import org.springframework.stereotype.Component;

@Component
public class MonitoringOfficerRegistrationModelPopulator {

    public MonitoringOfficerRegistrationViewModel populateModel(String email){
        return new MonitoringOfficerRegistrationViewModel(email);
    }
}
