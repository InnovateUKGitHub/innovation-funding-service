package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.questions.researchcategory.form.ResearchCategoryForm;
import org.innovateuk.ifs.application.forms.questions.researchcategory.populator.ApplicationResearchCategoryFormPopulator;
import org.innovateuk.ifs.application.forms.questions.researchcategory.populator.ApplicationResearchCategoryModelPopulator;
import org.innovateuk.ifs.application.forms.saver.ApplicationQuestionSaver;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.UserResource;
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
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.application.ApplicationUrlHelper.getQuestionUrl;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * This controller will handle all question requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ApplicationQuestionController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'project_finance', 'ifs_administrator', 'comp_admin', 'support', 'innovation_lead', 'stakeholder', 'assessor', 'monitoring_officer')")
public class ApplicationQuestionController {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationQuestionController.class);

    @Autowired
    private ApplicationResearchCategoryModelPopulator researchCategoryPopulator;
    @Autowired
    private ApplicationResearchCategoryFormPopulator researchCategoryFormPopulator;
    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private QuestionRestService questionRestService;
    @Autowired
    private ApplicantRestService applicantRestService;
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
                        Optional.of(TRUE)
                );
                validationHandler.addAnyErrors(errors);
            }
        });

        return viewQuestion(user, applicationId, questionId, model, form, markAsComplete);
    }

    private String viewQuestion(
            UserResource user,
            Long applicationId,
            Long questionId,
            Model model,
            ApplicationForm form,
            Optional<Boolean> markAsComplete) {

        QuestionResource questionResource = questionRestService.findById(questionId).getSuccess();
        QuestionSetupType questionType = questionResource.getQuestionSetupType();
        Optional<String> questionUrl = getQuestionUrl(questionType, questionId, applicationId);
        if (questionUrl.isPresent()) {
            return "redirect:" + questionUrl.get() + (markAsComplete.isPresent() ? "?show-errors=true" : "");
        }
        if (questionType == RESEARCH_CATEGORY) {
            ApplicationResource applicationResource = applicationService.getById(applicationId);
            model.addAttribute("researchCategoryModel", researchCategoryPopulator.populate(
                    applicationResource, user.getId(), questionId));
            model.addAttribute("form", researchCategoryFormPopulator.populate(applicationResource,
                    new ResearchCategoryForm()));
        }

        throw new IFSRuntimeException("Unknown question type" + questionType.name());
    }

}
