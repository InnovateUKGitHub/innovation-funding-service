package org.innovateuk.ifs.application.forms.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.forms.populator.QuestionModelPopulator;
import org.innovateuk.ifs.application.forms.service.ApplicationQuestionSaver;
import org.innovateuk.ifs.application.forms.service.ApplicationRedirectionService;
import org.innovateuk.ifs.application.forms.viewmodel.QuestionViewModel;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
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
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;

/**
 * This controller will handle all requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationQuestionController {

    private static final Log LOG = LogFactory.getLog(ApplicationQuestionController.class);

    @Autowired
    private QuestionModelPopulator questionModelPopulator;

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private ApplicantRestService applicantRestService;

    @Autowired
    private ApplicationRedirectionService applicationRedirectionService;

    @Autowired
    private ApplicationQuestionSaver applicationSaver;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder, WebRequest webRequest) {
        dataBinder.registerCustomEditor(String.class, new StringMultipartFileEditor());
    }

    @GetMapping(value = {QUESTION_URL + "{" + QUESTION_ID + "}", QUESTION_URL + "edit/{" + QUESTION_ID + "}"})
    public String showQuestion(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) ApplicationForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               @SuppressWarnings("unused") ValidationHandler validationHandler,
                               Model model,
                               @PathVariable(APPLICATION_ID) final Long applicationId,
                               @PathVariable(QUESTION_ID) final Long questionId,
                               UserResource user) {

        ApplicantQuestionResource question = applicantRestService.getQuestion(user.getId(), applicationId, questionId);
        QuestionViewModel questionViewModel = questionModelPopulator.populateModel(question, model, form);

        model.addAttribute(MODEL_ATTRIBUTE_MODEL, questionViewModel);
        applicationNavigationPopulator.addAppropriateBackURLToModel(applicationId, model, null);

        return APPLICATION_FORM;
    }

    @PostMapping(value = {QUESTION_URL + "{" + QUESTION_ID + "}", QUESTION_URL + "edit/{" + QUESTION_ID + "}"})
    public String questionFormSubmit(@Valid @ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     @PathVariable(APPLICATION_ID) final Long applicationId,
                                     @PathVariable(QUESTION_ID) final Long questionId,
                                     UserResource user,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {

        Map<String, String[]> params = request.getParameterMap();

        // Check if the request is to just open edit view or to save
        if (params.containsKey(EDIT_QUESTION)) {

            ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), applicationId);
            if (processRole != null) {
                questionService.markAsInComplete(questionId, applicationId, processRole.getId());
            } else {
                LOG.error("Not able to find process role for user " + user.getName() + " for application id " + applicationId);
            }

            return showQuestion(form, bindingResult, validationHandler, model, applicationId, questionId, user);

        } else {
            QuestionResource question = questionService.getById(questionId);
            ApplicationResource application = applicationService.getById(applicationId);

            if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
                questionService.assignQuestion(applicationId, user, request);
                cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
            }

            ValidationMessages errors = new ValidationMessages();

            // First check if any errors already exist in bindingResult
            if (isAllowedToUpdateQuestion(questionId, applicationId, user.getId()) || isMarkQuestionRequest(params)) {
                /* Start save action */
                errors.addAll(applicationSaver.saveApplicationForm(application, form, question, user, request, response, bindingResult));
            }

            model.addAttribute("form", form);

            /* End save action */
            if (isUploadWithValidationErrors(request, errors) || isMarkAsCompleteRequestWithValidationErrors(params, errors, bindingResult)) {
                validationHandler.addAnyErrors(errors);

                // Add any validated fields back in invalid entries are displayed on re-render
                ApplicantQuestionResource applicantQuestion = applicantRestService.getQuestion(user.getId(), applicationId, questionId);
                QuestionViewModel questionViewModel = questionModelPopulator.populateModel(applicantQuestion, model, form);

                model.addAttribute(MODEL_ATTRIBUTE_MODEL, questionViewModel);
                applicationNavigationPopulator.addAppropriateBackURLToModel(applicationId, model, null);
                return APPLICATION_FORM;
            } else {
                return applicationRedirectionService.getRedirectUrl(request, applicationId, Optional.empty());
            }
        }
    }

    private Boolean isMarkAsCompleteRequestWithValidationErrors(Map<String, String[]> params, ValidationMessages errors, BindingResult bindingResult) {
        return ((errors.hasErrors() || bindingResult.hasErrors()) && isMarkQuestionRequest(params));
    }

    private Boolean isUploadWithValidationErrors(HttpServletRequest request, ValidationMessages errors) {
        return (request.getParameter(UPLOAD_FILE) != null && errors.hasErrors());
    }

    private Boolean isAllowedToUpdateQuestion(Long questionId, Long applicationId, Long userId) {
        List<QuestionStatusResource> questionStatuses = questionService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId);
        return questionStatuses.isEmpty() || questionStatuses.stream()
                .anyMatch(questionStatusResource -> (
                        questionStatusResource.getAssignee() == null || questionStatusResource.getAssigneeUserId().equals(userId))
                        && (questionStatusResource.getMarkedAsComplete() == null || !questionStatusResource.getMarkedAsComplete()));
    }
}
