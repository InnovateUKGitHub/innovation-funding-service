package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.application.forms.form.ApplicationSubmitForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.Form;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.ASSIGN_QUESTION_PARAM;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MARK_AS_COMPLETE;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.commons.error.ValidationMessages.collectValidationMessages;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * This controller will handle all submit requests that are related to the application overview.
 */

@Controller
@RequestMapping("/application")
public class ApplicationSubmitController {

    private QuestionService questionService;
    private QuestionRestService questionRestService;
    private UserRestService userRestService;
    private ApplicationService applicationService;
    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;
    private ApplicationModelPopulator applicationModelPopulator;
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    private static final String FORM_ATTR_NAME = "form";
    public static final String APPLICATION_SUBMIT_FROM_ATTR_NAME = "applicationSubmitForm";

    public ApplicationSubmitController() {
    }

    @Autowired
    public ApplicationSubmitController(QuestionService questionService,
                                       QuestionRestService questionRestService,
                                       UserRestService userRestService,
                                       ApplicationService applicationService,
                                       ApplicationRestService applicationRestService,
                                       CompetitionRestService competitionRestService,
                                       ApplicationModelPopulator applicationModelPopulator,
                                       CookieFlashMessageFilter cookieFlashMessageFilter) {
        this.questionService = questionService;
        this.questionRestService = questionRestService;
        this.userRestService = userRestService;
        this.applicationService = applicationService;
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.applicationModelPopulator = applicationModelPopulator;
        this.cookieFlashMessageFilter = cookieFlashMessageFilter;
    }

    private boolean ableToSubmitApplication(UserResource user, ApplicationResource application) {
        return applicationModelPopulator.userIsLeadApplicant(application, user.getId()) && application.isSubmittable();
    }

    @SecuredBySpring(value = "APPLICANT_ASSIGN_OR_COMPLETE", description = "Applicants can assign or complete questions from the application form.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping("/{applicationId}/summary")
    public String completeOrAssign(@PathVariable("applicationId") long applicationId,
                                           UserResource user,
                                           HttpServletRequest request) {

        Map<String, String[]> params = request.getParameterMap();

        if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
            ProcessRoleResource assignedBy = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
            questionService.assignQuestion(applicationId, request, assignedBy);
        } else if (params.containsKey(MARK_AS_COMPLETE)) {
            Long markQuestionCompleteId = Long.valueOf(request.getParameter(MARK_AS_COMPLETE));
            if (markQuestionCompleteId != null) {
                ProcessRoleResource processRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
                List<ValidationMessages> markAsCompleteErrors = questionService.markAsComplete(markQuestionCompleteId, applicationId, processRole.getId());

                if (collectValidationMessages(markAsCompleteErrors).hasErrors()) {
                    questionService.markAsIncomplete(markQuestionCompleteId, applicationId, processRole.getId());

                    if (isResearchCategoryQuestion(markQuestionCompleteId)) {
                        return "redirect:/application/" + applicationId + "/form/question/" + markQuestionCompleteId + "/research-category?mark_as_complete=true";
                    }

                    return "redirect:/application/" + applicationId + "/form/question/edit/" + markQuestionCompleteId + "?mark_as_complete=true";
                }
            }
        }

        return "redirect:/application/" + applicationId + "/summary";
    }

    @SecuredBySpring(value = "APPLICATION_SUBMIT", description = "Applicants can submit their applications.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/summary", params = "submit-application")
    public String submitApplication(@PathVariable("applicationId") long applicationId,
                                    @ModelAttribute("applicationSubmitForm") ApplicationSubmitForm applicationSubmitForm,
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
                redirectAttributes.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + APPLICATION_SUBMIT_FROM_ATTR_NAME, bindingResult);
                redirectAttributes.addFlashAttribute(APPLICATION_SUBMIT_FROM_ATTR_NAME, applicationSubmitForm);
                return String.format("redirect:/application/%d/summary", applicationId);
            }

        }
        redirectAttributes.addFlashAttribute("termsAgreed", true);
        return String.format("redirect:/application/%d/confirm-submit", applicationId);
    }

    @SecuredBySpring(value = "APPLICANT_CONFIRM_SUBMIT", description = "Applicants can confirm they wish to submit their applications")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(@PathVariable("applicationId") long applicationId,
                                           @ModelAttribute("termsAgreed") Boolean termsAgreed,
                                           @ModelAttribute(FORM_ATTR_NAME) Form form,
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
                                    @ModelAttribute(FORM_ATTR_NAME) Form form,
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
                                   @PathVariable("applicationId") long applicationId) {
        ApplicationResource application = applicationService.getById(applicationId);

        if (!application.isSubmitted()) {
            return "redirect:/application/" + applicationId;
        }

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        applicationModelPopulator.addApplicationWithoutDetails(application, competition, model);
        return "application-track";
    }

    private boolean isResearchCategoryQuestion(Long questionId) {
        QuestionResource question = questionRestService.findById(questionId).getSuccess();
        return question.getQuestionSetupType() == RESEARCH_CATEGORY;
    }
}


