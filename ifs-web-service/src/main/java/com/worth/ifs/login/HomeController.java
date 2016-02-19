package com.worth.ifs.login;

import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This Controller redirects the request from http://<domain>/ to http://<domain>/login
 * So we don't have a public homepage, the login page is the homepage.
 */
@Controller
public class HomeController {


    @Autowired
    UserAuthenticationService userAuthenticationService;

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String login() {
        return getRedirectUrlForUser();
    }

    public static String getRedirectUrlForUser() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();

        return getRedirectUrlForUser(user);
    }

    public static String getRedirectUrlForUser(User user) {

        String roleName = "";

        if(!user.getRoles().isEmpty()) {
            roleName = user.getRoles().get(0).getName();
        }

        return "redirect:/" + roleName + "/dashboard";
    }
}
