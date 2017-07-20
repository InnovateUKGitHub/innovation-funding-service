package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.form.InternalUserRegistrationForm;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Web layer service here converts registration form into resource to be sent across via REST for creation of new user.
 */
@Service
public class InternalUserServiceImpl implements InternalUserService {
    @Autowired
    private UserRestService userRestService;

    @Override
    public ServiceResult<Void> createInternalUser(String inviteHash, InternalUserRegistrationForm registrationForm) {
        InternalUserRegistrationResource internalUserRegistrationResource = new InternalUserRegistrationResource();
        internalUserRegistrationResource.setPassword(registrationForm.getPassword());
        internalUserRegistrationResource.setFirstName(registrationForm.getFirstName());
        internalUserRegistrationResource.setLastName(registrationForm.getLastName());
        return userRestService.createInternalUser(inviteHash, internalUserRegistrationResource).toServiceResult();
    }

    @Override
    public ServiceResult<Void> editInternalUser(EditUserResource editUserResource) {
        return userRestService.editInternalUser(editUserResource).toServiceResult();
    }
}
