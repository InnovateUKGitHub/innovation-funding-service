package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.application.creation.controller.ApplicationCreationController;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.exception.InviteAlreadyAcceptedException;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.EthnicityRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.form.ResendEmailVerificationForm;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;
import static org.innovateuk.ifs.login.HomeController.getRedirectUrlForUser;
import static org.innovateuk.ifs.registration.AbstractAcceptInviteController.INVITE_HASH;
import static org.innovateuk.ifs.registration.OrganisationCreationController.ORGANISATION_ID;

@Controller
@RequestMapping("/registration")
@PreAuthorize("permitAll")
public class RegistrationController {
    public static final String BASE_URL = "/registration/register";

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Autowired
    @Qualifier("mvcValidator")
    Validator validator;
    @Autowired
    private UserService userService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private EthnicityRestService ethnicityRestService;

    @Autowired
    protected CookieFlashMessageFilter cookieFlashMessageFilter;

    private static final Log LOG = LogFactory.getLog(RegistrationController.class);

    public final static String EMAIL_FIELD_NAME = "email";

    @GetMapping("/success")
    public String registrationSuccessful(
            @RequestHeader(value = "referer", required = false) final String referer,
            final HttpServletRequest request, HttpServletResponse response) {
        cookieUtil.removeCookie(response, INVITE_HASH);
        if (referer == null || !referer.contains(request.getServerName() + "/registration/register")) {
            throw new ObjectNotFoundException("Attempt to access registration page directly...", Collections.emptyList());
        }
        return "registration/successful";
    }

    @GetMapping("/verified")
    public String verificationSuccessful(final HttpServletRequest request, final HttpServletResponse response) {
        if (!hasVerifiedCookieSet(request)) {
            throw new ObjectNotFoundException("Attempt to access registration page directly...", Collections.emptyList());
        } else {
            cookieFlashMessageFilter.removeFlashMessage(response);
            return "registration/verified";
        }
    }

    @GetMapping("/verify-email/{hash}")
    public String verifyEmailAddress(@PathVariable("hash") final String hash,
                                     final HttpServletResponse response) {
        userService.verifyEmail(hash);
        cookieFlashMessageFilter.setFlashMessage(response, "verificationSuccessful");
        return "redirect:/registration/verified";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("registrationForm") RegistrationForm registrationForm,
                               Model model,
                               UserResource user,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (user != null) {
            return getRedirectUrlForUser(user);
        }

        if (getOrganisationId(request) == null) {
            return "redirect:/";
        }

        try {
            addRegistrationFormToModel(registrationForm, model, request, response);
        } catch (InviteAlreadyAcceptedException e) {
            cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
            return "redirect:/login";
        }

        String destination = "registration/register";

        if (!processOrganisation(request, model)) {
            destination = "redirect:/";
        }

        return destination;
    }

    private List<EthnicityResource> getEthnicityOptions() {
        return ethnicityRestService.findAllActive().getSuccessObjectOrThrowException();
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

    private void addRegistrationFormToModel(RegistrationForm registrationForm, Model model, HttpServletRequest request, HttpServletResponse response) {
        setOrganisationIdCookie(registrationForm, request, response);
        setInviteeEmailAddress(registrationForm, request, model);
        model.addAttribute("registrationForm", registrationForm);
        model.addAttribute("ethnicityOptions", getEthnicityOptions());
    }

    /**
     * When the current user is a invitee, user the invite email-address in the registration flow.
     */
    private boolean setInviteeEmailAddress(RegistrationForm registrationForm, HttpServletRequest request, Model model) {
        String inviteHash = cookieUtil.getCookieValue(request, INVITE_HASH);
        if (StringUtils.hasText(inviteHash)) {
            RestResult<ApplicationInviteResource> invite = inviteRestService.getInviteByHash(inviteHash);
            if (invite.isSuccess() && InviteStatus.SENT.equals(invite.getSuccessObject().getStatus())) {
                ApplicationInviteResource inviteResource = invite.getSuccessObject();
                registrationForm.setEmail(inviteResource.getEmail());
                model.addAttribute("invitee", true);
                return true;
            } else {
                LOG.debug("Invite already accepted.");
                throw new InviteAlreadyAcceptedException();
            }
        }
        return false;
    }

    private OrganisationResource getOrganisation(HttpServletRequest request) {
        return organisationService.getOrganisationByIdForAnonymousUserFlow(getOrganisationId(request));
    }

    @PostMapping("/register")
    public String registerFormSubmit(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     HttpServletResponse response,
                                     UserResource user,
                                     HttpServletRequest request,
                                     Model model) {

        try {
            if (setInviteeEmailAddress(registrationForm, request, model)) {
                bindingResult = new BeanPropertyBindingResult(registrationForm, "registrationForm");
                validator.validate(registrationForm, bindingResult);
            }
        } catch (InviteAlreadyAcceptedException e) {
            cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");

            return "redirect:/login";
        }

        checkForExistingEmail(registrationForm.getEmail(), bindingResult);

        model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "registrationForm", bindingResult);

        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);

