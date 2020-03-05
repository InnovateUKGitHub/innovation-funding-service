package org.innovateuk.ifs.application.review.controller;

import org.innovateuk.ifs.application.forms.form.ApplicationSubmitForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.review.populator.ReviewAndSubmitViewModelPopulator;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;

@Controller
@RequestMapping("/application")
public class ReviewAndSubmitController {
    public static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ReviewAndSubmitViewModelPopulator reviewAndSubmitViewModelPopulator;
    @Autowired
    private ApplicationRestService applicationRestService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private UserService userService;
    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Autowired
    private QuestionStatusRestService questionStatusRestService;
    @Autowired
    private UserRestService userRestService;
    @Autowired
    private QuestionRestService questionRestService;

    @Value("${ifs.early.metrics.url}")
    private String earlyMetricsUrl;


    @SecuredBySpring(value = "READ", description = "Applicants can review and submit their applications")
    @PreAuthorize("hasAnyAuthority('applicant')")
    @GetMapping("/{applicationId}/review-and-submit")
    @AsyncMethod
    public String reviewAndSubmit(@ModelAttribute(value = FORM_ATTR_NAME, binding = false) ApplicationSubmitForm form,
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
                                    @ModelAttribute(FORM_ATTR_NAME) ApplicationSubmitForm form,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("termsAgreed", true);
        return format("redirect:/application/%d/confirm-submit", applicationId);
    }

    @SecuredBySpring(value = "APPLICATION_REVIEW_AND_SUBMIT_RETURN_AND_EDIT",
            description = "Applicants can return to edit questions from the review and submit page")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/review-and-submit", params = "edit")
    public String editQuestion(@PathVariable long applicationId,
                               @RequestParam("edit") long questionId) {
        return redirectToQuestion(applicationId, questionId);
    }

    @SecuredBySpring(value = "APPLICATION_REVIEW_AND_SUBMIT_MARK_AS_COMPLETE",
            description = "Applicants can mark questions as complete from the review and submit page")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/review-and-submit", params = "complete")
    public String completeQuestion(@PathVariable long applicationId,
                                   @RequestParam("complete") long questionId,
                                     UserResource user) {
        ProcessRoleResource processRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        List<ValidationMessages> messages = questionStatusRestService.markAsComplete(questionId, applicationId, processRole.getId()).getSuccess();
        if (messages.isEmpty()) {
            return redirectToReview(applicationId);
        } else {
            return handleMarkAsCompleteFailure(applicationId, questionId, processRole);
        }
    }
    @SecuredBySpring(value = "APPLICATION_REVIEW_AND_SUBMIT_ASSIGN",
            description = "Applicants can assign questions from the review and submit page")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/review-and-submit", params = "assign")
    public String assignQuestionToLead(@PathVariable long applicationId,
                                 @RequestParam("assign") long questionId,
                                       UserResource user) {

        ProcessRoleResource assignTo = userService.getLeadApplicantProcessRole(applicationId);
        ProcessRoleResource assignFrom = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.assign(questionId, applicationId, assignTo.getId(), assignFrom.getId()).getSuccess();
        return redirectToReview(applicationId);
    }

    @SecuredBySpring(value = "APPLICATION_REVIEW_AND_SUBMIT_MARK_AS_INCOMPLETE",
            description = "Applicants can mark questions as incomplete from the review and submit page")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/review-and-submit", params = "incomplete")
    public String incompleteQuestion(@PathVariable long applicationId,
                                     @RequestParam("incomplete") long questionId,
                                     UserResource user) {
        ProcessRoleResource processRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.markAsInComplete(questionId, applicationId, processRole.getId());
        return redirectToQuestion(applicationId, questionId);
    }

    private String redirectToQuestion(long applicationId, long questionId) {
        return format("redirect:/application/%d/form/question/%d", applicationId, questionId);
    }

    private String redirectToReview(long applicationId) {
        return format("redirect:/application/%d/review-and-submit", applicationId);
    }


    @SecuredBySpring(value = "APPLICANT_CONFIRM_SUBMIT", description = "Applicants can confirm they wish to submit their applications")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(@PathVariable long applicationId,
                                           @ModelAttribute("termsAgreed") Boolean termsAgreed,
                                           @ModelAttribute(FORM_ATTR_NAME) ApplicationSubmitForm form,
                                           Model model) {
        if (!TRUE.equals(termsAgreed)) {
            return format("redirect:/application/%d/summary", applicationId);
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

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();

        if (!ableToSubmitApplication(user, application)) {
            cookieFlashMessageFilter.setFlashMessage(response, "cannotSubmit");
            return "redirect:/application/" + applicationId + "/confirm-submit";
        }

        RestResult<Void> updateResult = applicationRestService.updateApplicationState(applicationId, SUBMITTED);

        Supplier<String> failureView = () -> applicationConfirmSubmit(applicationId, true, form, model);

        return validationHandler.addAnyErrors(updateResult)
                .failNowOrSucceedWith(failureView, () -> format("redirect:/application/%d/track", applicationId));
    }

    @SecuredBySpring(value = "APPLICANT_TRACK", description = "Applicants can track their application after submitting.")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/track")
    public String applicationTrack(Model model,
                                   @PathVariable long applicationId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();

        if (!application.isSubmitted()) {
            return "redirect:/application/" + applicationId;
        }

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        model.addAttribute("completedQuestionsPercentage", application.getCompletion());
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("earlyMetricsUrl", earlyMetricsUrl);

        return getTrackingPage(competition);
    }

    private String getTrackingPage(CompetitionResource competition) {
        if (competition.isH2020()) {
            return "h2020-grant-transfer-track";
        } else if (competition.isLoan()) {
            return "loan-application-track";
        } else {
            return "application-track";
        }
    }

    private boolean ableToSubmitApplication(UserResource user, ApplicationResource application) {
        return userService.isLeadApplicant(user.getId(), application) && application.isSubmittable();
    }

    private String handleMarkAsCompleteFailure(long applicationId, long questionId, ProcessRoleResource processRole) {
        questionStatusRestService.markAsInComplete(questionId, applicationId, processRole.getId());
        return "redirect:/application/" + applicationId + "/form/question/edit/" + questionId + "?show-errors=true";
    }
}
