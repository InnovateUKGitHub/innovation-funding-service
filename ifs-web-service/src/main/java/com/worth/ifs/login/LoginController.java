package com.worth.ifs.login;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.worth.ifs.application.service.UserService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.exception.InvalidURLException;
import com.worth.ifs.commons.rest.RestResult;

/**
 * This controller handles user login, logout and authentication / authorization.
 * It will also redirect the user after the login/logout is successful.
 */

@Controller
@Configuration
public class LoginController {
    public static final String LOGIN_BASE = "login";
    public static final String RESET_PASSWORD = "reset-password";
    public static final String RESET_PASSWORD_FORM = "reset-password-form";
    public static final String RESET_PASSWORD_NOTIFICATION_SEND = "reset-password-notification-send";
    public static final String PASSWORD_CHANGED = "password-changed";

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/" + LOGIN_BASE + "/" + RESET_PASSWORD, method = RequestMethod.GET)
    public String requestPasswordReset(ResetPasswordRequestForm resetPasswordRequestForm, Model model) {
        model.addAttribute("resetPasswordRequestForm", resetPasswordRequestForm);
        return LOGIN_BASE + "/" + RESET_PASSWORD;
    }

    @RequestMapping(value = "/" + LOGIN_BASE + "/" + RESET_PASSWORD, method = RequestMethod.POST)
    public String requestPasswordResetPost(@ModelAttribute @Valid ResetPasswordRequestForm resetPasswordRequestForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("resetPasswordRequestForm", resetPasswordRequestForm);
            return LOGIN_BASE + "/" + RESET_PASSWORD;
        } else {
            log.warn("Reset password for: " + resetPasswordRequestForm.getEmail());
            userService.sendPasswordResetNotification(resetPasswordRequestForm.getEmail());
            return LOGIN_BASE + "/" + RESET_PASSWORD_NOTIFICATION_SEND;
        }
    }

    @RequestMapping(value = "/" + LOGIN_BASE + "/" + RESET_PASSWORD + "/hash/{hash}", method = RequestMethod.GET)
    public String resetPassword(@PathVariable("hash") String hash) {
        if (userService.checkPasswordResetHash(hash).isFailure()) {
            throw new InvalidURLException();
        }
        return LOGIN_BASE + "/" + RESET_PASSWORD_FORM;
    }

    @RequestMapping(value = "/" + LOGIN_BASE + "/" + RESET_PASSWORD + "/hash/{hash}", method = RequestMethod.POST)
    public String resetPasswordPost(@PathVariable("hash") String hash, @Valid @ModelAttribute ResetPasswordForm resetPasswordForm, BindingResult bindingResult) {
        if (userService.checkPasswordResetHash(hash).isFailure()) {
            throw new InvalidURLException();
        }

        if (bindingResult.hasErrors()) {
            return LOGIN_BASE + "/" + RESET_PASSWORD + "-form";
        } else {
            RestResult<Void> result = userService.resetPassword(hash, resetPasswordForm.getPassword());
            if(result.isFailure()){
                List<Error> errors = result.getFailure().getErrors();
                for (Error error : errors) {
                    bindingResult.rejectValue("password", "registration."+error.getErrorKey());
                }

                return LOGIN_BASE + "/" + RESET_PASSWORD + "-form";
            }else{
                return LOGIN_BASE + "/" + PASSWORD_CHANGED;
            }
        }
    }
}