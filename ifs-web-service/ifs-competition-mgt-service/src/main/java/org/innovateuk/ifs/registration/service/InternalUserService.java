package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.form.InternalUserRegistrationForm;

/**
 * Interface for converting registration form into resource to be sent across via REST for creation of new user.
 */
public interface InternalUserService {
    ServiceResult<Void> createInternalUser(String inviteHash, InternalUserRegistrationForm registrationForm);
    ServiceResult<Void> editInternalUser(EditUserResource editUserResource);
}