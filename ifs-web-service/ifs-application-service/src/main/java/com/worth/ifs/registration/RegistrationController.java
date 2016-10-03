package com.worth.ifs.registration;

import com.worth.ifs.application.ApplicationCreationController;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.exception.InviteAlreadyAcceptedException;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.registration.form.RegistrationForm;
import com.worth.ifs.registration.form.ResendEmailVerificationForm;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.UserService;
import com.worth.ifs.util.CookieUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.login.HomeController.getRedirectUrlForUser;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    public static final String BASE_URL = "/registration/register";

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
    protected UserAuthenticationService userAuthenticationService;

    @Autowired
    protected CookieFlashMessageFilter cookieFlashMessageFilter;

    private static final Log LOG = LogFactory.getLog(RegistrationController.class);

    public final static String ORGANISATION_ID_PARAMETER_NAME = "organisationId";
    public final static String EMAIL_FIELD_NAME = "email";

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String registrationSuccessful(
            @RequestHeader(value = "referer", required = false) final String referer,
            final HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.removeCookie(response, AcceptInviteController.INVITE_HASH);
        if(referer == null || !referer.contains(request.getServerName() + "/registration/register")){
            throw new ObjectNotFoundException("Attempt to access registration page directly...", Collections.emptyList());
        }
        return "registration/successful";
    }

    @RequestMapping(value = "/verified", method = RequestMethod.GET)
    public String verificationSuccessful(final HttpServletRequest request, final HttpServletResponse response) {
        if(!hasVerifiedCookieSet(request)){
            throw new ObjectNotFoundException("Attempt to access registration page directly...", Collections.emptyList());
        } else {
            cookieFlashMessageFilter.removeFlashMessage(response);
            return "registration/verified";
        }
    }

    @RequestMapping(value = "/verify-email/{hash}", method = RequestMethod.GET)
    public String verifyEmailAddress(@PathVariable("hash") final String hash,
                                     final HttpServletResponse response){
        userService.verifyEmail(hash).getSuccessObjectOrThrowException();
        cookieFlashMessageFilter.setFlashMessage(response, "verificationSuccessful");
        return "redirect:/registration/verified";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm(Model model, HttpServletRequest request, HttpServletResponse response) {

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        if(user != null){
            return getRedirectUrlForUser(user);
        }

        if (getOrganisationId(request) == null){
            return  "redirect:/";
        }

        try {
        	addRegistrationFormToModel(model, request, response);
        }
        catch (InviteAlreadyAcceptedException e) {
        	cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
        	return "redirect:/login";
        }

        String destination = "registration-register";

        if (!processOrganisation(request, model)) {
            destination = "redirect:/";
        }

        return destination;
    }

    private boolean processOrganisation(HttpServletRequest request, Model model) {
        boolean success = true;

        OrganisationResource organisation = getOrganisation(request);
        if (organisation != null) {
            addOrganisationNameToModel(model, organisation);
        } else {
            success = false;
        }

        return success;
    }

    private void addRegistrationFormToModel(Model model, HttpServletRequest request, HttpServletResponse response) {
        RegistrationForm registrationForm = new RegistrationForm();
        setOrganisationIdCookie(registrationForm, request, response);
        setInviteeEmailAddress(registrationForm, request, model);
        model.addAttribute("registrationForm", registrationForm);
    }

    /**
     * When the current user is a invitee, user the invite email-address in the registration flow.
     */
    private boolean setInviteeEmailAddress(RegistrationForm registrationForm, HttpServletRequest request, Model model) {
        String inviteHash = CookieUtil.getCookieValue(request, AcceptInviteController.INVITE_HASH);
        if(StringUtils.hasText(inviteHash)){
            RestResult<ApplicationInviteResource> invite = inviteRestService.getInviteByHash(inviteHash);
            if(invite.isSuccess() && InviteStatus.SENT.equals(invite.getSuccessObject().getStatus())){
                ApplicationInviteResource inviteResource = invite.getSuccessObject();
                registrationForm.setEmail(inviteResource.getEmail());
                model.addAttribute("invitee", true);
                return true;
            }else{
                LOG.debug("Invite already accepted.");
                throw new InviteAlreadyAcceptedException();
            }
        }
        return false;
    }

    private OrganisationResource getOrganisation(HttpServletRequest request) {
        return organisationService.getOrganisationByIdForAnonymousUserFlow(getOrganisationId(request));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerFormSubmit(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     HttpServletResponse response,
                                     HttpServletRequest request,
                                     Model model) {

        boolean setInviteEmailAddress;
        
        try {
        	setInviteEmailAddress = setInviteeEmailAddress(registrationForm, request, model);
        } catch (InviteAlreadyAcceptedException e) {
            cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
            return "redirect:/login";
        }
        
        if(setInviteEmailAddress){
            LOG.info("setInviteeEmailAddress: "+ registrationForm.getEmail());
            // re-validate since we did set the emailaddress in the meantime. @Valid annotation is needed for unit tests.
            bindingResult = new BeanPropertyBindingResult(registrationForm, "registrationForm");
            validator.validate(registrationForm, bindingResult);
        }

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        if(user != null){
            return getRedirectUrlForUser(user);
        }

        String destination = "registration-register";

        checkForExistingEmail(registrationForm.getEmail(), bindingResult);

        if(!bindingResult.hasErrors()) {
            //TODO : INFUND-3691
            RestResult<UserResource> createUserResult = createUser(registrationForm, getOrganisationId(request), getCompetitionId(request));

            if (createUserResult.isSuccess()) {
                removeCompetitionIdCookie(response);
                acceptInvite(response, request, createUserResult.getSuccessObject()); // might want to move this, to after email verifications.
                CookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_ID);
                destination = "redirect:/registration/success";
            } else {
                if (!processOrganisation(request, model)) {
                    destination = "redirect:/";
                }
                addEnvelopeErrorsToBindingResultErrors(createUserResult.getFailure().getErrors(), bindingResult);
            }
        } else {
            if (!processOrganisation(request, model)) {
                destination = "redirect:/";
            }
        }

        return destination;
    }

    @RequestMapping(value = "/resend-email-verification", method = RequestMethod.GET)
    public String resendEmailVerification(final ResendEmailVerificationForm resendEmailVerificationForm, final Model model) {
        model.addAttribute("resendEmailVerificationForm", resendEmailVerificationForm);
        return "registration/resend-email-verification";
    }

    @RequestMapping(value = "/resend-email-verification", method = RequestMethod.POST)
    public String resendEmailVerification(@Valid final ResendEmailVerificationForm resendEmailVerificationForm, final BindingResult bindingResult, final Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("resendEmailVerificationForm", resendEmailVerificationForm);
            return "registration/resend-email-verification";
        }

        userService.resendEmailVerificationNotification(resendEmailVerificationForm.getEmail());
        return "registration/resend-email-verification-send";
    }

    private void removeCompetitionIdCookie(HttpServletResponse response) {
        CookieUtil.removeCookie(response, ApplicationCreationController.COMPETITION_ID);
    }

    private Long getCompetitionId(HttpServletRequest request) {
        Long competitionId = null;
        if(StringUtils.hasText(CookieUtil.getCookieValue(request, ApplicationCreationController.COMPETITION_ID))){
            competitionId = Long.valueOf(CookieUtil.getCookieValue(request, ApplicationCreationController.COMPETITION_ID));
        }
        return competitionId;
    }

    private boolean acceptInvite(HttpServletResponse response, HttpServletRequest request, UserResource userResource) {
        String inviteHash = CookieUtil.getCookieValue(request, AcceptInviteController.INVITE_HASH);
        if(StringUtils.hasText(inviteHash)){
            RestResult<Void> restResult = inviteRestService.acceptInvite(inviteHash, userResource.getId());
            if(restResult.isSuccess()){
                CookieUtil.removeCookie(response, AcceptInviteController.INVITE_HASH);
            }
            return restResult.isSuccess();
        }
        return false;
    }

    private void checkForExistingEmail(String email, BindingResult bindingResult) {
        if(!bindingResult.hasFieldErrors(EMAIL_FIELD_NAME) && StringUtils.hasText(email)) {
            RestResult<UserResource> existingUserSearch = userService.findUserByEmailForAnonymousUserFlow(email);
            if (!HttpStatus.NOT_FOUND.equals(existingUserSearch.getStatusCode())) {
                ValidationMessages.rejectValue(bindingResult, EMAIL_FIELD_NAME, "validation.standard.email.exists");
            }
        }
    }


    private void addEnvelopeErrorsToBindingResultErrors(List<Error> errors, BindingResult bindingResult) {
        errors.forEach(
                error -> rejectField(error, bindingResult)
        );
    }

    private void rejectField(Error error, BindingResult bindingResult) {
        if (StringUtils.hasText(error.getFieldName())) {
            bindingResult.rejectValue(error.getFieldName(), "registration."+error.getErrorKey());
        } else {
            bindingResult.reject("registration."+error.getErrorKey());
        }
    }

    private RestResult<UserResource> createUser(RegistrationForm registrationForm, Long organisationId, Long competitionId) {
        return userService.createLeadApplicantForOrganisationWithCompetitionId(
                registrationForm.getFirstName(),
                registrationForm.getLastName(),
                registrationForm.getPassword(),
                registrationForm.getEmail(),
                registrationForm.getTitle(),
                registrationForm.getPhoneNumber(),
                organisationId,
                competitionId);
    }

    private void addOrganisationNameToModel(Model model, OrganisationResource organisation) {
        model.addAttribute("organisationName", organisation.getName());
    }

    private Long getOrganisationId(HttpServletRequest request) {
        String organisationParameter = CookieUtil.getCookieValue(request, ORGANISATION_ID_PARAMETER_NAME);
        Long organisationId = null;

        try {
            if (Long.parseLong(organisationParameter) >= 0) {
                organisationId = Long.parseLong(organisationParameter);
            }
        } catch (NumberFormatException e) {
            LOG.info("Invalid organisationId number format:" + e);
        }

        return organisationId;
    }

    private void setOrganisationIdCookie(RegistrationForm registrationForm, HttpServletRequest request, HttpServletResponse response) {
        Long organisationId = getOrganisationId(request);
        if(organisationId != null) {
        	CookieUtil.saveToCookie(response, ORGANISATION_ID_PARAMETER_NAME, Long.toString(organisationId));
        }
    }

    private boolean hasVerifiedCookieSet(final HttpServletRequest request) {
        final Optional<Cookie> cookie = CookieUtil.getCookie(request, "flashMessage");
        return cookie.isPresent() && cookie.get().getValue().equals("verificationSuccessful");
    }
}
