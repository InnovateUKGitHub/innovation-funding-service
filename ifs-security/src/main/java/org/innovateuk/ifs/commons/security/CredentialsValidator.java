package org.innovateuk.ifs.commons.security;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.UserResource;

public interface CredentialsValidator {

    RestResult<UserResource> retrieveUserByUid(String token);

    RestResult<UserResource> retrieveUserByUid(String token, boolean expireCache);
}
