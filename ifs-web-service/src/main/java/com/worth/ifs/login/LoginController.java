package com.worth.ifs.login;

import com.worth.ifs.application.service.UserService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.exception.InvalidURLException;
import com.worth.ifs.commons.rest.RestResult;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    private UserService userService;


    @RequestMapping(value = "/login/reset-password", method = RequestMethod.GET)
    public String requestPasswordReset(ResetPasswordRequestForm resetPasswordRequestForm, Model model, HttpServletRequest request) {
        model.addAttribute("resetPasswordRequestForm", resetPasswordRequestForm);
        return "login/reset-password";
    }

    @RequestMapping(value = "/login/reset-password", method = RequestMethod.POST)
    public String requestPasswordResetPost(@ModelAttribute @Valid ResetPasswordRequestForm resetPasswordRequestForm, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("resetPasswordRequestForm", resetPasswordRequestForm);
            return "login/reset-password";
        } else {
            log.warn("Reset password for: " + resetPasswordRequestForm.getEmail());
            userService.sendPasswordResetNotification(resetPasswordRequestForm.getEmail());
            return "login/reset-password-notification-send";
        }
    }

    @RequestMapping(value = "/login/reset-password/hash/{hash}", method = RequestMethod.GET)
    public String resetPassword(@PathVariable("hash") String hash, @ModelAttribute ResetPasswordForm resetPasswordForm, Model model, HttpServletRequest request) {
        if (userService.checkPasswordResetHash(hash).isFailure()) {
            throw new InvalidURLException();
        }
        return "login/reset-password-form";
    }

    @RequestMapping(value = "/login/reset-password/hash/{hash}", method = RequestMethod.POST)
    public String resetPasswordPost(@PathVariable("hash") String hash, @Valid @ModelAttribute ResetPasswordForm resetPasswordForm, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (userService.checkPasswordResetHash(hash).isFailure()) {
            throw new InvalidURLException();
        }

        if (bindingResult.hasErrors()) {
            return "login/reset-password-form";
        } else {
            RestResult<Void> result = userService.resetPassword(hash, resetPasswordForm.getPassword());
            if(result.isFailure()){
                List<Error> errors = result.getFailure().getErrors();
                for (Error error : errors) {
                    bindingResult.rejectValue("password", "registration."+error.getErrorKey());
                }

                return "login/reset-password-form";
            }else{
                return "login/password-changed";
            }
        }
    }
}