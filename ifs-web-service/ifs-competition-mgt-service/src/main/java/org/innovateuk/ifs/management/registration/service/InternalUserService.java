package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.form.RegistrationForm;

/**
 * Interface for converting registration form into resource to be sent across via REST for creation of new user.
 */
public interface InternalUserService {
    ServiceResult<Void> createInternalUser(String inviteHash, RegistrationForm registrationForm);
    ServiceResult<Void> editInternalUser(EditUserResource editUserResource);
}