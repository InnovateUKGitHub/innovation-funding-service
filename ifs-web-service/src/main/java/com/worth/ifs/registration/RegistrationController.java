package com.worth.ifs.registration;

import com.worth.ifs.application.AcceptInviteController;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.UserService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.TokenAuthenticationService;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.login.LoginController;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.util.CookieUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Autowired
    Validator validator;
    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    protected UserAuthenticationService userAuthenticationService;

    private final Log log = LogFactory.getLog(getClass());

    public final static String ORGANISATION_ID_PARAMETER_NAME = "organisationId";
    public final static String EMAIL_FIELD_NAME = "email";

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm(Model model, HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        if(user != null){
            return LoginController.getRedirectUrlForUser(user);
        }

        String destination = "registration-register";

        if (!processOrganisation(request, model)) {
            destination = "redirect:/login";
        }

        addRegistrationFormToModel(model, request);
        return destination;
    }

    private boolean processOrganisation(HttpServletRequest request, Model model) {
        boolean success = true;

        Organisation organisation = getOrganisation(request);
        if (organisation != null) {
            addOrganisationNameToModel(model, organisation);
        } else {
            success = false;
        }

        return success;
    }

    private void addRegistrationFormToModel(Model model, HttpServletRequest request) {
        RegistrationForm registrationForm = new RegistrationForm();
        setFormActionURL(registrationForm, request);
        setInviteeEmailAddress(registrationForm, request, model);
        model.addAttribute("registrationForm", registrationForm);
    }

    private void setInviteeEmailAddress(RegistrationForm registrationForm, HttpServletRequest request, Model model) {
        String inviteHash = CookieUtil.getCookieValue(request, AcceptInviteController.INVITE_HASH);
        if(StringUtils.hasText(inviteHash)){
            RestResult<InviteResource> invite = inviteRestService.getInviteByHash(inviteHash);
            if(invite.isSuccess() && InviteStatusConstants.SEND.equals(invite.getSuccessObject().getStatus())){
                InviteResource inviteResource = invite.getSuccessObject();
                registrationForm.setEmail(inviteResource.getEmail());
                model.addAttribute("invitee", true);
            }else{
                log.debug("Invite already accepted.");
            }
        }
    }

    private Organisation getOrganisation(HttpServletRequest request) {
        return organisationService.getOrganisationById(getOrganisationId(request));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerFormSubmit(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     HttpServletResponse response,
                                     HttpServletRequest request,
                                     Model model) {
        setInviteeEmailAddress(registrationForm, request, model);
        // re-validate since we did set the emailaddress in the meantime. @Valid annotation is needed for unit tests.
        validator.validate(registrationForm, bindingResult);

        User user = userAuthenticationService.getAuthenticatedUser(request);
        if(user != null){
            return LoginController.getRedirectUrlForUser(user);
        }

        String destination = "registration-register";

        checkForExistingEmail(registrationForm.getEmail(), bindingResult);

        if(!bindingResult.hasErrors()) {
            RestResult<UserResource> createUserResult = createUser(registrationForm, getOrganisationId(request));

            if (createUserResult.isSuccess()) {
                loginUser(createUserResult.getSuccessObject(), response);
                linkToInvite(request, createUserResult.getSuccessObject());
                destination = "redirect:/application/create/initialize-application/";
            } else {
                addEnvelopeErrorsToBindingResultErrors(createUserResult.getFailure().getErrors(), bindingResult);
            }

        } else {
            if (!processOrganisation(request, model)) {
                destination = "redirect:/login";
            }
        }

        return destination;
    }

    private void linkToInvite(HttpServletRequest request, UserResource userResource) {
        String inviteHash = CookieUtil.getCookieValue(request, AcceptInviteController.INVITE_HASH);
        if(StringUtils.hasText(inviteHash)){
            inviteRestService.getInviteByHash(inviteHash).andOnSuccessReturn(i -> {
                log.debug("Found invite, link to created user now.");
                i.setStatus(InviteStatusConstants.ACCEPTED);
                HttpStatus statusCode = inviteRestService.acceptedInvite(inviteHash, userResource.getId()).getStatusCode();
                log.debug("Found invite, changed status " +statusCode.toString());
                return i;
            });
        }
    }

    private void checkForExistingEmail(String email, BindingResult bindingResult) {
        if(!bindingResult.hasFieldErrors(EMAIL_FIELD_NAME) && StringUtils.hasText(email)) {
            List<UserResource> users = userService.findUserByEmail(email).getSuccessObject();
            if (users != null && !users.isEmpty()) {
                bindingResult.addError(new FieldError(EMAIL_FIELD_NAME, EMAIL_FIELD_NAME, email, false, null, null, "Email address is already in use"));
            }
        }
    }

    private void addEnvelopeErrorsToBindingResultErrors(List<Error> errors, BindingResult bindingResult) {
        errors.forEach(
                error -> bindingResult.addError(
                        new ObjectError(
                                error.getErrorKey(),
                                error.getErrorMessage()
                        )
                )
        );
    }

    private void loginUser(UserResource userResource, HttpServletResponse response) {
        log.debug("loginUser");
        CookieUtil.saveToCookie(response, "userId", String.valueOf(userResource.getId()));
        tokenAuthenticationService.addAuthentication(response, userResource);
    }

    private RestResult<UserResource> createUser(RegistrationForm registrationForm, Long organisationId) {
        return userService.createLeadApplicantForOrganisation(
                registrationForm.getFirstName(),
                registrationForm.getLastName(),
                registrationForm.getPassword(),
                registrationForm.getEmail(),
                registrationForm.getTitle(),
                registrationForm.getPhoneNumber(),
                organisationId);
    }

    private void addOrganisationNameToModel(Model model, Organisation organisation) {
        model.addAttribute("organisationName", organisation.getName());
    }

    private Long getOrganisationId(HttpServletRequest request) {
        String organisationParameter = request.getParameter(ORGANISATION_ID_PARAMETER_NAME);
        Long organisationId = null;

        try {
            if (Long.parseLong(organisationParameter) >= 0) {
                organisationId = Long.parseLong(organisationParameter);
            }
        } catch (NumberFormatException e) {
            log.info("Invalid organisationId number format:" + e);
        }

        return organisationId;
    }

    private void setFormActionURL(RegistrationForm registrationForm, HttpServletRequest request) {
        Long organisationId = getOrganisationId(request);
        registrationForm.setActionUrl("/registration/register?" + ORGANISATION_ID_PARAMETER_NAME + "=" + organisationId);
    }
}
