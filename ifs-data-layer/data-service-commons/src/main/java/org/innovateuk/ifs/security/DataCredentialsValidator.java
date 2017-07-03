package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.CredentialsValidator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class DataCredentialsValidator implements CredentialsValidator {

    @Autowired
    @Lazy
    private BaseUserService userService;

    @Override
    public RestResult<UserResource> retrieveUserByUid(String token) {
        return retrieveUserByUid(token, false);
    }

    @Override
    public RestResult<UserResource> retrieveUserByUid(String uid, boolean expireCache) {
        return userService.getUserResourceByUid(uid).toGetResponse();
    }
}
