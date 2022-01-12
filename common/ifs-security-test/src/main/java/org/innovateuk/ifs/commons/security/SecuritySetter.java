package org.innovateuk.ifs.commons.security;

import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecuritySetter {

    public static UserResource basicSecurityUser = basicUser();

    public static final UserResource swapOutForUser(UserResource user) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (user == null) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        else {
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        }
        return authentication != null && authentication.getDetails() instanceof UserResource ? (UserResource)authentication.getDetails() : null;
    }

    public static final UserResource addBasicSecurityUser(){
        return swapOutForUser(basicSecurityUser);
    }

    private static final UserResource basicUser() {
        final UserResource basicSecurityUser = new UserResource();
        basicSecurityUser.setId(1L);
        basicSecurityUser.setFirstName("steve");
        basicSecurityUser.setLastName("smith");
        basicSecurityUser.setEmail("steve.smith@empire.com");
        basicSecurityUser.setUid("6b50cb4f-7222-33a5-99c5-8c068cd0b03c");
        basicSecurityUser.getRoles().add(Role.APPLICANT);
        return basicSecurityUser;
    }
}
