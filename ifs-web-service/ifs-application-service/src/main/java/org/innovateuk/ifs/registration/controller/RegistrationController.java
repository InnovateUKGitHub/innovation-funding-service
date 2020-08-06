package org.innovateuk.ifs.registration.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.exception.InviteAlreadyAcceptedException;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.registration.form.InviteAndIdCookie;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.form.RegistrationForm.ExternalUserRegistrationValidationGroup;
import org.innovateuk.ifs.registration.form.ResendEmailVerificationForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ORGANISATION_ALREADY_EXISTS_FOR_PROJECT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;
import static org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.RegistrationViewModelBuilder.aRegistrationViewModel;
import static org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.anInvitedUserViewModel;

@Controller
@RequestMapping("/registration")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = RegistrationController.class)
@PreAuthorize("permitAll")
public class RegistrationController {
    public static final String BASE_URL = "/registration/register";

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private RegistrationCookieService registrationCookieService;

    @Autowired
    private EncryptedCookieService cookieUtil;

    @Autowired
    private OrganisationRestService organisationRestService;
    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    protected CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private NavigationUtils navigationUtils;

    @Autowired
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    private static final Log LOG = LogFactory.getLog(RegistrationController.class);

    private final static String EMAIL_FIELD_NAME = "email";

    @GetMapping("/success")
    public String registrationSuccessful(Model model,
                                         @RequestHeader(value = "referer", required = false) final String referer,
                                         final HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("isApplicantJourney", registrationCookieService.isApplicantJourney(request));
        registrationCookieService.deleteInviteHashCookie(response);
        registrationCookieService.deleteProjectInviteHashCookie(response);
        if (referer == null || !referer.contains(request.getServerName() + "/registration/register")) {
            throw new ObjectNotFoundException("Attempt to access registration page directly...", emptyList());
        }
        return "registration/successful";
    }

    @GetMapping("/verified")
    public String verificationSuccessful(final HttpServletRequest request, final HttpServletResponse response) {
        if (!hasVerifiedCookieSet(request)) {
            throw new ObjectNotFoundException("Attempt to access registration page directly...", emptyList());
        } else {
            cookieFlashMessageFilter.removeFlashMessage(response);
            return "registration/verified";
        }
    }

    @GetMapping("/verify-email/{hash}")
    public String verifyEmailAddress(@PathVariable("hash") final String hash, final HttpServletResponse response) {
        userRestService.verifyEmail(hash).getSuccess();
        cookieFlashMessageFilter.setFlashMessage(response, "verificationSuccessful");
        return "redirect:/registration/verified";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("form") RegistrationForm registrationForm,
                               Model model,
                               UserResource user,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (user != null) {
            return navigationUtils.getRedirectToLandingPageUrl(request);
        }

        if (getOrganisationId(request) == null) {
            return navigationUtils.getRedirectToLandingPageUrl(request);
        }

        try {
            addRegistrationFormToModel(registrationForm, model, request, response);
        } catch (InviteAlreadyAcceptedException e) {
            LOG.info("invite already accepted", e);
            cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
            return "redirect:/login";
        }

        String destination = "registration/register";

        if (!processOrganisation(request, model)) {
            destination = "redirect:/";
        }

        return destination;
    }

    @PostMapping("/register")
    public String registerFormSubmit(@Validated({Default.class, ExternalUserRegistrationValidationGroup.class}) @ModelAttribute("form") RegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     HttpServletResponse response,
                                     UserResource user,
                                     HttpServletRequest request,
                                     Model model) {

        try {
            if (setInviteeEmailAddress(registrationForm, request, model)) {
                bindingResult = new BeanPropertyBindingResult(registrationForm, "form");
                validator.validate(registrationForm, bindingResult);
            }
        } catch (InviteAlreadyAcceptedException e) {
            LOG.info("invite already accepted", e);
            cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");

            return "redirect:/login";
        }

        checkForExistingEmail(registrationForm.getEmail(), bindingResult);
        model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);

