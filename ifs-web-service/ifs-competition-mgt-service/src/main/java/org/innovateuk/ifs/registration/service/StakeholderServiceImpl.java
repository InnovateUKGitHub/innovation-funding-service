package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestServiceImpl;
import org.innovateuk.ifs.registration.form.InternalUserRegistrationForm;
import org.innovateuk.ifs.registration.form.StakeholderRegistrationForm;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StakeholderServiceImpl implements StakeholderService {
    @Autowired
    private CompetitionSetupStakeholderRestServiceImpl competitionSetupStakeholderRestService;

    @Override
    public ServiceResult<Void> createStakeholder(String inviteHash, StakeholderRegistrationForm stakeholderRegistrationForm) {
        StakeholderRegistrationResource stakeholderRegistrationResource = new StakeholderRegistrationResource();
        stakeholderRegistrationResource.setPassword(stakeholderRegistrationForm.getPassword());
        stakeholderRegistrationResource.setFirstName(stakeholderRegistrationForm.getFirstName());
        stakeholderRegistrationResource.setLastName(stakeholderRegistrationForm.getLastName());
        return competitionSetupStakeholderRestService.createStakeholder(inviteHash, stakeholderRegistrationResource).toServiceResult();
    }
}
