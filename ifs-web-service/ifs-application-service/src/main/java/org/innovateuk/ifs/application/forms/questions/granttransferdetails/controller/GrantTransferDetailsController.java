package org.innovateuk.ifs.application.forms.questions.granttransferdetails.controller;


import org.innovateuk.ifs.application.forms.questions.granttransferdetails.form.GrantTransferDetailsForm;
import org.innovateuk.ifs.application.forms.questions.granttransferdetails.populator.GrantTransferDetailsFormPopulator;
import org.innovateuk.ifs.application.forms.questions.granttransferdetails.populator.GrantTransferDetailsViewModelPopulator;
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
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MODEL_ATTRIBUTE_FORM;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/grant-transfer-details")
@SecuredBySpring(value = "Controller", description = "Only applicants can update grant transfer details", securedType = GrantTransferDetailsController.class)
@PreAuthorize("hasAuthority('applicant')")
public class GrantTransferDetailsController {

    @Autowired
    private GrantTransferDetailsFormPopulator grantTransferDetailsFormPopulator;

    @Autowired
    private GrantTransferDetailsViewModelPopulator grantTransferDetailsViewModelPopulator;

    @GetMapping
    public String viewGrantAgreement(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) GrantTransferDetailsForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult,
                                     Model model,
                                     @PathVariable long applicationId,
                                     @PathVariable long questionId,
                                     UserResource userResource) {
        grantTransferDetailsFormPopulator.populate(form, applicationId);
        model.addAttribute("model", grantTransferDetailsViewModelPopulator.populate(applicationId, questionId, userResource.getId()));
        return "application/questions/grant-transfer-details";
    }

}
