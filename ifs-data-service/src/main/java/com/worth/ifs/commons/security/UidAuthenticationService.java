package com.worth.ifs.commons.security;

import com.worth.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Handling the tokens used for authentication for each request
 */
@Service
public class UidAuthenticationService implements UserAuthenticationService {

    public static final String AUTH_TOKEN = "IFS_AUTH_TOKEN";

    @Autowired
    private CredentialsValidator validator;

    @Autowired
    private UidSupplier uidSupplier;

    /**
     * Retrieve the Authenticated user by its authentication token in the request header.
     */
    public User getAuthenticatedUser(HttpServletRequest request) {
        Authentication authentication = getAuthentication(request);
        if(authentication!=null) {
            return (User) authentication.getDetails();
        }
        return null;
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        String uid = uidSupplier.getUid(request);
        User user = validator.retrieveUserByUid(uid);
        return user != null ? new UserAuthentication(user) : null;
    }
}
