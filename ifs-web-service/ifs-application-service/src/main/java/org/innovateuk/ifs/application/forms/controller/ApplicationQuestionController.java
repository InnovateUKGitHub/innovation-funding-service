package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.forms.populator.QuestionModelPopulator;
import org.innovateuk.ifs.application.forms.saver.ApplicationQuestionSaver;
import org.innovateuk.ifs.application.forms.service.ApplicationRedirectionService;
import org.innovateuk.ifs.application.forms.viewmodel.QuestionViewModel;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.APPLICATION_DETAILS;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.APPLICATION_TEAM;

/**
 * This controller will handle all question requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ApplicationQuestionController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationQuestionController {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationQuestionController.class);

    @Autowired
    private QuestionModelPopulator questionModelPopulator;

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private ApplicantRestService applicantRestService;

    @Autowired
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @Autowired
    private ApplicationRedirectionService applicationRedirectionService;

    @Autowired
    private ApplicationQuestionSaver applicationSaver;

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
                        Optional.of(Boolean.TRUE)
                );
                validationHandler.addAnyErrors(errors);
            }
        });

        model = populateShowQuestion(user, applicationId, questionId, model, form);

        if (isQuestionType(model, APPLICATION_TEAM)) {
            model.addAttribute("applicationTeamModel", applicationTeamModelPopulator.populateModel(applicationId, user.getId()));
            return APPLICATION_FORM_LEAD;
        }

        if (isQuestionType(model, APPLICATION_DETAILS)) {
            return APPLICATION_FORM_LEAD;
        }

        return APPLICATION_FORM;
    }

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
        ValidationMessages errors = new ValidationMessages();

        // Check if the request is to just open edit view or to save
        if (params.containsKey(EDIT_QUESTION)) {
            return handleEditQuestion(form, model, applicationId, questionId, user);
        } else {
            handleAssignedQuestions(applicationId, user, request, response);

            // First check if any errors already exist in bindingResult
            checkErrorsInForm(form, applicationId, questionId, user.getId(), request, response);

            model.addAttribute("form", form);

            /* End save action */
            if (hasErrors(request, errors, bindingResult)) {
                // Add any validated fields back in invalid entries are displayed on re-render
                validationHandler.addAnyErrors(errors);
                populateShowQuestion(user, applicationId, questionId, model, form);
                // TODO 3088: return APPLICATION_FORM or APPLICATION_FORM_LEAD based on questionIype
                return APPLICATION_FORM;
            } else {
                return applicationRedirectionService.getRedirectUrl(request, applicationId, Optional.empty());
            }
        }
    }

    private void handleAssignedQuestions(Long applicationId,
                                         UserResource user,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        Map<String, String[]> params = request.getParameterMap();
        if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
            questionService.assignQuestion(applicationId, user, request);
            cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
        }
    }

    private ValidationMessages checkErrorsInForm(ApplicationForm form,
                                                 Long applicationId,
                                                 Long questionId,
                                                 Long userId,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        Map<String, String[]> params = request.getParameterMap();
        if (isAllowedToUpdateQuestion(questionId, applicationId, userId) || isMarkQuestionRequest(params)) {
                /* Start save action */
            return applicationSaver.saveApplicationForm(
                    applicationId,
                    form,
                    questionId,
                    userId,
                    request,
                    response,
                    Optional.empty()
            );
        }
        return null;
    }

    private boolean hasErrors(HttpServletRequest request, ValidationMessages errors, BindingResult bindingResult) {
        return isUploadWithValidationErrors(request, errors)
                || isMarkAsCompleteRequestWithValidationErrors(request.getParameterMap(), errors, bindingResult);
    }

    private Model populateShowQuestion(
            UserResource user,
            Long applicationId,
            Long questionId,
            Model model,
            ApplicationForm form
    ) {
        ApplicantQuestionResource question = applicantRestService.getQuestion(user.getId(), applicationId, questionId);
        QuestionViewModel questionViewModel = questionModelPopulator.populateModel(question, form);

        model.addAttribute(MODEL_ATTRIBUTE_MODEL, questionViewModel);
        applicationNavigationPopulator.addAppropriateBackURLToModel(applicationId, model, null, Optional.empty());
        return model;
    }

    private String handleEditQuestion(
            ApplicationForm form,
            Model model,
            Long applicationId,
            Long questionId,
            UserResource user
    ) {
        ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), applicationId);
        if (processRole != null) {
            questionService.markAsIncomplete(questionId, applicationId, processRole.getId());
        } else {
            LOG.error("Not able to find process role for user {} for application id ", user.getName(), applicationId);
        }

        populateShowQuestion(user, applicationId, questionId, model, form);
        return APPLICATION_FORM;
    }

    private Boolean isMarkAsCompleteRequestWithValidationErrors(Map<String, String[]> params,
            ValidationMessages errors,
            BindingResult bindingResult) {
        return ((errors.hasErrors() || bindingResult.hasErrors()) && isMarkQuestionRequest(params));
    }

    private Boolean isUploadWithValidationErrors(HttpServletRequest request, ValidationMessages errors) {
        return (request.getParameter(UPLOAD_FILE) != null && errors.hasErrors());
    }

    private Boolean isAllowedToUpdateQuestion(Long questionId, Long applicationId, Long userId) {
        List<QuestionStatusResource> questionStatuses = questionService.findQuestionStatusesByQuestionAndApplicationId(
                questionId,
                applicationId);
        return questionStatuses.isEmpty() || questionStatuses.stream()
                .anyMatch(questionStatusResource ->
                        (questionStatusResource.getAssignee() == null
                                || questionStatusResource.getAssigneeUserId().equals(userId))
                        && (questionStatusResource.getMarkedAsComplete() == null
                                || !questionStatusResource.getMarkedAsComplete())
                );
    }

    private boolean isQuestionType(Model model, CompetitionSetupQuestionType questionType) {
        QuestionViewModel questionViewModel = (QuestionViewModel) model.asMap().get(MODEL_ATTRIBUTE_MODEL);
        return questionType == questionViewModel.getApplicantResource().getQuestion().getQuestionSetupType();
    }
}
