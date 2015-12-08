package com.worth.ifs.login;

import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * This controller handles user login, logout and authentication / authorization.
 * It will also redirect the user after the login/logout is successful.
 */

@Controller
@Configuration
public class LoginController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String login( Model model, HttpServletRequest request) {
        LoginForm loginForm = new LoginForm();
        setFormActionURL(loginForm, request);
        model.addAttribute("loginForm", loginForm);
        return "login";
    }

    @RequestMapping(value="/login", params={"logout"})
    public String logout(HttpServletResponse response) {
        userAuthenticationService.removeAuthentication(response);
        return "redirect:/login";
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginSubmit(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request) {
        String destination = "/login";
        if(!bindingResult.hasErrors()) {
            try {
                User authenticatedUser = userAuthenticationService.authenticate(loginForm.getEmail(), loginForm.getPassword());
                userAuthenticationService.addAuthentication(response, authenticatedUser);
                destination = setDestination(authenticatedUser, request);
            } catch(BadCredentialsException bce) {
                bindResult(bindingResult);
                setFormActionURL(loginForm, request);
                setDestinationToLogin();
            }
        }

        return destination;
    }

    private String setFormActionURL(LoginForm loginForm, HttpServletRequest request) {
        Long competitionIdString = getCompetitionId(request);
        if(competitionIdString!=null) {
            loginForm.setActionUrl("/login?competitionId="+competitionIdString);
        }
        else {
            loginForm.setActionUrl("/login");
        }

        return "something";
    }

    private void bindResult(BindingResult bindingResult) {
        bindingResult.rejectValue("email", "Your username/password combination doesn't seem to work", "Your username/password combination doesn't seem to work");
        bindingResult.rejectValue("password", "Your username/password combination doesn't seem to work", "Your username/password combination doesn't seem to work");
    }

    private String setDestinationToLogin() {
        return "/login";
    }

    private String setDestination(User authenticatedUser, HttpServletRequest request) {
        Long competitionId = getCompetitionId(request);

        String destination = null;
        if(competitionId!=null) {
            destination = redirectionForCreateApplication(competitionId);
        }
        else {
            destination = redirectionForUser(authenticatedUser);
        }

        return destination;
    }


    private Long getCompetitionId(HttpServletRequest request) {
        String competitionIdString = request.getParameter("competitionId");
        Long competitionId = null;

        try {
            if(Long.parseLong(competitionIdString)>=0) {
                competitionId = Long.parseLong(competitionIdString);
            }
        }
        catch (NumberFormatException e) {
            log.info("Invalid competitionId number format:" + e);
        }

        return competitionId;
    }

    private String redirectionForCreateApplication(Long competitionIdRedirect) {
        return "redirect:/application/create/"+competitionIdRedirect;
    }

    private String redirectionForUser(User user) {
        String roleName = "";

        if(user.getRoles().size() > 0) {
            roleName = user.getRoles().get(0).getName();
        }

        return "redirect:/" + roleName + "/dashboard";
    }
}

