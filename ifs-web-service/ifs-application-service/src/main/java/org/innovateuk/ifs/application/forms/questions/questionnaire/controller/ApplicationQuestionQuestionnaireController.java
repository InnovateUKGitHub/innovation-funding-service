package org.innovateuk.ifs.application.forms.questions.questionnaire.controller;


import org.innovateuk.ifs.application.forms.questions.questionnaire.form.ApplicationQuestionQuestionnaireForm;
import org.innovateuk.ifs.application.forms.questions.questionnaire.populator.ApplicationQuestionQuestionnaireModelPopulator;
import org.innovateuk.ifs.application.forms.questions.questionnaire.viewmodel.ApplicationQuestionQuestionnaireViewModel;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
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


    @GetMapping
    public String view(@ModelAttribute(name = "form", binding = false) ApplicationQuestionQuestionnaireForm form,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       Model model,
                       @PathVariable long applicationId,
                       @PathVariable long organisationId,
                       @PathVariable long questionId,
                       UserResource user) {
        ApplicationQuestionQuestionnaireViewModel viewModel = populator.populate(user, applicationId, questionId, organisationId);

        if (viewModel.navigateStraightToQuestionnaireWelcome()) {
            return String.format("redirect:/questionnaire/%d?redirectUrl=%s", viewModel.getQuestionnaireResponseId(), viewRedirectUrl(applicationId, organisationId, questionId));
        }
        model.addAttribute("model", viewModel);
        return "application/questions/questionnaire";
    }

    private String viewRedirectUrl(long applicationId, long organisationId, long questionId) {
        return String.format("/application/%d/form/organisation/%d/question/%d/questionnaire", applicationId, organisationId, questionId);
    }
}
