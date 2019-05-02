package org.innovateuk.ifs.application.review.controller;

import org.innovateuk.ifs.application.forms.form.ApplicationSubmitForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.review.populator.ReviewAndSubmitViewModelPopulator;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;

@Controller
@RequestMapping("/application")
public class ReviewAndSubmitController {
    private static final String FORM_ATTR_NAME = "applicationSubmitForm";

    private ReviewAndSubmitViewModelPopulator reviewAndSubmitViewModelPopulator;
    private QuestionService questionService;
    private QuestionRestService questionRestService;
    private UserRestService userRestService;
    private ApplicationService applicationService;
    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;
    private ApplicationModelPopulator applicationModelPopulator;
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    public ReviewAndSubmitController(ReviewAndSubmitViewModelPopulator reviewAndSubmitViewModelPopulator, QuestionService questionService, QuestionRestService questionRestService, UserRestService userRestService, ApplicationService applicationService, ApplicationRestService applicationRestService, CompetitionRestService competitionRestService, ApplicationModelPopulator applicationModelPopulator, CookieFlashMessageFilter cookieFlashMessageFilter) {
        this.reviewAndSubmitViewModelPopulator = reviewAndSubmitViewModelPopulator;
        this.questionService = questionService;
        this.questionRestService = questionRestService;
        this.userRestService = userRestService;
        this.applicationService = applicationService;
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.applicationModelPopulator = applicationModelPopulator;
        this.cookieFlashMessageFilter = cookieFlashMessageFilter;
    }

    @SecuredBySpring(value = "READ", description = "Applicants can review and submit their applications")
    @PreAuthorize("hasAnyAuthority('applicant')")
    @GetMapping("/{applicationId}/review-and-submit")
    public String reviewAndSubmit(@ModelAttribute(value = FORM_ATTR_NAME, binding = false) ApplicationSubmitForm applicationSubmitForm,
                                  BindingResult bindingResult,
                                  @PathVariable long applicationId,
                                  Model model,
                                  UserResource user) {
        model.addAttribute("model", reviewAndSubmitViewModelPopulator.populate(applicationId, user));
        return "application/review-and-submit";
    }

    @SecuredBySpring(value = "APPLICATION_SUBMIT", description = "Applicants can submit their applications.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping("/{applicationId}/review-and-submit")
    public String submitApplication(@PathVariable long applicationId,
                                    @ModelAttribute(FORM_ATTR_NAME) ApplicationSubmitForm applicationSubmitForm,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        if (competition.isFullyFunded()) {
            if (!applicationSubmitForm.isAgreeTerms()) {
                String errorCode = competition.isH2020()?
                        "validation.application.h2020.terms.required" :
                        "validation.application.procurement.terms.required";
                bindingResult.rejectValue("agreeTerms",errorCode);
                redirectAttributes.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + FORM_ATTR_NAME, bindingResult);
                redirectAttributes.addFlashAttribute(FORM_ATTR_NAME, applicationSubmitForm);
                return String.format("redirect:/application/%d/summary", applicationId);
            }

        }
        redirectAttributes.addFlashAttribute("termsAgreed", true);
        return String.format("redirect:/application/%d/confirm-submit", applicationId);
    }

    @SecuredBySpring(value = "APPLICANT_CONFIRM_SUBMIT", description = "Applicants can confirm they wish to submit their applications")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(@PathVariable long applicationId,
                                           @ModelAttribute("termsAgreed") Boolean termsAgreed,
                                           @ModelAttribute(FORM_ATTR_NAME) ApplicationSubmitForm form,
                                           Model model) {
        if (!Boolean.TRUE.equals(termsAgreed)) {
            return String.format("redirect:/application/%d/summary", applicationId);
        }
        model.addAttribute("applicationId", applicationId);
        return "application-confirm-submit";
    }

    @SecuredBySpring(value = "APPLICANT_CONFIRM_SUBMIT", description = "Applicants can confirm they wish to submit their applications")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping("/{applicationId}/confirm-submit")
    public String applicationSubmit(Model model,
                                    @ModelAttribute(FORM_ATTR_NAME) ApplicationSubmitForm form,
                                    @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    @PathVariable("applicationId") long applicationId,
                                    UserResource user,
                                    HttpServletResponse response) {

        ApplicationResource application = applicationService.getById(applicationId);

        if (!ableToSubmitApplication(user, application)) {
            cookieFlashMessageFilter.setFlashMessage(response, "cannotSubmit");
            return "redirect:/application/" + applicationId + "/confirm-submit";
        }

        RestResult<Void> updateResult = applicationRestService.updateApplicationState(applicationId, SUBMITTED);

        Supplier<String> failureView = () -> applicationConfirmSubmit(applicationId, true, form, model);

        return validationHandler.addAnyErrors(updateResult)
                .failNowOrSucceedWith(failureView, () -> String.format("redirect:/application/%d/track", applicationId));
    }

    @SecuredBySpring(value = "APPLICANT_TRACK", description = "Applicants can track their application after submitting.")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/track")
    public String applicationTrack(Model model,
                                   @PathVariable long applicationId) {
        ApplicationResource application = applicationService.getById(applicationId);

        if (!application.isSubmitted()) {
            return "redirect:/application/" + applicationId;
        }

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        applicationModelPopulator.addApplicationWithoutDetails(application, competition, model);

        return competition.isH2020() ?
                "h2020-grant-transfer-track" : "application-track";
    }

    private boolean ableToSubmitApplication(UserResource user, ApplicationResource application) {
        return applicationModelPopulator.userIsLeadApplicant(application, user.getId()) && application.isSubmittable();
    }
}
