package org.innovateuk.ifs.application.forms.questions.generic.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.controller.ApplicationDetailsController;
import org.innovateuk.ifs.application.forms.questions.generic.form.GenericQuestionApplicationForm;
import org.innovateuk.ifs.application.forms.questions.generic.populator.GenericQuestionApplicationFormPopulator;
import org.innovateuk.ifs.application.forms.questions.generic.populator.GenericQuestionApplicationModelPopulator;
import org.innovateuk.ifs.application.readonly.populator.GenericQuestionReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;

/**
 * This controller handles application questions which are built up of form inputs.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/generic")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit generic question", securedType = ApplicationDetailsController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class GenericQuestionApplicationController {

    @Autowired
    private ApplicantRestService applicantRestService;

    @Autowired
    private GenericQuestionReadOnlyViewModelPopulator genericQuestionReadOnlyViewModelPopulator;

    @Autowired
    private GenericQuestionApplicationModelPopulator modelPopulator;

    @Autowired
    private GenericQuestionApplicationFormPopulator formPopulator;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @GetMapping
    public String view(@ModelAttribute(name = "form", binding = false) GenericQuestionApplicationForm form,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       Model model,
                       @PathVariable long applicationId,
                       @PathVariable long questionId,
                       UserResource user) {
        ApplicantQuestionResource question = applicantRestService.getQuestion(user.getId(), applicationId, questionId);
        formPopulator.populate(form, question);
        return getView(model, question);
    }

    private String getView(Model model, ApplicantQuestionResource question) {
        model.addAttribute("model", modelPopulator.populate(question));
        return "application/questions/generic";
    }

    @GetMapping(params = "mark_as_complete")
    public String completeFromReviewPage(@ModelAttribute(value = "form") GenericQuestionApplicationForm form,
                                         BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         Model model,
                                         @PathVariable long applicationId,
                                         @PathVariable long questionId,
                                         UserResource user) {
        return markAsComplete(form, bindingResult, validationHandler, model, applicationId, questionId, user);
    }

    @PostMapping(params = "assign")
    public String assignToLeadForReview(@ModelAttribute(value = "form") GenericQuestionApplicationForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable long questionId,
                                 UserResource user) {
        questionStatusRestService.assign(questionId, applicationId, getLeadProcessRole(applicationId).getId(), getUsersProcessRole(applicationId, user).getId()).getSuccess();
        return redirectToQuestion(applicationId, questionId);
    }

    @PostMapping(params = "complete")
    public String markAsComplete(@ModelAttribute(value = "form") GenericQuestionApplicationForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable long questionId,
                                 UserResource user) {
        Supplier<String> failureView = () -> {
            questionStatusRestService.markAsInComplete(questionId, applicationId, getUsersProcessRole(applicationId, user).getId()).getSuccess();
            return getView(model, applicantRestService.getQuestion(user.getId(), applicationId, questionId));
        };

        ValidationMessages message = save(form, applicationId, questionId, user).getSuccess();
        validationHandler.addAnyErrors(message);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            List<ValidationMessages> validationMessages = questionStatusRestService.markAsComplete(questionId, applicationId, getUsersProcessRole(applicationId, user).getId()).getSuccess();
            validationMessages.forEach(validationHandler::addAnyErrors);
            return validationHandler.failNowOrSucceedWith(failureView,
                    () -> redirectToQuestion(applicationId, questionId));
        });
    }

    @PostMapping(params = "edit")
    public String edit(@PathVariable long applicationId,
                       @PathVariable long questionId,
                       UserResource user) {
        questionStatusRestService.markAsInComplete(questionId, applicationId, getUsersProcessRole(applicationId, user).getId()).getSuccess();
        return redirectToQuestion(applicationId, questionId);
    }

    @PostMapping("/auto-save")
    public @ResponseBody
    JsonNode autosave(@ModelAttribute(name = "form") GenericQuestionApplicationForm form,
                      @PathVariable long applicationId,
                      @PathVariable long questionId,
                       UserResource user) {
        save(form, applicationId, questionId, user);
        return new ObjectMapper().createObjectNode();
    }

    private RestResult<ValidationMessages> save(GenericQuestionApplicationForm form, long applicationId, long questionId, UserResource user) {
        FormInputResource formInput = getByType(questionId, FormInputType.TEXTAREA);
        return formInputResponseRestService.saveQuestionResponse(user.getId(), applicationId,
                formInput.getId(), form.getAnswer(), false);

    }

    @PostMapping(params = "uploadTemplateDocument")
    public String uploadTemplateDocument(@ModelAttribute(name = "form") GenericQuestionApplicationForm form,
                                         @SuppressWarnings("unused") BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         Model model,
                                         @PathVariable long applicationId,
                                         @PathVariable long questionId,
                                         UserResource user) {
        return handleFileUpload("templateDocument", FormInputType.TEMPLATE_DOCUMENT, form.getTemplateDocument(), questionId, applicationId, user, validationHandler, model);
    }

    @PostMapping(params = "removeTemplateDocument")
    public String removeTemplateDocument(@ModelAttribute(name = "form") GenericQuestionApplicationForm form,
                                         @SuppressWarnings("unused") BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         Model model,
                                         @PathVariable long applicationId,
                                         @PathVariable long questionId,
                                         UserResource user) {
        return handleRemoveFile("templateDocument", FormInputType.TEMPLATE_DOCUMENT, questionId, applicationId, user, validationHandler, model);
    }

    @PostMapping(params = "uploadAppendix")
    public String uploadAppendix(@ModelAttribute(name = "form") GenericQuestionApplicationForm form,
                                         @SuppressWarnings("unused") BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         Model model,
                                         @PathVariable long applicationId,
                                         @PathVariable long questionId,
                                         UserResource user) {
        return handleFileUpload("appendix", FormInputType.FILEUPLOAD, form.getAppendix(), questionId, applicationId, user, validationHandler, model);
    }

    @PostMapping(params = "removeAppendix")
    public String removeAppendix(@ModelAttribute(name = "form") GenericQuestionApplicationForm form,
                                         @SuppressWarnings("unused") BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         Model model,
                                         @PathVariable long applicationId,
                                         @PathVariable long questionId,
                                         UserResource user) {
        return handleRemoveFile("appendix", FormInputType.FILEUPLOAD, questionId, applicationId, user, validationHandler, model);
    }

    private String handleFileUpload(String field, FormInputType type, MultipartFile file, long questionId, long applicationId, UserResource user, ValidationHandler validationHandler, Model model) {
        FormInputResource formInput = getByType(questionId, type);
        ProcessRoleResource processRole = getUsersProcessRole(applicationId, user);

        RestResult<FileEntryResource> result = formInputResponseRestService.createFileEntry(formInput.getId(),
                applicationId,
                processRole.getId(),
                file.getContentType(),
                file.getSize(),
                file.getOriginalFilename(),
                getMultipartFileBytes(file));

        Supplier<String> view = () -> getView(model, applicantRestService.getQuestion(user.getId(), applicationId, questionId));

        return validationHandler.performActionOrBindErrorsToField(field, view, view, () -> result);
    }

    private String handleRemoveFile(String field, FormInputType type, long questionId, long applicationId, UserResource user, ValidationHandler validationHandler, Model model) {
        FormInputResource formInput = getByType(questionId, type);
        ProcessRoleResource processRole = getUsersProcessRole(applicationId, user);

        RestResult<Void> result = formInputResponseRestService.removeFileEntry(formInput.getId(),
                applicationId,
                processRole.getId());

        Supplier<String> view = () -> getView(model, applicantRestService.getQuestion(user.getId(), applicationId, questionId));

        return validationHandler.performActionOrBindErrorsToField(field, view, view, () -> result);
    }


    @GetMapping("/form-input/{formInputId}/download-template-file")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadFile(Model model,
                                                   @PathVariable long formInputId) {
        return getFileResponseEntity(formInputRestService.downloadFile(formInputId).getSuccess(),
                formInputRestService.findFile(formInputId).getSuccess());
    }

    private FormInputResource getByType(long questionId, FormInputType type) {
        return formInputRestService.getByQuestionId(questionId)
                .getSuccess()
                .stream()
                .filter(input -> input.getType().equals(FormInputType.TEMPLATE_DOCUMENT))
                .findAny()
                .orElseThrow(ObjectNotFoundException::new);
    }

    private ProcessRoleResource getUsersProcessRole(long applicationId, UserResource user) {
        return userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
    }

    private ProcessRoleResource getLeadProcessRole(long applicationId) {
        return userRestService.findProcessRole(applicationId)
                .getSuccess()
                .stream()
                .filter(pr -> pr.getRole().equals(LEADAPPLICANT))
                .findAny()
                .orElseThrow(ObjectNotFoundException::new);
    }

    private String redirectToQuestion(long applicationId, long questionId) {
        return String.format("redirect:/application/%d/form/question/%d/generic", applicationId, questionId);
    }

}
