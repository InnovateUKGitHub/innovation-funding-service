package org.innovateuk.ifs.application.forms.service;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.exception.UnableToReadUploadedFile;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.ErrorConverterFactory.toField;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.collectValidationMessages;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.noErrors;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD;
import static org.innovateuk.ifs.util.HttpUtils.requestParameterPresent;
import static org.springframework.util.StringUtils.hasText;

/**
 * This Saver will handle save all questions that are related to the application.
 */
@Service
public class ApplicationQuestionSaver {

    private static final Log LOG = LogFactory.getLog(ApplicationQuestionSaver.class);

    @Autowired
    private FinanceRowRestService financeRowRestService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    public ValidationMessages saveApplicationForm(ApplicationResource application,
                                                  ApplicationForm form,
                                                  QuestionResource question,
                                                  UserResource user,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response, BindingResult bindingResult) {

        ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), application.getId());

        // Check if action is mark as complete.  Check empty values if so, ignore otherwise. (INFUND-1222)
        Map<String, String[]> params = request.getParameterMap();

        logSaveApplicationDetails(params);

        boolean ignoreEmpty = !params.containsKey(MARK_AS_COMPLETE);

        ValidationMessages errors = new ValidationMessages();

        // Prevent saving question when it's a unmark question request (INFUND-2936)
        if (!isMarkQuestionAsIncompleteRequest(params)) {
            if (question != null) {
                errors.addAll(saveQuestionResponses(request, singletonList(question), user.getId(), processRole.getId(), application.getId(), ignoreEmpty));
            }
        }

        setApplicationDetails(application, form.getApplication());

        if (userService.isLeadApplicant(user.getId(), application)) {
            applicationService.save(application);
        }

        if (isMarkQuestionRequest(params)) {
            errors.addAll(handleApplicationDetailsMarkCompletedRequest(application, request, response, processRole, errors, bindingResult));
        }

        if (errors.hasErrors()) {
            errors.setErrors(sortValidationMessages(errors));
        }

        cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");

        return errors;
    }

    private List<Error> sortValidationMessages(ValidationMessages errors) {
        List<Error> sortedErrors = errors.getErrors().stream().filter(error ->
                error.getErrorKey().equals("application.validation.MarkAsCompleteFailed")).collect(toList());
        sortedErrors.addAll(errors.getErrors());
        return sortedErrors.parallelStream().distinct().collect(toList());
    }

    private void logSaveApplicationDetails(Map<String, String[]> params) {
        params.forEach((key, value) -> LOG.debug(String.format("saveApplicationForm key %s => value %s", key, value[0])));
    }

    private ValidationMessages handleApplicationDetailsMarkCompletedRequest(ApplicationResource application, HttpServletRequest request, HttpServletResponse response, ProcessRoleResource processRole, ValidationMessages errorsSoFar, BindingResult bindingResult) {
        ValidationMessages messages = new ValidationMessages();
        if (!errorsSoFar.hasErrors() && !bindingResult.hasErrors()) {
            List<ValidationMessages> applicationMessages = markApplicationQuestions(application, processRole.getId(), request, response, errorsSoFar);

            if (collectValidationMessages(applicationMessages).hasErrors()) {
                messages.addAll(handleApplicationDetailsValidationMessages(applicationMessages, application));
            }
        }
        return messages;
    }

    private ValidationMessages handleApplicationDetailsValidationMessages(List<ValidationMessages> applicationMessages, ApplicationResource application) {
        ValidationMessages toFieldErrors = new ValidationMessages();

        applicationMessages.forEach(validationMessage ->
                validationMessage.getErrors().stream()
                        .filter(Objects::nonNull)
                        .filter(e -> hasText(e.getErrorKey()))
                        .forEach(e -> {
                            if (validationMessage.getObjectName().equals("target")) {
                                if (hasText(e.getErrorKey())) {
                                    toFieldErrors.addError(fieldError("application." + e.getFieldName(), e.getFieldRejectedValue(), e.getErrorKey()));
                                }
                            }
                        }));

        return toFieldErrors;
    }

    private List<ValidationMessages> markApplicationQuestions(ApplicationResource application, Long processRoleId, HttpServletRequest request, HttpServletResponse response, ValidationMessages errorsSoFar) {

        if (processRoleId == null) {
            return emptyList();
        }

        Map<String, String[]> params = request.getParameterMap();

        if (params.containsKey(MARK_AS_COMPLETE)) {

            Long questionId = Long.valueOf(request.getParameter(MARK_AS_COMPLETE));

            List<ValidationMessages> markAsCompleteErrors = questionService.markAsComplete(questionId, application.getId(), processRoleId);

            if (collectValidationMessages(markAsCompleteErrors).hasErrors()) {
                questionService.markAsInComplete(questionId, application.getId(), processRoleId);
            } else {
                cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");
            }

            if (errorsSoFar.hasFieldErrors(questionId + "")) {
                markAsCompleteErrors.add(new ValidationMessages(fieldError(questionId + "", "", "mark.as.complete.invalid.data.exists")));
            }

            return markAsCompleteErrors;

        } else if (params.containsKey(MARK_AS_INCOMPLETE)) {
            Long questionId = Long.valueOf(request.getParameter(MARK_AS_INCOMPLETE));
            questionService.markAsInComplete(questionId, application.getId(), processRoleId);
        }

        return emptyList();
    }

    private ValidationMessages saveQuestionResponses(HttpServletRequest request,
                                                     List<QuestionResource> questions,
                                                     Long userId,
                                                     Long processRoleId,
                                                     Long applicationId,
                                                     boolean ignoreEmpty) {
        final Map<String, String[]> params = request.getParameterMap();

        ValidationMessages errors = new ValidationMessages();

        errors.addAll(saveNonFileUploadQuestions(questions, params, request, userId, applicationId, ignoreEmpty));

        errors.addAll(saveFileUploadQuestionsIfAny(questions, params, request, applicationId, processRoleId));

        return errors;
    }

    private ValidationMessages saveNonFileUploadQuestions(List<QuestionResource> questions,
                                                          Map<String, String[]> params,
                                                          HttpServletRequest request,
                                                          Long userId,
                                                          Long applicationId,
                                                          boolean ignoreEmpty) {

        ValidationMessages allErrors = new ValidationMessages();
        questions.forEach(question ->
                {
                    List<FormInputResource> formInputs = formInputRestService.getByQuestionIdAndScope(question.getId(), APPLICATION).getSuccessObjectOrThrowException();
                    formInputs
                            .stream()
                            .filter(formInput1 -> FILEUPLOAD != formInput1.getType())
                            .forEach(formInput -> {
                                String formInputKey = "formInput[" + formInput.getId() + "]";

                                requestParameterPresent(formInputKey, request).ifPresent(value -> {
                                    ValidationMessages errors = formInputResponseRestService.saveQuestionResponse(
                                            userId, applicationId, formInput.getId(), value, ignoreEmpty).getSuccessObjectOrThrowException();
                                    allErrors.addAll(errors, toField(formInputKey));
                                });
                            });
                }
        );
        return allErrors;
    }

    private ValidationMessages saveFileUploadQuestionsIfAny(List<QuestionResource> questions,
                                                            final Map<String, String[]> params,
                                                            HttpServletRequest request,
                                                            Long applicationId,
                                                            Long processRoleId) {
        ValidationMessages allErrors = new ValidationMessages();
        questions.forEach(question -> {
            List<FormInputResource> formInputs = formInputRestService.getByQuestionIdAndScope(question.getId(), APPLICATION).getSuccessObjectOrThrowException();
            formInputs
                    .stream()
                    .filter(formInput1 -> FILEUPLOAD == formInput1.getType() && request instanceof MultipartHttpServletRequest)
                    .forEach(formInput ->
                            allErrors.addAll(processFormInput(formInput.getId(), params, applicationId, processRoleId, request))
                    );
        });
        return allErrors;
    }

    private ValidationMessages processFormInput(Long formInputId, Map<String, String[]> params, Long applicationId, Long processRoleId, HttpServletRequest request) {
        if (params.containsKey(REMOVE_UPLOADED_FILE)) {
            formInputResponseRestService.removeFileEntry(formInputId, applicationId, processRoleId).getSuccessObjectOrThrowException();
            return noErrors();
        } else {
            final Map<String, MultipartFile> fileMap = ((MultipartHttpServletRequest) request).getFileMap();
            final MultipartFile file = fileMap.get("formInput[" + formInputId + "]");
            if (file != null && !file.isEmpty()) {
                try {
                    RestResult<FileEntryResource> result = formInputResponseRestService.createFileEntry(formInputId,
                            applicationId,
                            processRoleId,
                            file.getContentType(),
                            file.getSize(),
                            file.getOriginalFilename(),
                            file.getBytes());

                    if (result.isFailure()) {

                        ValidationMessages errors = new ValidationMessages();
                        result.getFailure().getErrors().forEach(e -> {
                            errors.addError(fieldError("formInput[" + formInputId + "]", e.getFieldRejectedValue(), e.getErrorKey()));
                        });
                        return errors;
                    }

                } catch (IOException e) {
                    LOG.error(e);
                    throw new UnableToReadUploadedFile();
                }
            }
        }

        return noErrors();
    }

    /**
     * Set the submitted values, if not null. If they are null, then probably the form field was not in the current html form.
     *
     * @param application
     * @param updatedApplication
     */
    private void setApplicationDetails(ApplicationResource application, ApplicationResource updatedApplication) {
        if (updatedApplication == null) {
            return;
        }

        if (updatedApplication.getName() != null) {
            LOG.debug("setApplicationDetails: " + updatedApplication.getName());
            application.setName(updatedApplication.getName());
        }

        setResubmissionDetails(application, updatedApplication);

        if (updatedApplication.getStartDate() != null) {
            LOG.debug("setApplicationDetails date 123: " + updatedApplication.getStartDate().toString());
            if (updatedApplication.getStartDate().isEqual(LocalDate.MIN)
                    || updatedApplication.getStartDate().isBefore(LocalDate.now(TimeZoneUtil.UK_TIME_ZONE))) {
                // user submitted a empty date field or date before today
                application.setStartDate(null);
            } else {
                application.setStartDate(updatedApplication.getStartDate());
            }
        } else {
            application.setStartDate(null);
        }

        if (updatedApplication.getDurationInMonths() != null) {
            LOG.debug("setApplicationDetails: " + updatedApplication.getDurationInMonths());
            application.setDurationInMonths(updatedApplication.getDurationInMonths());
        } else {
            application.setDurationInMonths(null);
        }
    }

    /**
     * Set the submitted details relating to resubmission of applications.
     *
     * @param application
     * @param updatedApplication
     */
    private void setResubmissionDetails(ApplicationResource application, ApplicationResource updatedApplication) {
        if (updatedApplication.getResubmission() != null) {
            LOG.debug("setApplicationDetails: resubmission " + updatedApplication.getResubmission());
            application.setResubmission(updatedApplication.getResubmission());
            if (updatedApplication.getResubmission()) {
                application.setPreviousApplicationNumber(updatedApplication.getPreviousApplicationNumber());
                application.setPreviousApplicationTitle(updatedApplication.getPreviousApplicationTitle());
            } else {
                application.setPreviousApplicationNumber(null);
                application.setPreviousApplicationTitle(null);
            }
        }
    }
}
