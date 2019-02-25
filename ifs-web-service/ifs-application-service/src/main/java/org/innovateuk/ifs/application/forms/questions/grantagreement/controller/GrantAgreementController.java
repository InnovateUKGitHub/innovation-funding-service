package org.innovateuk.ifs.application.forms.questions.grantagreement.controller;


import org.innovateuk.ifs.application.forms.questions.grantagreement.form.GrantAgreementForm;
import org.innovateuk.ifs.application.forms.questions.grantagreement.populator.GrantAgreementViewModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/grant-agreement")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = GrantAgreementController.class)
@PreAuthorize("hasAuthority('applicant')")
public class GrantAgreementController {

    @Autowired
    private GrantAgreementViewModelPopulator grantAgreementViewModelPopulator;

    @GetMapping
    public String viewGrantAgreement(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) GrantAgreementForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       Model model,
                                       @PathVariable(APPLICATION_ID) final Long applicationId,
                                       @PathVariable(QUESTION_ID) final Long questionId) {

        model.addAttribute("model", grantAgreementViewModelPopulator.populate(applicationId, questionId));
        return "application/questions/grant-agreement";

    }

}
