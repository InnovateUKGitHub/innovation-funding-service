package com.worth.ifs.registration;

import com.worth.ifs.application.AcceptInviteController;
import com.worth.ifs.application.ApplicationCreationController;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.UserService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.exception.InvalidURLException;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UidAuthenticationService;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.registration.form.RegistrationForm;
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
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

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
    private UidAuthenticationService uidAuthenticationService;

    @Autowired
    protected UserAuthenticationService userAuthenticationService;

    private final Log log = LogFactory.getLog(getClass());

    public final static String ORGANISATION_ID_PARAMETER_NAME = "organisationId";
    public final static String EMAIL_FIELD_NAME = "email";

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String registrationSuccessful() {
        return "registration/successful";
    }

    @RequestMapping(value = "/verified", method = RequestMethod.GET)
    public String verificationSuccessful() {
        return "registration/verified";
    }

    @RequestMapping(value = "/verify-email/{hash}", method = RequestMethod.GET)
    public String verifyEmailAddress(@PathVariable("hash") final String hash){
        if(userService.verifyEmail(hash).isSuccess()){
            return "redirect:/registration/verified";
        }else{
            throw new InvalidURLException();
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm(Model model, HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        if(user != null){
            return getRedirectUrlForUser(user);
        }

        String destination = "registration-register";

        if (!processOrganisation(request, model)) {
            destination = "redirect:/";
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

    /**
     * When the current user is a invitee, user the invite email-address in the registration flow.
     */
    private boolean setInviteeEmailAddress(RegistrationForm registrationForm, HttpServletRequest request, Model model) {
        String inviteHash = CookieUtil.getCookieValue(request, AcceptInviteController.INVITE_HASH);
        if(StringUtils.hasText(inviteHash)){
            RestResult<InviteResource> invite = inviteRestService.getInviteByHash(inviteHash);
            if(invite.isSuccess() && InviteStatusConstants.SEND.equals(invite.getSuccessObject().getStatus())){
                InviteResource inviteResource = invite.getSuccessObject();
                registrationForm.setEmail(inviteResource.getEmail());
                model.addAttribute("invitee", true);
                return true;
            }else{
                log.debug("Invite already accepted.");
            }
        }
        return false;
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

        log.warn("registerFormSubmit");
        if(setInviteeEmailAddress(registrationForm, request, model)){
            log.warn("setInviteeEmailAddress"+ registrationForm.getEmail());
            // re-validate since we did set the emailaddress in the meantime. @Valid annotation is needed for unit tests.
            bindingResult = new BeanPropertyBindingResult(registrationForm, "registrationForm");
            validator.validate(registrationForm, bindingResult);
        }

        User user = userAuthenticationService.getAuthenticatedUser(request);
        if(user != null){
            return getRedirectUrlForUser(user);
        }

        String destination = "registration-register";

        checkForExistingEmail(registrationForm.getEmail(), bindingResult);

        if(!bindingResult.hasErrors()) {
            RestResult<UserResource> createUserResult = createUser(registrationForm, getOrganisationId(request), getCompetitionId(request));

            if (createUserResult.isSuccess()) {
                removeCompetitionIdCookie(response);
                acceptInvite(request, response, createUserResult.getSuccessObject()); // might want to move this, to after email verifications.
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

    private boolean acceptInvite(HttpServletRequest request, HttpServletResponse response, UserResource userResource) {
        String inviteHash = CookieUtil.getCookieValue(request, AcceptInviteController.INVITE_HASH);
        if(StringUtils.hasText(inviteHash)){
            RestResult<InviteResource> restResult = inviteRestService.getInviteByHash(inviteHash).andOnSuccessReturn(i -> {
                HttpStatus statusCode = inviteRestService.acceptInvite(inviteHash, userResource.getId()).getStatusCode();
                return i;
            });
            CookieUtil.removeCookie(response, AcceptInviteController.INVITE_HASH);
            return restResult.isSuccess();
        }
        return false;
    }

    private void checkForExistingEmail(String email, BindingResult bindingResult) {
        if(!bindingResult.hasFieldErrors(EMAIL_FIELD_NAME) && StringUtils.hasText(email)) {
            RestResult existingUserSearch = userService.findUserByEmail(email);
            if (!HttpStatus.NOT_FOUND.equals(existingUserSearch.getStatusCode())) {
                bindingResult.addError(new FieldError(EMAIL_FIELD_NAME, EMAIL_FIELD_NAME, email, false, null, null, "Email address is already in use"));
            }
        }
    }

    private void addEnvelopeErrorsToBindingResultErrors(List<Error> errors, BindingResult bindingResult) {
        errors.forEach(
                error -> bindingResult.reject("registration."+error.getErrorKey())
        );
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
        registrationForm.setActionUrl(BASE_URL + "?" + ORGANISATION_ID_PARAMETER_NAME + "=" + organisationId);
    }
}
