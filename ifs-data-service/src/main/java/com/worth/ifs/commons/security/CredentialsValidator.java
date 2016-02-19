package com.worth.ifs.commons.security;


import com.worth.ifs.user.domain.User;

public interface CredentialsValidator {

    RestResult<User> retrieveUserByUid(String token);
}