        return validationHandler.failNowOrSucceedWith(
                () -> registerForm(registrationForm, model, user, request, response),
                () -> createUser(registrationForm, getOrganisationId(request), getCompetitionId(request)).handleSuccessOrFailure(
                        failure -> {
                            addValidationErrors(validationHandler, failure);
                            return registerForm(registrationForm, model, user, request, response);
                        },
                        userResource ->
                                acceptInvite(request, userResource).handleSuccessOrFailure(
                                        failure -> {
                                            removeInviteCookie(response);
                                            return handleAcceptInviteFailure(registrationForm, response, user, request, model, validationHandler, failure);
                                        },
                                        success -> {
                                            removeInviteCookie(response);
                                            return "redirect:/registration/success";
                                        })));
    }

    @GetMapping("/duplicate-project-organisation")
    public String displayErrorPage(HttpServletRequest request, Model model) {

        InviteAndIdCookie projectInvite = registrationCookieService.getProjectInviteHashCookieValue(request).get();
        SentProjectPartnerInviteResource invite = projectPartnerInviteRestService.getInviteByHash(projectInvite.getId(), projectInvite.getHash()).getSuccess();
        model.addAttribute("model", invite);
        return "registration/duplicate-organisation-error";
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


    private boolean processOrganisation(HttpServletRequest request, Model model) {
        RestResult<OrganisationResource> result = organisationRestService.getOrganisationByIdForAnonymousUserFlow(getOrganisationId(request));
        if (result.isSuccess()) {
            addOrganisationNameToModel(model, result.getSuccess());
            return true;
        }
        return false;
    }

    private void addRegistrationFormToModel(RegistrationForm registrationForm, Model model, HttpServletRequest request, HttpServletResponse response) {
        setOrganisationIdCookie(request, response);
        setInviteeEmailAddress(registrationForm, request, model);
        model.addAttribute("form", registrationForm);
    }

    /**
     * When the current user is an invitee, use the invited email address in the registration flow.
     */
    private boolean setInviteeEmailAddress(RegistrationForm registrationForm, HttpServletRequest request, Model model) {
        Optional<String> inviteHash = registrationCookieService.getInviteHashCookieValue(request);
        if (inviteHash.isPresent()) {
            RestResult<ApplicationInviteResource> invite = inviteRestService.getInviteByHash(inviteHash.get());
            if (invite.isSuccess() && InviteStatus.SENT.equals(invite.getSuccess().getStatus())) {
                ApplicationInviteResource inviteResource = invite.getSuccess();
                registrationForm.setEmail(inviteResource.getEmail());
                model.addAttribute("model", anInvitedUserViewModel());
                return true;
            } else {
                LOG.debug("Invite already accepted.");
                throw new InviteAlreadyAcceptedException();
            }
        }
        Optional<InviteAndIdCookie> projectInvite = registrationCookieService.getProjectInviteHashCookieValue(request);
        if (projectInvite.isPresent()) {
            RestResult<SentProjectPartnerInviteResource> invite = projectPartnerInviteRestService.getInviteByHash(projectInvite.get().getId(), projectInvite.get().getHash());
            if (invite.isSuccess() && InviteStatus.SENT.equals(invite.getSuccess().getStatus())) {
                SentProjectPartnerInviteResource inviteResource = invite.getSuccess();
                registrationForm.setEmail(inviteResource.getEmail());
                model.addAttribute("model", anInvitedUserViewModel());
                return true;
            } else {
                LOG.debug("Invite already accepted.");
                throw new InviteAlreadyAcceptedException();
            }
        }
        model.addAttribute("model", aRegistrationViewModel()
                .withInvitee(false)
                .withTermsRequired(true)
                .withPhoneRequired(true)
                .build());
        return false;
    }

    private String handleAcceptInviteFailure(@ModelAttribute("form") @Valid RegistrationForm registrationForm, HttpServletResponse response, UserResource user, HttpServletRequest request, Model model, ValidationHandler validationHandler, ServiceFailure failure) {
        if (failure.getErrors().stream().anyMatch(
                error -> error.getErrorKey().equals(ORGANISATION_ALREADY_EXISTS_FOR_PROJECT.name()))) {
            return "redirect:/registration/duplicate-project-organisation";
        }
        addValidationErrors(validationHandler, failure);
        return registerForm(registrationForm, model, user, request, response);
    }

    private void removeInviteCookie(HttpServletResponse response) {
        registrationCookieService.deleteCompetitionIdCookie(response);
        registrationCookieService.deleteOrganisationIdCookie(response);
    }

    private void addValidationErrors(ValidationHandler validationHandler, ServiceFailure failure) {
        validationHandler.addAnyErrors(failure,
                fieldErrorsToFieldErrors(
                        e -> newFieldError(e, e.getFieldName(), e.getFieldRejectedValue(), "registration." + e.getErrorKey())
                ),
                asGlobalErrors()
        );
    }

    private Long getCompetitionId(HttpServletRequest request) {
        return registrationCookieService.getCompetitionIdCookieValue(request).orElse(null);
    }

    private ServiceResult<Void> acceptInvite(HttpServletRequest request, UserResource userResource) {
        Optional<String> inviteHash = registrationCookieService.getInviteHashCookieValue(request);
        if (inviteHash.isPresent()) {
            Optional<Long> organisationId = registrationCookieService.getOrganisationIdCookieValue(request);
            return inviteRestService.acceptInvite(inviteHash.get(), userResource.getId(), organisationId.get()).toServiceResult();
        }

        Optional<InviteAndIdCookie> projectInvite = registrationCookieService.getProjectInviteHashCookieValue(request);
        if (projectInvite.isPresent()) {
            SentProjectPartnerInviteResource invite = projectPartnerInviteRestService.getInviteByHash(projectInvite.get().getId(), projectInvite.get().getHash()).getSuccess();
            Optional<Long> organisationId = registrationCookieService.getOrganisationIdCookieValue(request);
            return projectPartnerInviteRestService.acceptInvite(projectInvite.get().getId(), invite.getId(), organisationId.get()).toServiceResult();
        }
        return serviceSuccess();
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
        return userRestService.createUser(
                registrationForm.constructUserCreationResource()
                .withOrganisationId(organisationId)
                .withCompetitionId(competitionId)
                .withRole(Role.APPLICANT)
                .build())
                .toServiceResult();
    }

    private void addOrganisationNameToModel(Model model, OrganisationResource organisation) {
        model.addAttribute("organisationName", organisation.getName());
    }

    private Long getOrganisationId(HttpServletRequest request) {
        return registrationCookieService.getOrganisationIdCookieValue(request).orElse(null);
    }

    private void setOrganisationIdCookie(HttpServletRequest request, HttpServletResponse response) {
        Long organisationId = getOrganisationId(request);
        if (organisationId != null) {
            registrationCookieService.saveToOrganisationIdCookie(organisationId, response);
        }
    }

    private boolean hasVerifiedCookieSet(final HttpServletRequest request) {
        final Optional<Cookie> cookie = cookieUtil.getCookie(request, CookieFlashMessageFilter.COOKIE_NAME);
        return cookie.isPresent() && cookieUtil.getCookieValue(request, CookieFlashMessageFilter.COOKIE_NAME).equals("verificationSuccessful");
    }
}