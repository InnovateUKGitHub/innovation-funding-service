package com.worth.ifs.login;

import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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

    private final static String REDIRECT_URL_PARAMETER = "redirect_url";

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String login( Model model, HttpServletRequest request) {
        LoginForm loginForm = new LoginForm();
        loginForm.setActionUrl(getActionUrl(request));
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
                if (authenticatedUser != null) {
                    String redirectUrl = getRedirectUrl(request);
                    if(redirectUrl != null){
                        destination = "redirect:"+redirectUrl;
                    }else{
                        destination = redirectionForUser(authenticatedUser);
                    }
                }
            } catch(BadCredentialsException bce) {
                log.error(bce);
                bindResult(bindingResult);
                setDestinationToLogin();
            }
        }

        loginForm.setActionUrl(getActionUrl(request));

        return destination;
    }

    private String getActionUrl(HttpServletRequest request) {
        String actionUrl = "/login";
        if(getRedirectUrl(request)!=null){
            actionUrl="/login?"+REDIRECT_URL_PARAMETER+"="+getRedirectUrl(request);
        }

        return actionUrl;
    }

    private String getRedirectUrl(HttpServletRequest request){
        String redirectUrl =null;
        if(request.getParameter(REDIRECT_URL_PARAMETER) != null && StringUtils.hasText(request.getParameter(REDIRECT_URL_PARAMETER))){
            redirectUrl = request.getParameter(REDIRECT_URL_PARAMETER);

            if(!redirectUrl.startsWith("/") || redirectUrl.contains("http")){
                log.error("Login tried to redirect to not allowed URL: "+ redirectUrl);
                redirectUrl = null;
            }
        }
        return redirectUrl;
    }

    private void bindResult(BindingResult bindingResult) {
        bindingResult.rejectValue("email", "Your username/password combination doesn't seem to work", "Your username/password combination doesn't seem to work");
        bindingResult.rejectValue("password", "Your username/password combination doesn't seem to work", "Your username/password combination doesn't seem to work");
    }

    private String setDestinationToLogin() {
        return "/login";
    }

    private String redirectionForUser(User user) {
        String roleName = "";

        if(user.getRoles().size() > 0) {
            roleName = user.getRoles().get(0).getName();
        }

        return "redirect:/" + roleName + "/dashboard";
    }
}

