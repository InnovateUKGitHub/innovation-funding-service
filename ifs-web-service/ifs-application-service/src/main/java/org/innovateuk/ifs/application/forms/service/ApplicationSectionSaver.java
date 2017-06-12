package org.innovateuk.ifs.application.forms.service;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.overheads.OverheadFileSaver;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.exception.UnableToReadUploadedFile;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.ErrorConverterFactory.toField;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.collectValidationMessages;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.noErrors;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.HttpUtils.requestParameterPresent;
import static org.springframework.util.StringUtils.hasText;

/**
 * TODO: comments
 */
@Service
public class ApplicationSectionSaver {

    private static final Log LOG = LogFactory.getLog(ApplicationSectionSaver.class);

    @Autowired
    private FinanceRowRestService financeRowRestService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private FinanceHandler financeHandler;

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

    @Autowired
    private OverheadFileSaver overheadFileSaver;

    public ValidationMessages saveApplicationForm(ApplicationResource application,
                                                  CompetitionResource competition,
                                                  ApplicationForm form,
                                                  Long sectionId,
                                                  UserResource user,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response, Boolean validFinanceTerms) {

        ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), application.getId());

        SectionResource selectedSection = null;
        if (sectionId != null) {
            selectedSection = sectionService.getById(sectionId);
        }

        // Check if action is mark as complete.  Check empty values if so, ignore otherwise. (INFUND-1222)
        Map<String, String[]> params = request.getParameterMap();
        logSaveApplicationDetails(params);

        boolean ignoreEmpty = (!params.containsKey(MARK_AS_COMPLETE)) && (!params.containsKey(MARK_SECTION_AS_COMPLETE));

        ValidationMessages errors = new ValidationMessages();

        if (isMarkSectionAsCompleteRequest(params)) {
            application.setStateAidAgreed(form.isStateAidAgreed());
        } else if (isMarkSectionAsIncompleteRequest(params) && selectedSection.getType() == SectionType.FINANCE) {
            application.setStateAidAgreed(Boolean.FALSE);
        }

        // Prevent saving question when it's a unmark question request (INFUND-2936)
        if (!isMarkQuestionAsInCompleteRequest(params)) {
            List<QuestionResource> questions = simpleMap(selectedSection.getQuestions(), questionService::getById);
            errors.addAll(saveQuestionResponses(request, questions, user.getId(), processRole.getId(), application.getId(), ignoreEmpty));
        }

        if (isNotRequestingFundingRequest(params)) {
            setRequestingFunding(NOT_REQUESTING_FUNDING, user.getId(), application.getId(), competition.getId(), processRole.getId(), errors);
        } else if (isRequestingFundingRequest(params)) {
            setRequestingFunding(REQUESTING_FUNDING, user.getId(), application.getId(), competition.getId(), processRole.getId(), errors);
        }

        if (userService.isLeadApplicant(user.getId(), application)) {
            applicationService.save(application);
        }

        errors.addAll(overheadFileSaver.handleOverheadFileRequest(request));

        if (!isMarkSectionAsIncompleteRequest(params)) {
            Long organisationType = organisationService.getOrganisationType(user.getId(), application.getId());
            ValidationMessages saveErrors = financeHandler.getFinanceFormHandler(organisationType).update(request, user.getId(), application.getId(), competition.getId());

            if (!overheadFileSaver.isOverheadFileRequest(request)) {
                errors.addAll(saveErrors);
            }

            markAcademicFinancesAsNotRequired(organisationType, selectedSection, application.getId(), competition.getId(), processRole.getId());
        }

        if (isMarkSectionRequest(params)) {
            errors.addAll(handleMarkSectionRequest(application, sectionId, request, processRole, errors, validFinanceTerms));
        }

        if (errors.hasErrors()) {
            errors.setErrors(sortValidationMessages(errors));
        }

        cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");

        return errors;
    }

    private void setRequestingFunding(String requestingFunding, Long userId, Long applicationId, Long competitionId, Long processRoleId, ValidationMessages errors) {
        ApplicationFinanceResource finance = financeService.getApplicationFinanceDetails(userId, applicationId);
        QuestionResource financeQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.FINANCE).getSuccessObjectOrThrowException();
        if (finance.getGrantClaim() != null) {
            finance.getGrantClaim().setGrantClaimPercentage(0);
        }
        errors.addAll(financeRowRestService.add(finance.getId(), financeQuestion.getId(), finance.getGrantClaim()));

        if (!errors.hasErrors()) {
            SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES).get(0);
            SectionResource fundingSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).get(0);
            if (REQUESTING_FUNDING.equals(requestingFunding)) {
                sectionService.markAsInComplete(organisationSection.getId(), applicationId, processRoleId);
                sectionService.markAsInComplete(fundingSection.getId(), applicationId, processRoleId);
            } else if (NOT_REQUESTING_FUNDING.equals(requestingFunding)) {
                sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
                sectionService.markAsNotRequired(fundingSection.getId(), applicationId, processRoleId);
            }
        }
    }

    private void markAcademicFinancesAsNotRequired(long organisationType, SectionResource selectedSection, long applicationId, long competitionId, long processRoleId) {
        if (selectedSection != null && SectionType.PROJECT_COST_FINANCES.equals(selectedSection.getType())
                && OrganisationTypeEnum.RESEARCH.getId().equals(organisationType)) {
            SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES).get(0);
            SectionResource fundingSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).get(0);
            sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
            sectionService.markAsNotRequired(fundingSection.getId(), applicationId, processRoleId);
        }
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

    private ValidationMessages handleMarkSectionRequest(ApplicationResource application, Long sectionId, HttpServletRequest request,
                                                        ProcessRoleResource processRole, ValidationMessages errorsSoFar, Boolean validFinanceTerms) {
        ValidationMessages messages = new ValidationMessages();

        if (errorsSoFar.hasErrors()) {
            messages.addError(fieldError("formInput[cost]", "", "application.validation.MarkAsCompleteFailed"));
        } else if (isMarkSectionAsIncompleteRequest(request.getParameterMap()) ||
                (isMarkSectionAsCompleteRequest(request.getParameterMap()) && validFinanceTerms)) {
            SectionResource selectedSection = sectionService.getById(sectionId);
            List<ValidationMessages> financeErrorsMark = markAllQuestionsInSection(application, selectedSection, processRole.getId(), request);

            if (collectValidationMessages(financeErrorsMark).hasErrors()) {
                messages.addError(fieldError("formInput[cost]", "", "application.validation.MarkAsCompleteFailed"));
                messages.addAll(handleMarkSectionValidationMessages(financeErrorsMark));
            }
        }

        return messages;
    }

    private ValidationMessages handleMarkSectionValidationMessages(List<ValidationMessages> financeErrorsMark) {

        ValidationMessages toFieldErrors = new ValidationMessages();

        financeErrorsMark.forEach(validationMessage ->
                validationMessage.getErrors().stream()
                        .filter(Objects::nonNull)
                        .filter(e -> hasText(e.getErrorKey()))
                        .forEach(e -> {
                            if (validationMessage.getObjectName().equals("costItem")) {
                                if (hasText(e.getErrorKey())) {
                                    toFieldErrors.addError(fieldError("formInput[cost-" + validationMessage.getObjectId() + "-" + e.getFieldName() + "]", e));
                                } else {
                                    toFieldErrors.addError(fieldError("formInput[cost-" + validationMessage.getObjectId() + "]", e));
                                }
                            } else {
                                toFieldErrors.addError(fieldError("formInput[" + validationMessage.getObjectId() + "]", e));
                            }
                        })
        );

        return toFieldErrors;
    }

    private List<ValidationMessages> markAllQuestionsInSection(ApplicationResource application,
                                                               SectionResource selectedSection,
                                                               Long processRoleId,
                                                               HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();

        String action = params.containsKey(MARK_SECTION_AS_COMPLETE) ? MARK_AS_COMPLETE : MARK_AS_INCOMPLETE;

        if (action.equals(MARK_AS_COMPLETE)) {
            return sectionService.markAsComplete(selectedSection.getId(), application.getId(), processRoleId);
        } else {
            sectionService.markAsInComplete(selectedSection.getId(), application.getId(), processRoleId);
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

        errors.addAll(saveNonFileUploadQuestions(questions, request, userId, applicationId, ignoreEmpty));

        errors.addAll(saveFileUploadQuestionsIfAny(questions, params, request, applicationId, processRoleId));

        return errors;
    }

    private ValidationMessages saveNonFileUploadQuestions(List<QuestionResource> questions,
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
                        result.getFailure().getErrors().forEach(e ->
                            errors.addError(fieldError("formInput[" + formInputId + "]", e.getFieldRejectedValue(), e.getErrorKey()))
                        );
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
}
