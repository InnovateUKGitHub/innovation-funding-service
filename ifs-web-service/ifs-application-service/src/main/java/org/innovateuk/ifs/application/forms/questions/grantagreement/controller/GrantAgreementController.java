package org.innovateuk.ifs.application.forms.questions.grantagreement.controller;


import org.innovateuk.ifs.application.forms.questions.grantagreement.form.GrantAgreementForm;
import org.innovateuk.ifs.application.forms.questions.grantagreement.populator.GrantAgreementViewModelPopulator;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fileUploadField;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/grant-agreement")
@SecuredBySpring(value = "Controller", description = "Only applicants can upload and remove grant agreements", securedType = GrantAgreementController.class)
@PreAuthorize("hasAuthority('applicant')")
public class GrantAgreementController {

    private GrantAgreementViewModelPopulator grantAgreementViewModelPopulator;

    private EuGrantTransferRestService euGrantTransferRestService;

    private QuestionStatusRestService questionStatusRestService;

    private UserRestService userRestService;

    public GrantAgreementController(GrantAgreementViewModelPopulator grantAgreementViewModelPopulator, EuGrantTransferRestService euGrantTransferRestService, QuestionStatusRestService questionStatusRestService, UserRestService userRestService) {
        this.grantAgreementViewModelPopulator = grantAgreementViewModelPopulator;
        this.euGrantTransferRestService = euGrantTransferRestService;
        this.questionStatusRestService = questionStatusRestService;
        this.userRestService = userRestService;
    }

    @GetMapping
    public String viewGrantAgreement(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) GrantAgreementForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult,
                                     Model model,
                                     @PathVariable(APPLICATION_ID) final Long applicationId,
                                     @PathVariable(QUESTION_ID) final Long questionId,
                                     UserResource userResource) {
        model.addAttribute("model", grantAgreementViewModelPopulator.populate(applicationId, questionId, userResource.getId()));
        return "application/questions/grant-agreement";
    }

    @PostMapping
    public String saveAndReturn(@PathVariable(APPLICATION_ID) final Long applicationId) {
        return String.format("redirect:/application/%d", applicationId);
    }

    @PostMapping(params = "complete")
    public String markAsComplete(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) GrantAgreementForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 Model model,
                                 @PathVariable(APPLICATION_ID) final Long applicationId,
                                 @PathVariable(QUESTION_ID) final Long questionId,
                                 UserResource user) {
        if (euGrantTransferRestService.findGrantAgreement(applicationId).isFailure()) {
            bindingResult.rejectValue("grantAgreement", "validation.field.must.not.be.blank");
            return viewGrantAgreement(form, bindingResult, model, applicationId, questionId, user);
        }
        ProcessRoleResource role = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.markAsComplete(questionId, applicationId, role.getId()).getSuccess();
        return saveAndReturn(applicationId);
    }

    @PostMapping(params = "edit")
    public String edit(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) GrantAgreementForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 Model model,
                                 @PathVariable(APPLICATION_ID) final Long applicationId,
                                 @PathVariable(QUESTION_ID) final Long questionId,
                                 UserResource user) {
        ProcessRoleResource role = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.markAsInComplete(questionId, applicationId, role.getId()).getSuccess();
        return viewGrantAgreement(form, bindingResult, model, applicationId, questionId, user);
    }

    @PostMapping(params = "uploadGrantAgreement")
    public String uploadGrantAgreement(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) GrantAgreementForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       Model model,
                                       @PathVariable(APPLICATION_ID) final Long applicationId,
                                       @PathVariable(QUESTION_ID) final Long questionId,
                                       UserResource user) {

        MultipartFile file = form.getGrantAgreement();
        RestResult<Void> sendResult = euGrantTransferRestService
                .uploadGrantAgreement(applicationId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));

        Supplier<String> failureAndSuccesView = () -> viewGrantAgreement(form, bindingResult, model, applicationId, questionId, user);

        return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())), fileUploadField("grantAgreement"), defaultConverters())
                .failNowOrSucceedWith(failureAndSuccesView, failureAndSuccesView);
    }

    @PostMapping(params = "removeGrantAgreement")
    public String removeGrantAgreement(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) GrantAgreementForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       Model model,
                                       @PathVariable(APPLICATION_ID) final Long applicationId,
                                       @PathVariable(QUESTION_ID) final Long questionId,
                                       UserResource user) {

        RestResult<Void> sendResult = euGrantTransferRestService
                .deleteGrantAgreement(applicationId);

        Supplier<String> failureAndSuccesView = () -> viewGrantAgreement(form, bindingResult, model, applicationId, questionId, user);

        return validationHandler.addAnyErrors(sendResult.getErrors())
                .failNowOrSucceedWith(failureAndSuccesView, failureAndSuccesView);
    }

    @GetMapping("/download")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadGrantAgreement(@PathVariable(APPLICATION_ID) final Long applicationId) {
        return getFileResponseEntity(euGrantTransferRestService.downloadGrantAgreement(applicationId).getSuccess(),
                euGrantTransferRestService.findGrantAgreement(applicationId).getSuccess());
    }

}
