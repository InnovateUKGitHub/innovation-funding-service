package com.worth.ifs.login;

import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

/**
 * This controller handles user login, logout and authentication / authorization.
 * It will also redirect the user after the login/logout is successful.
 */

@Controller
@Configuration
public class LoginController {

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String login() {
        return getRedirectUrlForUser();
    }

    @RequestMapping(value="/login", params={"logout"})
    public String logout(HttpServletResponse response) {
//        userAuthenticationService.removeAuthentication(response);
        return "redirect:/login";
    }

    public static String getRedirectUrlForUser() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();

        String roleName = "";

        if(!user.getRoles().isEmpty()) {
            roleName = user.getRoles().get(0).getName();
        }

        return "redirect:/" + roleName + "/dashboard";
    }
}

