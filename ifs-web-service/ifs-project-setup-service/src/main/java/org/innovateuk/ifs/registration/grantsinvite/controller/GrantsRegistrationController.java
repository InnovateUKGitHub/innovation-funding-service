package org.innovateuk.ifs.registration.grantsinvite.controller;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.form.RegistrationForm.PhoneNumberValidationGroup;
import org.innovateuk.ifs.registration.form.RegistrationForm.TermsValidationGroup;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.RegistrationViewModelBuilder.aRegistrationViewModel;

@Controller
@SecuredBySpring(value = "Controller",
        description = "All invitees with a valid hash are able to register and create an account on the invited project",
        securedType = GrantsRegistrationController.class)
@PreAuthorize("permitAll")
@RequestMapping("/project/{projectId}/grants/invite/register")
public class GrantsRegistrationController {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private GrantsInviteRestService grantsInviteRestService;

    @Autowired
    private EncryptedCookieService cookieUtil;

    private final static String EMAIL_FIELD_NAME = "email";
    private static final String REGISTRATION_SUCCESS_VIEW = "project/registration/successful";
    private static final String REGISTRATION_REGISTER_VIEW = "registration/register";

    @GetMapping
    public String registerForm(Model model,
                               @PathVariable long projectId,
                               HttpServletRequest request,
                               UserResource loggedInUser) {
        String hash = cookieUtil.getCookieValue(request, AcceptGrantsInviteController.INVITE_HASH);
        return grantsInviteRestService.getInviteByHash(projectId, hash).andOnSuccess(invite -> {
            ValidationMessages errors = AcceptGrantsInviteController.validateUserCanAcceptInvite(loggedInUser, invite);
            if (errors.hasErrors()) {
                return AcceptGrantsInviteController.populateModelWithErrorsAndReturnErrorView(errors, model);
            }
            model.addAttribute("model",
                    aRegistrationViewModel()
                    .withInvitee(true)
                    .withPhoneRequired(true)
                    .withTermsRequired(true)
                    .withRole(invite.getGrantsInviteRole().getDisplayName())
                    .withProject(String.format("%d: %s", invite.getApplicationId(), invite.getProjectName()))
                    .withPhoneGuidance(invite.getGrantsInviteRole() == GrantsInviteRole.GRANTS_MONITORING_OFFICER ? "The project manager or partners can use this to contact you about their project." : null)
                    .build());


            model.addAttribute("form", new RegistrationForm().withEmail(invite.getEmail()));
                    return restSuccess(REGISTRATION_REGISTER_VIEW);
                }
        ).getSuccess();
    }

    @PostMapping
    public String registerFormSubmit(@Validated({Default.class, PhoneNumberValidationGroup.class, TermsValidationGroup.class}) @ModelAttribute("form") RegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     @PathVariable long projectId,
                                     HttpServletRequest request,
                                     Model model,
                                     UserResource loggedInUser) {
        String hash = cookieUtil.getCookieValue(request, AcceptGrantsInviteController.INVITE_HASH);
        return grantsInviteRestService.getInviteByHash(projectId, hash).andOnSuccess(invite -> {
            registrationForm.setEmail(invite.getEmail());
            model.addAttribute("model",  aRegistrationViewModel()
                    .withInvitee(true)
                    .withPhoneRequired(true)
                    .withTermsRequired(true)
                    .withRole(invite.getGrantsInviteRole().getDisplayName())
                    .withProject(String.format("%d: %s", invite.getApplicationId(), invite.getProjectName()))
                    .withPhoneGuidance(invite.getGrantsInviteRole() == GrantsInviteRole.GRANTS_MONITORING_OFFICER ? "The project manager or partners can use this to contact you about their project." : null)
                    .build());
            if (bindingResult.hasErrors()) {
                model.addAttribute("failureMessageKeys", bindingResult.getAllErrors());
                return restSuccess(REGISTRATION_REGISTER_VIEW);
            }

            ValidationMessages errors = AcceptGrantsInviteController.validateUserCanAcceptInvite(loggedInUser, invite);
            if (errors.hasErrors()) {
                return AcceptGrantsInviteController.populateModelWithErrorsAndReturnErrorView(errors, model);
            }

            if (invite.userExists()) {
                ValidationMessages.rejectValue(bindingResult, EMAIL_FIELD_NAME, "validation.standard.email.exists");
                return restSuccess(REGISTRATION_REGISTER_VIEW);
            }

            ServiceResult<String> result = userRestService.createUser(registrationForm.constructUserCreationResource()
                    .withRole(Role.LIVE_PROJECTS_USER)
                    .build())
                .toServiceResult()
                    .andOnSuccess(newUser -> {
                        grantsInviteRestService.acceptInvite(projectId, invite.getId());
                        return serviceSuccess(REGISTRATION_SUCCESS_VIEW);
                    });
            if (result.isSuccess()) {
                return restSuccess(REGISTRATION_SUCCESS_VIEW);
            } else {
                result.getErrors().forEach(error -> bindingResult.reject("registration." + error.getErrorKey()));
                return restSuccess(REGISTRATION_REGISTER_VIEW);
            }

        }).getSuccess();
    }
}
