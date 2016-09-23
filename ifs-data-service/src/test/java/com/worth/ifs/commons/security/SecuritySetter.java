package com.worth.ifs.commons.security;

import com.worth.ifs.user.resource.UserResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;

public class SecuritySetter {

    public static UserResource basicSecurityUser = newUserResource().withId(1L).withFirstName("steve").withLastName("smith").withEmail("steve.smith@empire.com").build();

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

}
