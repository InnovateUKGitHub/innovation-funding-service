package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.form.InternalUserRegistrationForm;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by rav on 30/06/2017.
 */
@Service
public class InternalUserServiceImpl implements InternalUserService {
    @Autowired
    private UserRestService inviteUserRestService;

    @Override
    public ServiceResult<Void> createInternalUser(String inviteHash, InternalUserRegistrationForm registrationForm) {
        InternalUserRegistrationResource internalUserRegistrationResource = new InternalUserRegistrationResource();
        internalUserRegistrationResource.setPassword(registrationForm.getPassword());
        internalUserRegistrationResource.setFirstName(registrationForm.getFirstName());
        internalUserRegistrationResource.setLastName(registrationForm.getLastName());
        return inviteUserRestService.createInternalUser(inviteHash, internalUserRegistrationResource).toServiceResult();
    }
}
