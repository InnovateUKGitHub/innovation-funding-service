package org.innovateuk.ifs.commons.security;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Handling the tokens used for authentication for each request.
 * Note that this does not have the annotation as doing so would force dependent projects to provide
 * the dependencies of this class whether or not it is needed. Instead this class should be subclasses where it is
 * required and the annotation added to that.
 */
public class RootUidAuthenticationService implements UserAuthenticationService {

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
        return user.map(UserAuthentication::new).orElse(null);
    }
}
