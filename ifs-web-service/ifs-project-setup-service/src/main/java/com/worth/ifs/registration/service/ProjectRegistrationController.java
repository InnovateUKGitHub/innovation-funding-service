package com.worth.ifs.registration.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.invite.service.ProjectInviteRestService;
import com.worth.ifs.project.service.ProjectRestService;
import com.worth.ifs.registration.form.RegistrationForm;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.registration.service.AcceptProjectInviteController.*;
import static com.worth.ifs.util.CookieUtil.getCookieValue;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class ProjectRegistrationController {

    @Autowired
    @Qualifier("mvcValidator")
    Validator validator;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    @Autowired
    protected UserAuthenticationService userAuthenticationService;

    @Autowired
    protected ProjectRestService projectRestService;

    @Autowired
    protected OrganisationRestService organisationRestService;

    private final static String EMAIL_FIELD_NAME = "email";
    public static final String REGISTER_MAPPING = "/registration/register";
    private static final String REGISTRATION_SUCCESS_VIEW = "project/registration/successful";
    private static final String REGISTRATION_REGISTER_VIEW = "project/registration/register";

    @RequestMapping(value = REGISTER_MAPPING, method = RequestMethod.GET)
    public String registerForm(Model model,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        String hash = getCookieValue(request, INVITE_HASH);
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

    @RequestMapping(value = REGISTER_MAPPING, method = RequestMethod.POST)
    public String registerFormSubmit(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     HttpServletRequest request,
                                     Model model,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        String hash = getCookieValue(request, INVITE_HASH);
        return projectInviteRestService.getInviteByHash(hash).andOnSuccess(invite -> {
            registrationForm.setEmail(invite.getEmail());
            ValidationMessages errors = errorMessages(loggedInUser, invite);
            if (errors.hasErrors()) {
                return populateModelWithErrorsAndReturnErrorView(errors, model);
            }
            if (emailExists(registrationForm.getEmail())) {
                bindingResult.addError(new FieldError(EMAIL_FIELD_NAME, EMAIL_FIELD_NAME, registrationForm.getEmail(), false, null, null, "Email address is already in use"));
                return restSuccess(REGISTRATION_REGISTER_VIEW);
            }
            RestResult<String> result = createUser(registrationForm, invite.getOrganisation())
                    .andOnSuccess(newUser -> {
                        projectInviteRestService.acceptInvite(hash, newUser.getId());
                        return restSuccess(REGISTRATION_SUCCESS_VIEW);
                    });
            if (result.isSuccess()) {
                return result;
            } else {
                result.getErrors().forEach(error -> bindingResult.reject("registration." + error.getErrorKey()));
                return restSuccess(REGISTER_MAPPING);
            }
        }).getSuccessObject();
    }

    private boolean emailExists(String email) {
        RestResult<UserResource> existingUserSearch = userService.findUserByEmailForAnonymousUserFlow(email);
        return !NOT_FOUND.equals(existingUserSearch.getStatusCode());
    }

    private RestResult<UserResource> createUser(RegistrationForm registrationForm, Long organisationId) {
        return userService.createOrganisationUser(
                registrationForm.getFirstName(),
                registrationForm.getLastName(),
                registrationForm.getPassword(),
                registrationForm.getEmail(),
                registrationForm.getTitle(),
                registrationForm.getPhoneNumber(),
                organisationId);
    }
}
