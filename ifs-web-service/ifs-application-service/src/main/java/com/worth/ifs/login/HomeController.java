package com.worth.ifs.login;

import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.resource.UserResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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

    public static String getRedirectUrlForUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (unauthenticated(authentication)) {
            return "redirect:/";
        }

        UserResource user = (UserResource) authentication.getDetails();

        return getRedirectUrlForUser(user);
    }

    public static String getRedirectUrlForUser(UserResource user) {

        String roleUrl = "";

        if (!user.getRoles().isEmpty()) {
            roleUrl = user.getRoles().get(0).getUrl();
        }

        StringBuilder stringBuilder = new StringBuilder()
                .append("redirect:");

        if (StringUtils.hasText(roleUrl)) {
            stringBuilder.append("/").append(roleUrl);
        }else{
            stringBuilder.append("/dashboard");
        }

        return stringBuilder
                .toString();
    }

    private static boolean unauthenticated(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated() || authentication.getDetails() == null;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login() {
        return getRedirectUrlForUser();
    }
}
