package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.registration.service.AcceptProjectInviteController.*;

@Controller
@PreAuthorize("permitAll")
public class ProjectRegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    @Autowired
    private CookieUtil cookieUtil;

    private final static String EMAIL_FIELD_NAME = "email";
    public static final String REGISTER_MAPPING = "/registration/register";
    private static final String REGISTRATION_SUCCESS_VIEW = "project/registration/successful";
    private static final String REGISTRATION_REGISTER_VIEW = "registration/register";

    @GetMapping(REGISTER_MAPPING)
    public String registerForm(Model model,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        String hash = cookieUtil.getCookieValue(request, INVITE_HASH);
        return projectInviteRestService.getInviteByHash(hash).andOnSuccess(invite -> {
                    ValidationMessages errors = errorMessages(loggedInUser, invite);
                    if (errors.hasErrors()) {
                        return populateModelWithErrorsAndReturnErrorView(errors, model);
                    }
                    model.addAttribute("invitee", true);
                    model.addAttribute("registrationForm", new RegistrationForm().withEmail(invite.getEmail()));
                    return restSuccess(REGISTRATION_REGISTER_VIEW);
                }
        ).getSuccessObject();
    }

    @PostMapping(REGISTER_MAPPING)
    public String registerFormSubmit(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     HttpServletRequest request,
                                     Model model,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        String hash = cookieUtil.getCookieValue(request, INVITE_HASH);
        return projectInviteRestService.getInviteByHash(hash).andOnSuccess(invite -> {
            registrationForm.setEmail(invite.getEmail());
            ValidationMessages errors = errorMessages(loggedInUser, invite);
            if (errors.hasErrors()) {
                return populateModelWithErrorsAndReturnErrorView(errors, model);
            }
            if (emailExists(registrationForm.getEmail())) {
                ValidationMessages.rejectValue(bindingResult, EMAIL_FIELD_NAME, "validation.standard.email.exists");
                return restSuccess(REGISTRATION_REGISTER_VIEW);
            }

            ServiceResult<String> result = createUser(registrationForm, invite.getOrganisation())
                    .andOnSuccess(newUser -> {
                        projectInviteRestService.acceptInvite(hash, newUser.getId());
                        return serviceSuccess(REGISTRATION_SUCCESS_VIEW);
                    });
            if (result.isSuccess()) {
                return restSuccess(REGISTRATION_SUCCESS_VIEW);
            } else {
                result.getErrors().forEach(error -> bindingResult.reject("registration." + error.getErrorKey()));
                return restSuccess(REGISTRATION_REGISTER_VIEW);
            }

        }).getSuccessObject();
    }

    private boolean emailExists(String email) {
        return userService.findUserByEmail(email).isPresent();
    }

    private ServiceResult<UserResource> createUser(RegistrationForm registrationForm, Long organisationId) {
        return userService.createOrganisationUser(
                registrationForm.getFirstName(),
                registrationForm.getLastName(),
                registrationForm.getPassword(),
                registrationForm.getEmail(),
                registrationForm.getTitle(),
                registrationForm.getPhoneNumber(),
                organisationId,
                registrationForm.getAllowMarketingEmails());
    }
}
