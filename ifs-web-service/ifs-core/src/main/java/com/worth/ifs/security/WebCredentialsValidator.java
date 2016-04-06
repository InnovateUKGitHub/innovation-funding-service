package com.worth.ifs.security;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.CredentialsValidator;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.UserRestService;
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
