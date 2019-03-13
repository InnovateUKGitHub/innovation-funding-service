package org.innovateuk.ifs.application.forms.questions.granttransferdetails.controller;


import org.innovateuk.ifs.application.forms.questions.granttransferdetails.form.GrantTransferDetailsForm;
import org.innovateuk.ifs.application.forms.questions.granttransferdetails.populator.GrantTransferDetailsFormPopulator;
import org.innovateuk.ifs.application.forms.questions.granttransferdetails.populator.GrantTransferDetailsViewModelPopulator;
import org.innovateuk.ifs.application.forms.questions.granttransferdetails.saver.GrantTransferDetailsSaver;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MODEL_ATTRIBUTE_FORM;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/grant-transfer-details")
@SecuredBySpring(value = "Controller", description = "Only applicants can update grant transfer details", securedType = GrantTransferDetailsController.class)
@PreAuthorize("hasAuthority('applicant')")
public class GrantTransferDetailsController {

    private GrantTransferDetailsFormPopulator grantTransferDetailsFormPopulator;

    private GrantTransferDetailsViewModelPopulator grantTransferDetailsViewModelPopulator;

    private GrantTransferDetailsSaver grantTransferDetailsSaver;

    private UserRestService userRestService;

    private QuestionStatusRestService questionStatusRestService;

    public GrantTransferDetailsController(GrantTransferDetailsFormPopulator grantTransferDetailsFormPopulator, GrantTransferDetailsViewModelPopulator grantTransferDetailsViewModelPopulator, GrantTransferDetailsSaver grantTransferDetailsSaver, UserRestService userRestService, QuestionStatusRestService questionStatusRestService) {
        this.grantTransferDetailsFormPopulator = grantTransferDetailsFormPopulator;
        this.grantTransferDetailsViewModelPopulator = grantTransferDetailsViewModelPopulator;
        this.grantTransferDetailsSaver = grantTransferDetailsSaver;
        this.userRestService = userRestService;
        this.questionStatusRestService = questionStatusRestService;
    }

    @GetMapping
    public String viewGrantTransferDetails(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) GrantTransferDetailsForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult,
                                     Model model,
                                     @PathVariable long applicationId,
                                     @PathVariable long questionId,
                                     UserResource userResource) {
        grantTransferDetailsFormPopulator.populate(form, applicationId);
        return view(model, applicationId, questionId, userResource);
    }

    @PostMapping
    public String saveAndReturn(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) GrantTransferDetailsForm form,
                                @SuppressWarnings("unused") BindingResult bindingResult,
                                Model model,
                                @PathVariable long applicationId,
                                @PathVariable long questionId,
                                UserResource userResource) {
        grantTransferDetailsSaver.save(form, applicationId);
        return String.format("redirect:/application/%d", applicationId);
    }

    @PostMapping(params = "complete")
    public String markAsComplete(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) @Valid GrantTransferDetailsForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable long questionId,
                                 UserResource user) {
        Supplier<String> failureView = () -> view(model, applicationId, questionId, user);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(grantTransferDetailsSaver.save(form, applicationId).getErrors());
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ProcessRoleResource role = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
                questionStatusRestService.markAsComplete(questionId, applicationId, role.getId()).getSuccess();
                return String.format("redirect:/application/%d", applicationId);
            });
        });
    }

    @PostMapping(params = "edit")
    public String edit(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) GrantTransferDetailsForm form,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       Model model,
                       @PathVariable long applicationId,
                       @PathVariable long questionId,
                       UserResource user) {
        ProcessRoleResource role = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.markAsInComplete(questionId, applicationId, role.getId()).getSuccess();
        return viewGrantTransferDetails(form, bindingResult, model, applicationId, questionId, user);
    }

    private String view(Model model, long applicationId, long questionId, UserResource userResource) {
        model.addAttribute("model", grantTransferDetailsViewModelPopulator.populate(applicationId, questionId, userResource.getId()));
        return "application/questions/grant-transfer-details";
    }
}
