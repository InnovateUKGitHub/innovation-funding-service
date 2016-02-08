package com.worth.ifs.commons.security;


import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.User;

public interface CredentialsValidator {
    RestResult<User> retrieveUserByEmailAndPassword(String emailAddress, String password);

    RestResult<User> retrieveUserByToken(String token);
}
