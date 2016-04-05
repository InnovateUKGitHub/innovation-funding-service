package com.worth.ifs;

import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {
    @Autowired
    UserAuthenticationService userAuthenticationService;

    public UserResource getLoggedUser(HttpServletRequest req) {
        return userAuthenticationService.getAuthenticatedUser(req);
    }
}
