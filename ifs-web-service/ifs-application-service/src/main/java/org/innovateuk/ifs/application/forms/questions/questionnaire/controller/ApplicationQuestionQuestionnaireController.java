package org.innovateuk.ifs.application.forms.questions.questionnaire.controller;


import org.innovateuk.ifs.application.forms.questions.questionnaire.form.ApplicationQuestionQuestionnaireForm;
import org.innovateuk.ifs.application.forms.questions.questionnaire.populator.ApplicationQuestionQuestionnaireModelPopulator;
import org.innovateuk.ifs.application.forms.questions.questionnaire.viewmodel.ApplicationQuestionQuestionnaireViewModel;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.questionnaire.link.service.QuestionnaireResponseLinkRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/organisation/{organisationId}/question/{questionId}/questionnaire")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit a question", securedType = ApplicationQuestionQuestionnaireController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class ApplicationQuestionQuestionnaireController {

    @Autowired
    private ApplicationQuestionQuestionnaireModelPopulator populator;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private QuestionnaireResponseLinkRestService questionnaireResponseLinkRestService;

    @GetMapping
    public String view(@ModelAttribute(name = "form", binding = false) ApplicationQuestionQuestionnaireForm form,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       Model model,
                       @PathVariable long applicationId,
                       @PathVariable long organisationId,
                       @PathVariable long questionId,
                       UserResource user) {
        QuestionResource question = questionRestService.findById(questionId).getSuccess();
        ApplicationQuestionQuestionnaireViewModel viewModel = populator.populate(user, applicationId, question, organisationId);

        if (!viewModel.isReadOnly()) {
            Long questionnaireResponseId = questionnaireResponseLinkRestService.getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(question.getQuestionnaireId(), applicationId, organisationId).getSuccess();
            return String.format("redirect:/questionnaire/%d", questionnaireResponseId);
        }
        return ""; //TODO readonly view.
    }
}
