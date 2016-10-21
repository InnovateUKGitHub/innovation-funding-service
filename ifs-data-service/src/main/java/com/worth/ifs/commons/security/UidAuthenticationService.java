package com.worth.ifs.commons.security;

import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

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
    @Override
    public UserResource getAuthenticatedUser(HttpServletRequest request) {
        Authentication authentication = getAuthentication(request);
        if(authentication!=null) {
            return (UserResource) authentication.getDetails();
        }
        return null;
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request) {
        String uid = uidSupplier.getUid(request);

        if (StringUtils.isBlank(uid)) {
            return null;
        }

        Optional<UserResource> user = validator.retrieveUserByUid(uid).getOptionalSuccessObject();
        return user.isPresent() ? new UserAuthentication(user.get()) : null;
    }
}
