package org.innovateuk.ifs.login;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.login.form.ResetPasswordForm;
import org.innovateuk.ifs.login.form.ResetPasswordRequestForm;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * This controller handles user login, logout and authentication / authorization.
 * It will also redirect the user after the login/logout is successful.
 */

@Controller
@Configuration
@PreAuthorize("permitAll")
public class LoginController {
    public static final String LOGIN_BASE = "login";
    public static final String RESET_PASSWORD = "reset-password";
    public static final String RESET_PASSWORD_FORM = "reset-password-form";
    public static final String RESET_PASSWORD_NOTIFICATION_SEND = "reset-password-notification-send";
    public static final String PASSWORD_CHANGED = "password-changed";

    private static final Log LOG = LogFactory.getLog(LoginController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/" + LOGIN_BASE + "/" + RESET_PASSWORD)
    public String requestPasswordReset(ResetPasswordRequestForm resetPasswordRequestForm, Model model) {
        model.addAttribute("resetPasswordRequestForm", resetPasswordRequestForm);
        return LOGIN_BASE + "/" + RESET_PASSWORD;
    }

    @PostMapping("/" + LOGIN_BASE + "/" + RESET_PASSWORD)
    public String requestPasswordResetPost(@ModelAttribute @Valid ResetPasswordRequestForm resetPasswordRequestForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("resetPasswordRequestForm", resetPasswordRequestForm);
            return LOGIN_BASE + "/" + RESET_PASSWORD;
        } else {
            LOG.warn("Reset password for: " + resetPasswordRequestForm.getEmail());
            userService.sendPasswordResetNotification(resetPasswordRequestForm.getEmail());
            return LOGIN_BASE + "/" + RESET_PASSWORD_NOTIFICATION_SEND;
        }
    }

    @GetMapping("/" + LOGIN_BASE + "/" + RESET_PASSWORD + "/hash/{hash}")
    public String resetPassword(@PathVariable("hash") String hash, @ModelAttribute ResetPasswordForm resetPasswordForm, Model model, HttpServletRequest request) {
        userService.checkPasswordResetHash(hash);
        return LOGIN_BASE + "/" + RESET_PASSWORD_FORM;
    }

    @PostMapping("/" + LOGIN_BASE + "/" + RESET_PASSWORD + "/hash/{hash}")
    public String resetPasswordPost(@PathVariable("hash") String hash, @Valid @ModelAttribute ResetPasswordForm resetPasswordForm, BindingResult bindingResult) {
        userService.checkPasswordResetHash(hash);

        if (bindingResult.hasErrors()) {
            return LOGIN_BASE + "/" + RESET_PASSWORD + "-form";
        } else {
            ServiceResult<Void> result = userService.resetPassword(hash, resetPasswordForm.getPassword());
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
