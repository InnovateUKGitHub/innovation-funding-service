package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.CredentialsValidator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebCredentialsValidator implements CredentialsValidator {

    @Autowired
    private UserRestService userRestService;

    @Override
    public RestResult<UserResource> retrieveUserByUid(String uid) {
        return userRestService.retrieveUserResourceByUid(uid);
    }
}
