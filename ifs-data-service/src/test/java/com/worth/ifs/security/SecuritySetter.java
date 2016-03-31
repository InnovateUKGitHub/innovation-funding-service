package com.worth.ifs.security;


import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecuritySetter {

    public static final User swapOutForUser(User user){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (user == null) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        else {
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        }
        return authentication != null && authentication.getDetails() instanceof User ? (User)authentication.getDetails() : null;
    }

}
