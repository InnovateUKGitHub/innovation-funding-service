package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.form.InternalUserRegistrationForm;

/**
 * Created by rav on 30/06/2017.
 */
public interface InternalUserService {
    ServiceResult<Void> createInternalUser(String inviteHash, InternalUserRegistrationForm registrationForm);
    ServiceResult<Void> editInternalUser(EditUserResource editUserResource);
}