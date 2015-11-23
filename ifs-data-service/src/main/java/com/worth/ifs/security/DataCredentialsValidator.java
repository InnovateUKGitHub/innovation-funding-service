package com.worth.ifs.security;

import com.worth.ifs.commons.security.CredentialsValidator;
import com.worth.ifs.user.controller.UserController;
import com.worth.ifs.user.domain.User;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DataCredentialsValidator implements CredentialsValidator {

    @Autowired
    UserController userController;

    @Override
    public User retrieveUserByEmailAndPassword(String emailAddress, String password) {
        User user = userController.getUserByEmailandPassword(emailAddress, password);
        return user;
    }

    @Override
    public User retrieveUserByToken(String token) {
        User user = userController.getUserByToken(token);
        return user;
  }
}
