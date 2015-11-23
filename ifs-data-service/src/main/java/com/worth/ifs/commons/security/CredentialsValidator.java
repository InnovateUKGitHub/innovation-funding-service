package com.worth.ifs.commons.security;


import com.worth.ifs.user.domain.User;

public interface CredentialsValidator {
    User retrieveUserByEmailAndPassword(String emailAddress, String password);

    User retrieveUserByToken(String token);
}
