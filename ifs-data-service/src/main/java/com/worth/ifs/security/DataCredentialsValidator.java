package com.worth.ifs.security;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.CredentialsValidator;
import com.worth.ifs.user.controller.UserController;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DataCredentialsValidator implements CredentialsValidator {

    @Autowired
    UserService userService;

    @Override
    public RestResult<User> retrieveUserByEmailAndPassword(String emailAddress, String password) {
        return userService.getUserByEmailandPassword(emailAddress, password).toGetResponse();
    }

    @Override
    public RestResult<User> retrieveUserByToken(String token) {
        return userService.getUserByToken(token).toGetResponse();
  }
}
