package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.RegistrationViewModelBuilder;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
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
import static org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.RegistrationViewModelBuilder.aRegistrationViewModel;

/**
 * Controller to manage external user registration.
 */
@Controller
@RequestMapping("/registration")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ExternalUserRegistrationController.class)
@PreAuthorize("permitAll")
public class ExternalUserRegistrationController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private InviteUserRestService inviteUserRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping("/{inviteHash}/register")
    public String yourDetails(Model model,
                              @PathVariable("inviteHash") String inviteHash,
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) RegistrationForm form,
                              UserResource loggedInUser) {
        RoleInviteResource invite = inviteUserRestService.getInvite(inviteHash).getSuccess();
        form.setEmail(invite.getEmail());
        return doViewYourDetails(model, invite, loggedInUser);
    }

    @PostMapping("/{inviteHash}/register")
    public String submitYourDetails(Model model,
                                    @PathVariable("inviteHash") String inviteHash,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) RegistrationForm registrationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    UserResource loggedInUser) {
        RoleInviteResource invite = inviteUserRestService.getInvite(inviteHash).getSuccess();
        Supplier<String> failureView = () -> doViewYourDetails(model, invite, loggedInUser);

        if(loggedInUser != null){
            return failureView.get();
        } else {
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                RestResult<UserResource> result = userRestService.createUser(registrationForm.constructUserCreationResource()
                    .withInviteHash(inviteHash)
                    .withRole(invite.getRole())
                .build());
                result.getErrors().forEach(error -> {
                    if (StringUtils.hasText(error.getFieldName())) {
                        bindingResult.rejectValue(error.getFieldName(), "registration." + error.getErrorKey());
                    } else {
                        bindingResult.reject("registration." + error.getErrorKey());
                    }
                });
                return validationHandler.
                            failNowOrSucceedWith(failureView,
                                                 () -> format("redirect:/registration/%s/register/account-created", inviteHash));
            });
        }
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
                return format("redirect:/registration/%s/register", inviteHash);
            }
            else {
                return "registration/account-created";
            }
        }).getSuccess();
    }

    private String doViewYourDetails(Model model, RoleInviteResource invite, UserResource loggedInUser) {
        if(loggedInUser != null) {
            return "registration/error";
        } else {
            RegistrationViewModelBuilder viewModelBuilder = aRegistrationViewModel();
            if (invite.getRole() == Role.KNOWLEDGE_TRANSFER_ADVISOR) {
                viewModelBuilder.withTermsRequired(true)
                        .withPhoneRequired(true)
                        .withAddressRequired(true)
                        .withRole(invite.getRole().getDisplayName())
                        .withPageTitle("Create " + invite.getRole().getDisplayName() + " account")
                        .withSubTitle("");
            }
            model.addAttribute("model", viewModelBuilder.build());
            return "registration/register";
        }
    }
}