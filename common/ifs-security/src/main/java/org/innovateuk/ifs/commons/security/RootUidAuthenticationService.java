package org.innovateuk.ifs.commons.security;

import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
        UserResource userResource = new UserResource();
        userResource.setCreatedBy("");
        userResource.setAllowMarketingEmails(false);
        userResource.setEmail("123@123.com");
        userResource.setFirstName("sdf");
        userResource.setInviteName("sdddfs");
        userResource.setLastName("sdfsdf");
        userResource.setImageUrl("sdfsdfdfs");
        userResource.setModifiedBy("sddfsdsf");
        userResource.setId(37L);
        userResource.setUid(UUID.randomUUID().toString());

        Set<Long> tocIds = new HashSet<>();
        for (long i=0; i< 100; i++) {
            tocIds.add(i);
        }
        userResource.setTermsAndConditionsIds(tocIds);
        List roles = new ArrayList<>();
        roles.add(Role.SUPER_ADMIN_USER);
        userResource.setRoles(roles);
        userResource.setStatus(UserStatus.ACTIVE);
        Optional<UserResource> user = Optional.of(userResource);
        return user.map(UserAuthentication::new).orElse(null);
    }
}