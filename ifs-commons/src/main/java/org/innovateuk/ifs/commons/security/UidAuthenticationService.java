package org.innovateuk.ifs.commons.security;

import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.UserResource;
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

    @Autowired
    private CredentialsValidator validator;

    @Autowired
    private UidSupplier uidSupplier;


    @Override
    public Authentication getAuthentication(HttpServletRequest request) {
        return getAuthentication(request, false);
    }

    @Override
    public UserResource getAuthenticatedUser(HttpServletRequest request) {
        return getAuthenticatedUser(request, false);
    }

    /**
     * Retrieve the Authenticated user by its authentication token in the request header.
     */
    @Override
    public UserResource getAuthenticatedUser(HttpServletRequest request, boolean expireCache) {
        Authentication authentication = getAuthentication(request, expireCache);
        if(authentication!=null) {
            return (UserResource) authentication.getDetails();
        }
        return null;
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request, boolean expireCache) {
        String uid = uidSupplier.getUid(request);

        if (StringUtils.isBlank(uid)) {
            return null;
        }

        Optional<UserResource> user = validator.retrieveUserByUid(uid, expireCache).getOptionalSuccessObject();
        return user.isPresent() ? new UserAuthentication(user.get()) : null;
    }
}
