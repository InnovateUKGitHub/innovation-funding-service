package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.form.AssignQuestionForm;
import org.innovateuk.ifs.application.forms.populator.AssignQuestionModelPopulator;
import org.innovateuk.ifs.application.forms.populator.QuestionModelPopulator;
import org.innovateuk.ifs.application.forms.questions.researchcategory.form.ResearchCategoryForm;
import org.innovateuk.ifs.application.forms.questions.researchcategory.populator.ApplicationResearchCategoryFormPopulator;
import org.innovateuk.ifs.application.forms.questions.researchcategory.populator.ApplicationResearchCategoryModelPopulator;
import org.innovateuk.ifs.application.forms.saver.ApplicationQuestionSaver;
import org.innovateuk.ifs.application.forms.service.ApplicationRedirectionService;
import org.innovateuk.ifs.application.forms.viewmodel.QuestionViewModel;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.*;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;

/**
 * This controller will handle all question requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
@SecuredBySpring(value = "Controller",
        description = "Applicants are allowed to view questions on their own applications",
        securedType = ApplicationQuestionController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationQuestionController {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationQuestionController.class);

    private QuestionModelPopulator questionModelPopulator;

    private ApplicationResearchCategoryModelPopulator researchCategoryPopulator;

    private ApplicationResearchCategoryFormPopulator researchCategoryFormPopulator;

    private ApplicationNavigationPopulator applicationNavigationPopulator;

    private ApplicationService applicationService;

    private UserRestService userRestService;

    private QuestionService questionService;

    private ApplicantRestService applicantRestService;

    private ApplicationRedirectionService applicationRedirectionService;

    private ApplicationQuestionSaver applicationSaver;

    private AssignQuestionModelPopulator assignQuestionModelPopulator;

    @Autowired
    public ApplicationQuestionController(
            ApplicationResearchCategoryModelPopulator researchCategoryPopulator,
            QuestionModelPopulator questionModelPopulator,
            ApplicationResearchCategoryFormPopulator researchCategoryFormPopulator,
            ApplicationNavigationPopulator applicationNavigationPopulator,
            ApplicationService applicationService,
            UserRestService userRestService,
            QuestionService questionService,
            AssignQuestionModelPopulator assignQuestionModelPopulator,
            ApplicantRestService applicantRestService,
            ApplicationRedirectionService applicationRedirectionService,
            ApplicationQuestionSaver applicationSaver
    ) {
        this.researchCategoryPopulator = researchCategoryPopulator;
        this.questionModelPopulator = questionModelPopulator;
        this.researchCategoryFormPopulator = researchCategoryFormPopulator;
        this.applicationNavigationPopulator = applicationNavigationPopulator;
        this.applicationService = applicationService;
        this.userRestService = userRestService;
        this.questionService = questionService;
        this.assignQuestionModelPopulator = assignQuestionModelPopulator;
        this.applicantRestService = applicantRestService;
        this.applicationRedirectionService = applicationRedirectionService;
        this.applicationSaver = applicationSaver;
    }

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder, WebRequest webRequest) {
        dataBinder.registerCustomEditor(String.class, new StringMultipartFileEditor());
    }

    @GetMapping(value = {QUESTION_URL + "{" + QUESTION_ID + "}", QUESTION_URL + "edit/{" + QUESTION_ID + "}"})
    public String showQuestion(
            @ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) ApplicationForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @PathVariable(APPLICATION_ID) final Long applicationId,
            @PathVariable(QUESTION_ID) final Long questionId,
            @RequestParam("mark_as_complete") final Optional<Boolean> markAsComplete,
            UserResource user,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        markAsComplete.ifPresent(markAsCompleteSet -> {
            if (markAsCompleteSet) {
                ValidationMessages errors = applicationSaver.saveApplicationForm(
                        applicationId,
                        form,
                        questionId,
                        user.getId(),
                        request,
                        response,
                        Optional.of(TRUE)
                );
                validationHandler.addAnyErrors(errors);
            }
        });

        return viewQuestion(user, applicationId, questionId, model, form, markAsComplete);
    }

    @ZeroDowntime(description = "remove references to assign", reference = "IFS-TODO")
    @PostMapping(value = {
            QUESTION_URL + "{" + QUESTION_ID + "}",
            QUESTION_URL + "edit/{" + QUESTION_ID + "}"
    })
    public String questionFormSubmit(
            @ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
            BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @PathVariable(APPLICATION_ID) final Long applicationId,
            @PathVariable(QUESTION_ID) final Long questionId,
            UserResource user,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Map<String, String[]> params = request.getParameterMap();

        // Check if the request is to just open edit view or to save
        if (params.containsKey(EDIT_QUESTION)) {
            return handleEditQuestion(form, model, applicationId, questionId, user);
        } else {
            // First check if any errors already exist in bindingResult
            ValidationMessages errors = checkErrorsInFormAndSave(form, applicationId, questionId, user.getId(), request, response);

            model.addAttribute("form", form);

            /* End save action */
            if (hasErrors(request, errors, bindingResult)) {
                // Add any validated fields back in invalid entries are displayed on re-render
                validationHandler.addAnyErrors(errors);
                return viewQuestion(user, applicationId, questionId, model, form, Optional.empty());
            } else {
                return applicationRedirectionService.getRedirectUrl(request, applicationId, Optional.empty());
            }
        }
    }

    @GetMapping("/question/{questionId}/assign")
    public String getAssignPage(@ModelAttribute(name = "form", binding = false) AssignQuestionForm form,
                                @PathVariable("questionId") long questionId,
                                @PathVariable("applicationId") long applicationId,
                                @RequestParam MultiValueMap<String, String> queryParams,
                                Model model) {
        populateAssigneeForm(questionId, applicationId, form);
        return doViewAssignPage(model, questionId, applicationId, "");
    }

    @PostMapping("/question/{questionId}/assign")
    public String assign(@Valid @ModelAttribute("form") AssignQuestionForm form,
                         @SuppressWarnings("unused") BindingResult bindingResult,
                         ValidationHandler validationHandler,
                         @PathVariable("questionId") long questionId,
                         @PathVariable ("applicationId") long applicationId,
                         @RequestParam MultiValueMap<String, String> queryParams,
                         Model model,
                         UserResource loggedInUser) {
        String originQuery = "";
        Supplier<String> failureView = () -> doViewAssignPage(model, questionId, applicationId, originQuery);
        ProcessRoleResource assignedBy = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> assignResult = questionService.assign(questionId, applicationId, form.getAssignee(), assignedBy.getId());

            return validationHandler.addAnyErrors(assignResult)
                    .failNowOrSucceedWith(failureView, () -> redirectToRelevantPage(applicationId, questionId));
        });
    }

    private String doViewAssignPage(Model model, long questionId, long applicationId, String originQuery) {
        model.addAttribute("model", assignQuestionModelPopulator.populateModel(questionId, applicationId, originQuery));
        return "application/questions/assign-question";
    }

    private String redirectToRelevantPage(long applicationId, long questionId) {
        return format("redirect:/application/%d", applicationId);
    }

    private void populateAssigneeForm(long questionId, long applicationId, AssignQuestionForm form) {
        QuestionStatusResource questionStatus = questionService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId).get(0);
        form.setAssignee(questionStatus.getAssignee());
    }

    private ValidationMessages checkErrorsInFormAndSave(ApplicationForm form,
                                                        Long applicationId,
                                                        Long questionId,
                                                        Long userId,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        ValidationMessages errors = new ValidationMessages();
        Map<String, String[]> params = request.getParameterMap();
        if (isAllowedToUpdateQuestion(questionId, applicationId, userId) || isMarkQuestionRequest(params)) {
                /* Start save action */
            errors = applicationSaver.saveApplicationForm(
                    applicationId,
                    form,
                    questionId,
                    userId,
                    request,
                    response,
                    Optional.empty()
            );
        }
        return errors;
    }

    private boolean hasErrors(HttpServletRequest request, ValidationMessages errors, BindingResult bindingResult) {
        return isUploadWithValidationErrors(request, errors)
                || isMarkAsCompleteRequestWithValidationErrors(request.getParameterMap(), errors, bindingResult);
    }

    private String viewQuestion(
            UserResource user,
            Long applicationId,
            Long questionId,
            Model model,
            ApplicationForm form,
            Optional<Boolean> markAsComplete
    ) {
        ApplicantQuestionResource question = applicantRestService.getQuestion(user.getId(), applicationId, questionId);

        if (GRANT_AGREEMENT.equals(question.getQuestion().getQuestionSetupType())) {
            return format("redirect:/application/%d/form/question/%d/grant-agreement", applicationId, questionId);
        } else if (GRANT_TRANSFER_DETAILS.equals(question.getQuestion().getQuestionSetupType())) {
            return format("redirect:/application/%d/form/question/%d/grant-transfer-details", applicationId, questionId);
        } else if (APPLICATION_TEAM.equals(question.getQuestion().getQuestionSetupType())) {
            return format("redirect:/application/%d/form/question/%d/team", applicationId, questionId) +
                    (markAsComplete.isPresent() ? "?mark_as_complete=true" : "");
        }

        QuestionViewModel questionViewModel = questionModelPopulator.populateModel(question, form);
        boolean isSupport = user.hasRole(SUPPORT);

        applicationNavigationPopulator.addAppropriateBackURLToModel(applicationId, model, null, Optional.empty(), Optional.empty(), isSupport);

        if(question.getQuestion().getQuestionSetupType() == RESEARCH_CATEGORY) {
            ApplicationResource applicationResource = applicationService.getById(applicationId);
            model.addAttribute("researchCategoryModel", researchCategoryPopulator.populate(
                    applicationResource, user.getId(), questionId));
            model.addAttribute("form", researchCategoryFormPopulator.populate(applicationResource,
                    new ResearchCategoryForm()));
        }
        model.addAttribute(MODEL_ATTRIBUTE_MODEL, questionViewModel);

        QuestionSetupType questionType = question.getQuestion().getQuestionSetupType();
        if (questionType == null) {
            return APPLICATION_FORM;
        }
        switch (questionType) {
            case APPLICATION_DETAILS:
            case APPLICATION_TEAM:
            case RESEARCH_CATEGORY:
                return APPLICATION_FORM_LEAD;
            default:
                return APPLICATION_FORM;
        }    }

    private String handleEditQuestion(
            ApplicationForm form,
            Model model,
            Long applicationId,
            Long questionId,
            UserResource user
    ) {
        ProcessRoleResource processRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        if (processRole != null) {
            questionService.markAsIncomplete(questionId, applicationId, processRole.getId());
        } else {
            LOG.error("Not able to find process role for user {} for application id ", user.getName(), applicationId);
        }

        return viewQuestion(user, applicationId, questionId, model, form, Optional.empty());
    }

    private Boolean isMarkAsCompleteRequestWithValidationErrors(Map<String, String[]> params,
            ValidationMessages errors,
            BindingResult bindingResult) {
        return ((errors.hasErrors() || bindingResult.hasErrors()) && isMarkQuestionRequest(params));
    }

    private Boolean isUploadWithValidationErrors(HttpServletRequest request, ValidationMessages errors) {
        return (request.getParameter(UPLOAD_FILE) != null && errors.hasErrors());
    }

    private boolean isAllowedToUpdateQuestion(Long questionId, Long applicationId, Long userId) {
        List<QuestionStatusResource> questionStatuses = questionService.findQuestionStatusesByQuestionAndApplicationId(
                questionId,
                applicationId);
        return questionStatuses.isEmpty() || questionStatuses.stream()
                .anyMatch(question ->
                                  userId.equals(question.getAssignedByUserId())
                                          && !TRUE.equals(question.getMarkedAsComplete())
                );
    }
}
