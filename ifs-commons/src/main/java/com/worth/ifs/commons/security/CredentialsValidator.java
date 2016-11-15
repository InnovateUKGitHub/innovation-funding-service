package com.worth.ifs.commons.security;


import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.UserResource;

public interface CredentialsValidator {

    RestResult<UserResource> retrieveUserByUid(String token);
}
