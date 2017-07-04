package org.innovateuk.ifs.registration.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.registration.form.InternalUserRegistrationForm;
import org.innovateuk.ifs.registration.populator.InternalUserRegistrationModelPopulator;
import org.innovateuk.ifs.registration.service.InternalUserService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * Controller to manage internal user registration.
 */
@Controller
@RequestMapping("/registration")
@PreAuthorize("permitAll")
public class InternalUserRegistrationController {
    private static final Log LOG = LogFactory.getLog(InternalUserRegistrationController.class);

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private InternalUserRegistrationModelPopulator internalUserRegistrationModelPopulator;

    @Autowired
    private InviteUserRestService inviteUserRestService;

    @Autowired
    private InternalUserService internalUserService;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping("/{inviteHash}/register")
    public String yourDetails(Model model,
                              @PathVariable("inviteHash") String inviteHash,
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) InternalUserRegistrationForm form) {
        return doViewYourDetails(model, inviteHash);
    }

    @PostMapping("/{inviteHash}/register")
    public String submitYourDetails(Model model,
                                    @PathVariable("inviteHash") String inviteHash,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) InternalUserRegistrationForm registrationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> result = internalUserService.createInternalUser(inviteHash, registrationForm);

            result.getErrors().forEach(error -> {
                if (StringUtils.hasText(error.getFieldName())) {
                    bindingResult.rejectValue(error.getFieldName(), "registration." + error.getErrorKey());
                } else {
                    bindingResult.reject("registration." + error.getErrorKey());
                }
            });

            return validationHandler.
                    failNowOrSucceedWith(failureView, () -> format("redirect:/registration/%s/register/account-created", inviteHash));
        });
    }

    @GetMapping(value = "/{inviteHash}/register/account-created")
    public String accountCreated(@PathVariable("inviteHash") String inviteHash, UserResource loggedInUser) {
        boolean userIsLoggedIn = loggedInUser != null;

        // the user is already logged in, take them back to the dashboard
        if (userIsLoggedIn) {
            return "redirect:/";
        }

        return inviteUserRestService.checkExistingUser(inviteHash).andOnSuccessReturn(userExists -> {
            if (!userExists) {
                // reached here without creating account, redirect back to registatation
                return format("redirect:/registration/%s/register", inviteHash);
            }
            else {
                return "registration/account-created";
            }
        }).getSuccessObjectOrThrowException();
    }

    private String doViewYourDetails(Model model, String inviteHash) {
        model.addAttribute("model", internalUserRegistrationModelPopulator.populateModel(inviteHash));
        return "registration/register";
    }
}