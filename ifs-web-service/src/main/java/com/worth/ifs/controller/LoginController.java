package com.worth.ifs.controller;

import com.worth.ifs.domain.User;
import com.worth.ifs.form.LoginForm;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * This controller handles user login, logout and authentication / authorization.
 * It will also redirect the user after the login/logout is successful.
 */
@Controller
@Configuration
public class LoginController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    UserService userService;

    @Autowired
    TokenAuthenticationService tokenAuthenticationService;

    @RequestMapping(value="/login", method=RequestMethod.GET)
     public String login( Model model, HttpServletRequest request) {
        String token = "";
        if(token != null && token != ""){
            User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();
            if(user != null){
                return "redirect:/applicant/dashboard";
            }
        }

        List<User> users =userService.findAll();

        log.debug("Users in frontend " + users.size());

        model.addAttribute("users", users);
        model.addAttribute("loginForm", new LoginForm());


        return "login";
    }
    @RequestMapping(value="/login", params={"logout"})
    public String logout(Model model, HttpServletResponse response) {
        // Removing the cookie is not possible, just expire it as soon as possible.
        tokenAuthenticationService.removeAuthentication(response);
        return "redirect:/login";
    }


    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginSubmit(@ModelAttribute LoginForm loginForm, HttpServletResponse response){
        User user = userService.retrieveUserByToken(loginForm.getToken());
        if(user != null){
            tokenAuthenticationService.addAuthentication(response, loginForm.getToken());
            // redirect to my applications
            return "redirect:/applicant/dashboard";
        }

        return "login";
    }
}

