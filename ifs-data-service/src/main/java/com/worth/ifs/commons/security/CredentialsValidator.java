package com.worth.ifs.commons.security;


import com.worth.ifs.user.domain.User;

public interface CredentialsValidator {

    User retrieveUserByUid(String token);
}
