package com.worth.ifs.security;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.CredentialsValidator;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
public class DataCredentialsValidator implements CredentialsValidator {

    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public RestResult<UserResource> retrieveUserByUid(String uid) {
        return userService.getUserResourceByUid(uid).toGetResponse();
    }
}
