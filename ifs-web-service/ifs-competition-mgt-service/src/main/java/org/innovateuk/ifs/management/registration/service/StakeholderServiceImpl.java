package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.management.registration.form.StakeholderRegistrationForm;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Web layer service here converts registration form into resource to be sent across via REST for creation of new stakeholder users
 */
@Service
public class StakeholderServiceImpl implements StakeholderService {

    @Autowired
    CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService;

    @Override
    public ServiceResult<Void> createStakeholder(String inviteHash, StakeholderRegistrationForm stakeholderRegistrationForm) {
        StakeholderRegistrationResource stakeholderRegistrationResource = new StakeholderRegistrationResource();
        stakeholderRegistrationResource.setPassword(stakeholderRegistrationForm.getPassword());
        stakeholderRegistrationResource.setFirstName(stakeholderRegistrationForm.getFirstName());
        stakeholderRegistrationResource.setLastName(stakeholderRegistrationForm.getLastName());
        return competitionSetupStakeholderRestService.createStakeholder(inviteHash, stakeholderRegistrationResource).toServiceResult();
    }
}