        // TODO : INFUND-3691
        return validationHandler.failNowOrSucceedWith(
                () -> registerForm(registrationForm, model, user, request, response),
                () -> createUser(registrationForm, getOrganisationId(request), getCompetitionId(request)).handleSuccessOrFailure(
                        failure -> {
                            validationHandler.addAnyErrors(failure,
                                    fieldErrorsToFieldErrors(
                                            e -> newFieldError(e, e.getFieldName(), e.getFieldRejectedValue(), "registration." + e.getErrorKey())
                                    ),
                                    asGlobalErrors()
                            );

                            return registerForm(registrationForm, model, user, request, response);
                        },
                        userResource -> {
                            removeCompetitionIdCookie(response);
                            acceptInvite(response, request, userResource); // might want to move this, to after email verifications.
                            cookieUtil.removeCookie(response, ORGANISATION_ID);

                            return "redirect:/registration/success";
                        }
                )
        );
    }

    @GetMapping("/resend-email-verification")
    public String resendEmailVerification(final ResendEmailVerificationForm resendEmailVerificationForm, final Model model) {
        model.addAttribute("resendEmailVerificationForm", resendEmailVerificationForm);
        return "registration/resend-email-verification";
    }

    @PostMapping("/resend-email-verification")
    public String resendEmailVerification(@Valid final ResendEmailVerificationForm resendEmailVerificationForm, final BindingResult bindingResult, final Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("resendEmailVerificationForm", resendEmailVerificationForm);
            return "registration/resend-email-verification";
        }

        userService.resendEmailVerificationNotification(resendEmailVerificationForm.getEmail());
        return "registration/resend-email-verification-send";
    }

    private void removeCompetitionIdCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ApplicationCreationController.COMPETITION_ID);
    }

    private Long getCompetitionId(HttpServletRequest request) {
        Long competitionId = null;
        if (StringUtils.hasText(cookieUtil.getCookieValue(request, ApplicationCreationController.COMPETITION_ID))) {
            competitionId = Long.valueOf(cookieUtil.getCookieValue(request, ApplicationCreationController.COMPETITION_ID));
        }
        return competitionId;
    }

    private boolean acceptInvite(HttpServletResponse response, HttpServletRequest request, UserResource userResource) {
        String inviteHash = cookieUtil.getCookieValue(request, INVITE_HASH);
        if (StringUtils.hasText(inviteHash)) {
            RestResult<Void> restResult = inviteRestService.acceptInvite(inviteHash, userResource.getId());
            if (restResult.isSuccess()) {
                cookieUtil.removeCookie(response, INVITE_HASH);
            }
            return restResult.isSuccess();
        }
        return false;
    }

    private void checkForExistingEmail(String email, BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors(EMAIL_FIELD_NAME) && StringUtils.hasText(email)) {
            Optional<UserResource> existingUserSearch = userService.findUserByEmail(email);

            if (existingUserSearch.isPresent()) {
                bindingResult.rejectValue(EMAIL_FIELD_NAME, "validation.standard.email.exists");
            }
        }
    }

    private ServiceResult<UserResource> createUser(RegistrationForm registrationForm, Long organisationId, Long competitionId) {
        return userService.createLeadApplicantForOrganisationWithCompetitionId(
                registrationForm.getFirstName(),
                registrationForm.getLastName(),
                registrationForm.getPassword(),
                registrationForm.getEmail(),
                registrationForm.getTitle(),
                registrationForm.getPhoneNumber(),
                registrationForm.getGender(),
                Long.parseLong(registrationForm.getEthnicity()),
                registrationForm.getDisability(),
                organisationId,
                competitionId,
                registrationForm.getAllowMarketingEmails());
    }

    private void addOrganisationNameToModel(Model model, OrganisationResource organisation) {
        model.addAttribute("organisationName", organisation.getName());
    }

    private Long getOrganisationId(HttpServletRequest request) {
        String organisationParameter = cookieUtil.getCookieValue(request, ORGANISATION_ID);
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
        if (organisationId != null) {
            cookieUtil.saveToCookie(response, ORGANISATION_ID, Long.toString(organisationId));
        }
    }

    private boolean hasVerifiedCookieSet(final HttpServletRequest request) {
        final Optional<Cookie> cookie = cookieUtil.getCookie(request, CookieFlashMessageFilter.COOKIE_NAME);
        return cookie.isPresent() && cookieUtil.getCookieValue(request, CookieFlashMessageFilter.COOKIE_NAME).equals("verificationSuccessful");
    }
}
