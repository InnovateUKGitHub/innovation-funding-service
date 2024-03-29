package org.innovateuk.ifs.application.forms.questions.generic.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.questions.generic.form.GenericQuestionApplicationForm;
import org.innovateuk.ifs.application.forms.questions.generic.populator.GenericQuestionApplicationFormPopulator;
import org.innovateuk.ifs.application.forms.questions.generic.populator.GenericQuestionApplicationModelPopulator;
import org.innovateuk.ifs.application.forms.questions.generic.validator.GenericQuestionApplicationFormValidator;
import org.innovateuk.ifs.application.readonly.populator.GenericQuestionReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.navigation.PageHistoryService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.negate;

/**
 * This controller handles application questions which are built up of form inputs.
 */


@Log4j2
@Controller
@RequestMapping({APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/generic",
        APPLICATION_BASE_URL + "{applicationId}/form/organisation/{organisationId}/question/{questionId}/generic"})
@SecuredBySpring(value = "Controller", description = "Only applicants can edit generic question", securedType = GenericQuestionApplicationController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class GenericQuestionApplicationController {

    static final List<QuestionSetupType> eligibleForExternalRedirection = List.of(QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION,
            QuestionSetupType.IMPACT_MANAGEMENT_SURVEY);

    private static final String APPLICATION_OVERVIEW_PAGE = "Application Overview";

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
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private GenericQuestionApplicationFormValidator validator;

    @Autowired
    private PageHistoryService pageHistoryService;

    @Value("${ifs.loan.partb.enabled}")
    private boolean isLoanPartBEnabled;

    @GetMapping
    public String view(@ModelAttribute(name = "form", binding = false) GenericQuestionApplicationForm form,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       Model model,
                       @PathVariable long applicationId,
                       @PathVariable Optional<Long> organisationId,
                       @PathVariable long questionId,
                       UserResource user, HttpServletRequest request,
                       HttpServletResponse response) {

        ApplicantQuestionResource question = applicantRestService.getQuestion(user.getId(), applicationId, questionId);
        formPopulator.populate(form, organisationId, question);
        recordApplicationOverviewPageHistory(applicationId, request, response, question);
        return getView(model, organisationId, question);
    }

    private String getView(Model model, Optional<Long> organisationId, ApplicantQuestionResource question) {
        model.addAttribute("model", modelPopulator.populate(question, organisationId));
        return "application/questions/generic";
    }

    @GetMapping(params = "show-errors")
    public String completeFromReviewPage(@ModelAttribute(value = "form") GenericQuestionApplicationForm form,
                                         BindingResult bindingResult,
                                         Model model,
                                         @PathVariable long applicationId,
                                         @PathVariable Optional<Long> organisationId,
                                         @PathVariable long questionId,
                                         UserResource user) {
        ApplicantQuestionResource question = applicantRestService.getQuestion(user.getId(), applicationId, questionId);
        formPopulator.populate(form, organisationId, question);
        validate(form, bindingResult, applicationId, questionId);
        return getView(model, organisationId, question);
    }

    @PostMapping
    public String saveAndReturn(@ModelAttribute(value = "form") GenericQuestionApplicationForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                Model model,
                                @PathVariable long applicationId,
                                @PathVariable Optional<Long> organisationId,
                                @PathVariable long questionId,
                                UserResource user) {
        save(form, applicationId, questionId, user);
        return redirectToApplicationOverview(applicationId);
    }

    @PostMapping(params = "assign")
    public String assignToLeadForReview(@ModelAttribute(value = "form") GenericQuestionApplicationForm form,
                                        BindingResult bindingResult,
                                        ValidationHandler validationHandler,
                                        Model model,
                                        @PathVariable long applicationId,
                                        @PathVariable Optional<Long> organisationId,
                                        @PathVariable long questionId,
                                        UserResource user,
                                        HttpServletResponse response) {
        cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
        questionStatusRestService.assign(questionId, applicationId, getLeadProcessRole(applicationId).getId(), getUsersProcessRole(applicationId, user).getId()).getSuccess();
        return redirectToQuestion(applicationId, organisationId, questionId);
    }

    @PostMapping(params = "complete")
    public String markAsComplete(@ModelAttribute(value = "form") GenericQuestionApplicationForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable Optional<Long> organisationId,
                                 @PathVariable long questionId,
                                 UserResource user) {
        Supplier<String> failureView = () -> {
            questionStatusRestService.markAsInComplete(questionId, applicationId, getUsersProcessRole(applicationId, user).getId()).getSuccess();
            return getView(model, organisationId, applicantRestService.getQuestion(user.getId(), applicationId, questionId));
        };

        validate(form, bindingResult, applicationId, questionId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ValidationMessages message = save(form, applicationId, questionId, user).getSuccess();
            validationHandler.addAnyErrors(message);
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                List<ValidationMessages> validationMessages = questionStatusRestService.markAsComplete(questionId, applicationId, getUsersProcessRole(applicationId, user).getId()).getSuccess();
                validationMessages.forEach(validationHandler::addAnyErrors);
                return validationHandler.failNowOrSucceedWith(failureView,
                        () -> redirectToQuestion(applicationId, organisationId, questionId));
            });
        });
    }

    @PostMapping(params = "edit")
    public String edit(@PathVariable long applicationId,
                       @PathVariable Optional<Long> organisationId,
                       @PathVariable long questionId,
                       UserResource user) {
        questionStatusRestService.markAsInComplete(questionId, applicationId, getUsersProcessRole(applicationId, user).getId()).getSuccess();
        return redirectToQuestion(applicationId, organisationId, questionId);
    }

    @PostMapping("/auto-save")
    public @ResponseBody
    JsonNode autosave(@ModelAttribute(name = "form") GenericQuestionApplicationForm form,
                      @PathVariable long applicationId,
                      @PathVariable Optional<Long> organisationId,
                      @PathVariable long questionId,
                      UserResource user) {
        save(form, applicationId, questionId, user);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping(params = "uploadTemplateDocument")
    public String uploadTemplateDocument(@ModelAttribute(name = "form") GenericQuestionApplicationForm form,
                                         @SuppressWarnings("unused") BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         Model model,
                                         @PathVariable long applicationId,
                                         @PathVariable Optional<Long> organisationId,
                                         @PathVariable long questionId,
                                         UserResource user) {
        return handleFileUpload("templateDocument", FormInputType.TEMPLATE_DOCUMENT, form.getTemplateDocument(), questionId, applicationId, organisationId, user, validationHandler, model);
    }

    @PostMapping(params = "removeTemplateDocument")
    public String removeTemplateDocument(@ModelAttribute(name = "form") GenericQuestionApplicationForm form,
                                         @SuppressWarnings("unused") BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         @RequestParam("removeTemplateDocument") long fileEntryId,
                                         Model model,
                                         @PathVariable long applicationId,
                                         @PathVariable Optional<Long> organisationId,
                                         @PathVariable long questionId,
                                         UserResource user) {
        return handleRemoveFile("templateDocument", FormInputType.TEMPLATE_DOCUMENT, questionId, applicationId, organisationId, fileEntryId, user, validationHandler, model);
    }

    @PostMapping(params = "uploadAppendix")
    public String uploadAppendix(@ModelAttribute(name = "form") GenericQuestionApplicationForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable Optional<Long> organisationId,
                                 @PathVariable long questionId,
                                 UserResource user) {
        return handleFileUpload("appendix", FormInputType.FILEUPLOAD, form.getAppendix(), questionId, applicationId, organisationId, user, validationHandler, model);
    }

    @PostMapping(params = "removeAppendix")
    public String removeAppendix(@ModelAttribute(name = "form") GenericQuestionApplicationForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 @RequestParam("removeAppendix") long fileEntryId,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable Optional<Long> organisationId,
                                 @PathVariable long questionId,
                                 UserResource user) {
        return handleRemoveFile("appendix", FormInputType.FILEUPLOAD, questionId, applicationId, organisationId, fileEntryId, user, validationHandler, model);
    }

    @GetMapping("/form-input/{formInputId}/download-template-file")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadFile(Model model,
                                                   @PathVariable long formInputId) {
        return getFileResponseEntity(formInputRestService.downloadFile(formInputId).getSuccess(),
                formInputRestService.findFile(formInputId).getSuccess());
    }

    private RestResult<ValidationMessages> save(GenericQuestionApplicationForm form, long applicationId, long questionId, UserResource user) {
        FormInputType formInputType = form.isMultipleChoiceOptionsActive() ? FormInputType.MULTIPLE_CHOICE : FormInputType.TEXTAREA;
        FormInputResource formInput = getByType(questionId, formInputType);
        return formInputResponseRestService.saveQuestionResponse(user.getId(), applicationId,
                formInput.getId(), form.getAnswer(), form.getMultipleChoiceOptionId(), false);
    }

    private String handleFileUpload(String field, FormInputType type, MultipartFile file, long questionId, long applicationId, Optional<Long> organisationId, UserResource user, ValidationHandler validationHandler, Model model) {
        Supplier<String> view = () -> getView(model, organisationId, applicantRestService.getQuestion(user.getId(), applicationId, questionId));

        return validationHandler.performFileUpload(field, view, () -> {
            FormInputResource formInput = getByType(questionId, type);
            ProcessRoleResource processRole = getUsersProcessRole(applicationId, user);
            return formInputResponseRestService.createFileEntry(formInput.getId(),
                    applicationId,
                    processRole.getId(),
                    file.getContentType(),
                    file.getSize(),
                    file.getOriginalFilename(),
                    getMultipartFileBytes(file));
        });
    }

    private String handleRemoveFile(String field, FormInputType type, long questionId, long applicationId, Optional<Long> organisationId, long fileEntryId, UserResource user, ValidationHandler validationHandler, Model model) {
        FormInputResource formInput = getByType(questionId, type);
        ProcessRoleResource processRole = getUsersProcessRole(applicationId, user);

        RestResult<Void> result = formInputResponseRestService.removeFileEntry(formInput.getId(),
                applicationId,
                processRole.getId(),
                fileEntryId);

        Supplier<String> view = () -> getView(model, organisationId, applicantRestService.getQuestion(user.getId(), applicationId, questionId));

        return validationHandler.performActionOrBindErrorsToField(field, view, view, () -> result);
    }

    private void validate(GenericQuestionApplicationForm form, BindingResult bindingResult, long applicationId, long questionId) {
        validator.validate(form, bindingResult);
        Optional<FormInputResource> templateDocument = findByType(questionId, FormInputType.TEMPLATE_DOCUMENT);
        if (templateDocument.isPresent()) {
            Optional<FormInputResponseResource> response = formInputResponseRestService
                    .getByFormInputIdAndApplication(templateDocument.get().getId(), applicationId).getOptionalSuccessObject()
                    .filter(negate(List::isEmpty))
                    .map(responses -> responses.get(0));
            boolean filePresent = response.map(resp -> !resp.getFileEntries().isEmpty()).orElse(false);
            if (!filePresent) {
                bindingResult.rejectValue("templateDocument", "validation.file.required");
            }
        }
    }

    private FormInputResource getByType(long questionId, FormInputType type) {
        return findByType(questionId, type)
                .orElseThrow(ObjectNotFoundException::new);
    }

    private Optional<FormInputResource> findByType(long questionId, FormInputType type) {
        return formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)
                .getSuccess()
                .stream()
                .filter(input -> input.getType().equals(type))
                .findAny();
    }

    private ProcessRoleResource getUsersProcessRole(long applicationId, UserResource user) {
        return processRoleRestService.findProcessRole(user.getId(), applicationId).getSuccess();
    }

    private ProcessRoleResource getLeadProcessRole(long applicationId) {
        return processRoleRestService.findProcessRole(applicationId)
                .getSuccess()
                .stream()
                .filter(pr -> pr.getRole().equals(LEADAPPLICANT))
                .findAny()
                .orElseThrow(ObjectNotFoundException::new);
    }

    private String redirectToQuestion(long applicationId, Optional<Long> organisationId, long questionId) {
        if (organisationId.isPresent()) {
            return String.format("redirect:/application/%d/form/organisation/%d/question/%d/generic", applicationId, organisationId.get(), questionId);
        }
        return String.format("redirect:/application/%d/form/question/%d/generic", applicationId, questionId);
    }

    private String redirectToApplicationOverview(long applicationId) {
        return String.format("redirect:/application/%d", applicationId);
    }

    private void recordApplicationOverviewPageHistory(long applicationId, HttpServletRequest request, HttpServletResponse response, ApplicantQuestionResource question) {
        if (Objects.nonNull(question.getQuestion()) && eligibleForExternalRedirection.contains(question.getQuestion().getQuestionSetupType())) {
            pageHistoryService.recordApplicationOverviewPageHistory(request, response, APPLICATION_OVERVIEW_PAGE, "/application/" + applicationId);
        } else {
            log.info("Ignoring, as {} is not eligible to be recorded in cookie",
                    Objects.nonNull(question.getQuestion()) ? question.getQuestion().getQuestionSetupType() : question);
        }
    }
}
