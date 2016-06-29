package com.worth.ifs.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.form.validation.ApplicationStartDateValidator;
import com.worth.ifs.application.model.OpenFinanceSectionSectionModelPopulator;
import com.worth.ifs.application.model.OpenSectionModelPopulator;
import com.worth.ifs.application.model.QuestionModelPopulator;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.exception.AutosaveElementException;
import com.worth.ifs.exception.BigDecimalNumberFormatException;
import com.worth.ifs.exception.IntegerNumberFormatException;
import com.worth.ifs.exception.UnableToReadUploadedFile;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.model.OrganisationDetailsModelPopulator;
import com.worth.ifs.profiling.ProfileExecution;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.util.AjaxResult;
import com.worth.ifs.util.MessageUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

import static com.worth.ifs.application.resource.SectionType.FINANCE;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.rest.ValidationMessages.noErrors;
import static com.worth.ifs.controller.RestFailuresToValidationErrorBindingUtils.bindAnyErrorsToBindingResult;
import static com.worth.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * This controller will handle all requests that are related to the application form.
 */
@Controller
@RequestMapping(ApplicationFormController.APPLICATION_BASE_URL+"{applicationId}/form")
public class ApplicationFormController extends AbstractApplicationController {


    private static final Log LOG = LogFactory.getLog(ApplicationFormController.class);

    @Autowired
    private CostService costService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private QuestionModelPopulator questionModelPopulator;

    @Autowired
    private OpenSectionModelPopulator openSectionModel;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Autowired
    private OpenFinanceSectionSectionModelPopulator openFinanceSectionModel;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder, WebRequest webRequest) {
        dataBinder.registerCustomEditor(String.class, new StringMultipartFileEditor());
    }

    @ProfileExecution
    @RequestMapping(value = {QUESTION_URL + "{"+QUESTION_ID+"}", QUESTION_URL + "edit/{"+QUESTION_ID+"}"}, method = RequestMethod.GET)
    public String showQuestion(@ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
                               BindingResult bindingResult, Model model,
                               @PathVariable(APPLICATION_ID) final Long applicationId,
                               @PathVariable(QUESTION_ID) final Long questionId,
                               HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        questionModelPopulator.populateModel(questionId, applicationId, user, model, form, bindingResult);
        organisationDetailsModelPopulator.populateModel(model, applicationId);
        return APPLICATION_FORM;
    }

    @ProfileExecution
    @RequestMapping(value = QUESTION_URL + "{"+QUESTION_ID+"}/forminput/{formInputId}/download", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadApplicationFinanceFile(
                                @PathVariable(APPLICATION_ID) final Long applicationId,
                                @PathVariable(QUESTION_ID) final Long questionId,
                                @PathVariable("formInputId") final Long formInputId,
                                HttpServletRequest request) {
        final UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), applicationId);
        final ByteArrayResource resource = formInputResponseService.getFile(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        final FormInputResponseFileEntryResource fileDetails = formInputResponseService.getFileDetails(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileDetails.getFileEntryResource());
    }

    @RequestMapping(value = "/{applicationFinanceId}/finance-download", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadApplicationFinanceFile(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId) {

        final ByteArrayResource resource = financeService.getFinanceDocumentByApplicationFinance(applicationFinanceId).getSuccessObjectOrThrowException();
        final FileEntryResource fileDetails = financeService.getFinanceEntryByApplicationFinanceId(applicationFinanceId).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileDetails);
    }

    @ProfileExecution
    @RequestMapping(value = SECTION_URL + "{sectionId}", method = RequestMethod.GET)
    public String applicationFormWithOpenSection(@Valid @ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form, BindingResult bindingResult, Model model,
                                                 @PathVariable(APPLICATION_ID) final Long applicationId,
                                                 @PathVariable("sectionId") final Long sectionId,
                                                 HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());
        SectionResource section = simpleFilter(allSections, s -> sectionId.equals(s.getId())).get(0);

        if (FINANCE.equals(section.getType())) {
            openFinanceSectionModel.populateModel(form, model, application, section, user, bindingResult, allSections);
        } else {
            openSectionModel.populateModel(form, model, application, section, user, bindingResult, allSections);
        }

        return APPLICATION_FORM;
    }

    private void addFormAttributes(ApplicationResource application,
                                   CompetitionResource competition,
                                   Optional<SectionResource> section,
                                   UserResource user, Model model,
                                   ApplicationForm form, Optional<QuestionResource> question,
                                   Optional<List<FormInputResource>> formInputs,
                                   List<ProcessRoleResource> userApplicationRoles){
        addApplicationDetails(application, competition, user.getId(), section, question.map(q -> q.getId()), model, form, userApplicationRoles);
        organisationDetailsModelPopulator.populateModel(model, application.getId(), userApplicationRoles);
        addNavigation(question.orElse(null), application.getId(), model);
        Map<Long, List<FormInputResource>> questionFormInputs = new HashMap<>();

        if(question.isPresent()) {
            questionFormInputs.put(question.get().getId(), formInputs.orElse(null));
        }
        model.addAttribute("currentQuestion", question.orElse(null));
        model.addAttribute("questionFormInputs", questionFormInputs);
        model.addAttribute("currentUser", user);
        model.addAttribute("form", form);
        if(question.isPresent()) {
            model.addAttribute("title", question.get().getShortName());
        }
    }

    @ProfileExecution
    @RequestMapping(value = {QUESTION_URL + "{"+QUESTION_ID+"}", QUESTION_URL + "edit/{"+QUESTION_ID+"}"}, method = RequestMethod.POST)
    public String questionFormSubmit(@Valid @ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
                                     BindingResult bindingResult,
                                     Model model,
                                     @PathVariable(APPLICATION_ID) final Long applicationId,
                                     @PathVariable(QUESTION_ID) final Long questionId,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);

        Map<String, String[]> params = request.getParameterMap();

        // Check if the request is to just open edit view or to save
        if(params.containsKey(EDIT_QUESTION)){
            ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), applicationId);
            if (processRole != null) {
                questionService.markAsInComplete(questionId, applicationId, processRole.getId());
            } else {
                LOG.error("Not able to find process role for user " + user.getName() + " for application id " + applicationId);
            }
            return showQuestion(form, bindingResult, model, applicationId, questionId, request);
        } else {
            QuestionResource question = questionService.getById(questionId);
            SectionResource section = sectionService.getSectionByQuestionId(questionId);
            ApplicationResource application = applicationService.getById(applicationId);
            CompetitionResource competition = competitionService.getById(application.getCompetition());
            List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
            List<FormInputResource> formInputs = formInputService.findByQuestion(questionId);

            if (isAllowedToUpdateQuestion(questionId, applicationId, user.getId()) || isMarkQuestionRequest(params)) {
                /* Start save action */
                saveApplicationForm(application, competition, form, applicationId, null, question, request, response, bindingResult);
            }

            if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
                assignQuestion(applicationId, request);
                cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
            }

            bindingResult = removeDuplicateFieldErrors(bindingResult);
            form.setBindingResult(bindingResult);
            form.setObjectErrors(bindingResult.getAllErrors());
            model.addAttribute("form", form);
            /* End save action */

            if (bindingResult.hasErrors()) {
                this.addFormAttributes(application, competition, Optional.ofNullable(section), user, model, form,
                        Optional.ofNullable(question), Optional.ofNullable(formInputs), userApplicationRoles);
                model.addAttribute("currentUser", user);
                addUserDetails(model, application, user.getId());
                addNavigation(question, applicationId, model);
                return APPLICATION_FORM;
            } else {
                return getRedirectUrl(request, applicationId);
            }
        }
    }

    private Boolean isAllowedToUpdateQuestion(Long questionId, Long applicationId, Long userId) {
        List<QuestionStatusResource> questionStatuses = questionService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId);
        return questionStatuses.isEmpty() || questionStatuses.stream()
                .anyMatch(questionStatusResource -> (
                        questionStatusResource.getAssignee() == null || questionStatusResource.getAssigneeUserId().equals(userId))
                        && (questionStatusResource.getMarkedAsComplete() == null || !questionStatusResource.getMarkedAsComplete()));
    }

    private BindingResult removeDuplicateFieldErrors(BindingResult bindingResult) {
        BindingResult br = new BeanPropertyBindingResult(this, "form");
        bindingResult.getFieldErrors().stream().distinct()
                .filter(e -> {
                    for (FieldError fieldError : br.getFieldErrors(e.getField())) {
                        if(fieldError.getDefaultMessage().equals(e.getDefaultMessage())){
                            return false;
                        }else{
                            return true;
                        }
                    }
                    return true;
                })
                .forEach(e -> br.addError(e));
        bindingResult.getGlobalErrors().stream().forEach(e -> br.addError(e));
        bindingResult = br;
        return bindingResult;
    }

    private String getRedirectUrl(HttpServletRequest request, Long applicationId) {
        if (request.getParameter(ASSIGN_QUESTION_PARAM) != null ||
                request.getParameter(MARK_AS_INCOMPLETE) != null ||
                request.getParameter(MARK_SECTION_AS_INCOMPLETE) != null ||
                request.getParameter(ADD_COST) != null ||
                request.getParameter(REMOVE_COST) != null ||
                request.getParameter(MARK_AS_COMPLETE) != null ||
                request.getParameter(REMOVE_UPLOADED_FILE) != null ||
                request.getParameter(UPLOAD_FILE) != null ||
                request.getParameter(EDIT_QUESTION) != null) {
            // user did a action, just display the same page.
            LOG.debug("redirect: " + request.getRequestURI());
            return "redirect:" + request.getRequestURI();
        } else {
            // add redirect, to make sure the user cannot resubmit the form by refreshing the page.
            LOG.debug("default redirect: ");
            return "redirect:"+APPLICATION_BASE_URL + applicationId;
        }
    }

    @RequestMapping(value = "/add_cost/{"+QUESTION_ID+"}")
    public String addCostRow(@ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
                             BindingResult bindingResult,
                             Model model,
                             @PathVariable(APPLICATION_ID) final Long applicationId,
                             @PathVariable(QUESTION_ID) final Long questionId,
                             HttpServletRequest request) {
        CostItem costItem = addCost(applicationId, questionId, request);
        String type = costItem.getCostType().getType();
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);

        Set<Long> markedAsComplete = new TreeSet<>();
        model.addAttribute("markedAsComplete", markedAsComplete);
        String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
        financeHandler.getFinanceModelManager(organisationType).addCost(model, costItem, applicationId, user.getId(), questionId, type);

        form.setBindingResult(bindingResult);
        return String.format("finance/finance :: %s_row", type);
    }

    @RequestMapping(value = "/remove_cost/{costId}")
    public @ResponseBody String removeCostRow(@PathVariable("costId") final Long costId) throws JsonProcessingException {
        costService.delete(costId);
        AjaxResult ajaxResult = new AjaxResult(HttpStatus.OK, "true");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ajaxResult);
    }

    private CostItem addCost(Long applicationId, Long questionId, HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
        return financeHandler.getFinanceFormHandler(organisationType).addCost(applicationId, user.getId(), questionId);
    }

    private void saveApplicationForm(ApplicationResource application,
                                              CompetitionResource competition,
                                              ApplicationForm form,
                                              Long applicationId, Long sectionId, QuestionResource question,
                                              HttpServletRequest request,
                                              HttpServletResponse response,
                                              BindingResult bindingResult) {

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), applicationId);

        // Check if action is mark as complete.  Check empty values if so, ignore otherwise. (INFUND-1222)
        Map<String, String[]> params = request.getParameterMap();
        params.forEach((key, value) -> LOG.debug(String.format("saveApplicationForm key %s   => value %s", key, value[0])));
        boolean ignoreEmpty = (!params.containsKey(MARK_AS_COMPLETE)) && (!params.containsKey(MARK_SECTION_AS_COMPLETE));

        ValidationMessages errors = new ValidationMessages();

        // Prevent saving question when it's a unmark question request (INFUND-2936)
        if(!isMarkQuestionAsInCompleteRequest(params)) {
            if (question != null) {
                errors.addAll(saveQuestionResponses(application, singletonList(question), user.getId(), processRole.getId(), request, ignoreEmpty));
            } else {
                SectionResource selectedSection = getSelectedSection(competition.getSections(), sectionId);
                List<QuestionResource> questions = simpleMap(selectedSection.getQuestions(), questionService::getById);
                errors.addAll(saveQuestionResponses(application, questions, user.getId(), processRole.getId(), request, ignoreEmpty));
            }
        }

        errors.addAll(validateStartDate(request, form));
        setApplicationDetails(application, form.getApplication());

        if(userIsLeadApplicant(application, user.getId())) {
            applicationService.save(application);
        }

        if(!isMarkSectionAsIncompleteRequest(params)) {
            String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
            errors.addAll(financeHandler.getFinanceFormHandler(organisationType).update(request, user.getId(), applicationId));
        }

        if(isMarkQuestionRequest(params)) {
            errors.addAll(markApplicationQuestions(application, processRole.getId(), request, response, errors));
        } else if(isMarkSectionRequest(params)){
            errors.addAll(handleMarkSectionRequest(application, competition, sectionId, request, errors.hasErrors(), processRole));
        }

        bindAnyErrorsToBindingResult(errors, bindingResult);

        cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");
    }

    private ValidationMessages validateStartDate(HttpServletRequest request, ApplicationForm form) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
        new ApplicationStartDateValidator().validate(request, bindingResult);
        List<Error> fieldErrors = simpleMap(bindingResult.getFieldErrors(), error -> fieldError(error.getField(), error.getDefaultMessage(), error.getArguments()));
        return new ValidationMessages(fieldErrors);
    }

    private ValidationMessages handleMarkSectionRequest(ApplicationResource application, CompetitionResource competition, Long sectionId, HttpServletRequest request, boolean errorsExist, ProcessRoleResource processRole) {

        if (errorsExist) {
            return new ValidationMessages(fieldError("formInput[cost]", "application.validation.MarkAsCompleteFailed"));
        } else {
            SectionResource selectedSection = getSelectedSection(competition.getSections(), sectionId);
            List<ValidationMessages> financeErrorsMark = markAllQuestionsInSection(application, selectedSection, processRole.getId(), request);

            if (!financeErrorsMark.isEmpty()) {
                ValidationMessages errors = new ValidationMessages(fieldError("formInput[cost]", "application.validation.MarkAsCompleteFailed"));
                ValidationMessages resolvedFinanceErrors = handleMarkSectionValidationMessages(financeErrorsMark);
                errors.addAll(resolvedFinanceErrors);
                return errors;
            }

            return noErrors();
        }
    }

    private ValidationMessages handleMarkSectionValidationMessages(List<ValidationMessages> errors) {

        ValidationMessages resolvedErrors = new ValidationMessages();

        errors.forEach(validationMessage ->
            validationMessage.getErrors().stream()
                .filter(Objects::nonNull)
                .filter(e -> StringUtils.hasText(e.getErrorMessage()))
                .forEach(e -> {
                    if (validationMessage.getObjectName().equals("costItem")) {
                        if (StringUtils.hasText(e.getErrorKey())) {
                            resolvedErrors.addError(fieldError("formInput[cost-" + validationMessage.getObjectId() + "-" + e.getErrorKey() + "]", e.getErrorMessage()));
                        } else {
                            resolvedErrors.addError(fieldError("formInput[cost-" + validationMessage.getObjectId() + "]", e.getErrorMessage()));
                        }
                    } else {
                        resolvedErrors.addError(fieldError("formInput[" + validationMessage.getObjectId() + "]", e.getErrorMessage()));
                    }
                })
        );

        return resolvedErrors;
    }

    private List<ValidationMessages> markAllQuestionsInSection(ApplicationResource application,
                                                               SectionResource selectedSection,
                                                               Long processRoleId,
                                                               HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();

        String action = params.containsKey(MARK_SECTION_AS_COMPLETE) ? MARK_AS_COMPLETE : MARK_AS_INCOMPLETE;

        if(action.equals(MARK_AS_COMPLETE)){
            return sectionService.markAsComplete(selectedSection.getId(), application.getId(), processRoleId);
        }else{
            sectionService.markAsInComplete(selectedSection.getId(), application.getId(), processRoleId);
            return emptyList();
        }
    }

    private boolean isMarkQuestionRequest(@NotNull Map<String, String[]> params){
        return params.containsKey(MARK_AS_COMPLETE) || params.containsKey(MARK_AS_INCOMPLETE);
    }

    private boolean isMarkQuestionAsInCompleteRequest(@NotNull Map<String, String[]> params){
        return params.containsKey(MARK_AS_INCOMPLETE);
    }

    private boolean isMarkSectionRequest(@NotNull Map<String, String[]> params){
        return params.containsKey(MARK_SECTION_AS_COMPLETE) || params.containsKey(MARK_SECTION_AS_INCOMPLETE);
    }

    private boolean isMarkSectionAsIncompleteRequest(@NotNull Map<String, String[]> params){
        return params.containsKey(MARK_SECTION_AS_INCOMPLETE);
    }

    private SectionResource getSelectedSection(List<Long> sectionIds, Long sectionId) {
        return sectionIds.stream()
                .map(sectionService::getById)
                .filter(x -> x.getId().equals(sectionId))
                .findFirst()
                .get();
    }

    private ValidationMessages saveQuestionResponses(ApplicationResource application, List<QuestionResource> questions,
                                                          Long userId,
                                                          Long processRoleId,
                                                          HttpServletRequest request,
                                                          boolean ignoreEmpty) {

        return saveQuestionResponses(request, questions, userId, processRoleId, application.getId(), ignoreEmpty);
    }

    private ValidationMessages markApplicationQuestions(ApplicationResource application, Long processRoleId, HttpServletRequest request, HttpServletResponse response, ValidationMessages errorsSoFar) {
        Map<String, String[]> params = request.getParameterMap();

        ValidationMessages errors = new ValidationMessages();
        boolean marked = markQuestion(request, params, application.getId(), processRoleId, errors, errorsSoFar);

        // if a question is marked as complete, don't show the field saved message.
        if (!marked) {
            cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");
        }

        return errors;
    }

    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button or reassigns a question to another user.
     */
    @ProfileExecution
    @RequestMapping(value = SECTION_URL + "{sectionId}", method = RequestMethod.POST)
    public String applicationFormSubmit(@Valid @ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
                                        BindingResult bindingResult,
                                        Model model,
                                        @PathVariable(APPLICATION_ID) final Long applicationId,
                                        @PathVariable("sectionId") final Long sectionId,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        Map<String, String[]> params = request.getParameterMap();

        if(LOG.isDebugEnabled())
            bindingResult.getAllErrors().forEach(e -> LOG.debug("Validations on application : " + e.getObjectName() + " v: " + e.getDefaultMessage()));

        saveApplicationForm(application, competition, form, applicationId, sectionId, null, request, response, bindingResult);

        if(LOG.isDebugEnabled()){
            bindingResult.getFieldErrors().forEach(e -> LOG.debug("Remote validation field: " + e.getObjectName() + " v: " + e.getField() + " v: " + e.getDefaultMessage()));
            bindingResult.getGlobalErrors().forEach(e -> LOG.debug("Remote validation global: " + e.getObjectName()+ " v: " + e.getCode() + " v: " + e.getDefaultMessage()));
        }

        if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
            assignQuestion(applicationId, request);
            cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
        }

        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());
        model.addAttribute("form", form);

        if(bindingResult.hasErrors()){
            SectionResource section = sectionService.getById(sectionId);
            addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), Optional.ofNullable(section), model, form);
            addOrganisationAndUserFinanceDetails(competition.getId(), application.getId(), user, model, form);
            addNavigation(section, applicationId, model);
            List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
            Optional<OrganisationResource> userOrganisation = getUserOrganisation(user.getId(), userApplicationRoles);
            addCompletedDetails(model, application, userOrganisation);
            return APPLICATION_FORM;
        } else {
            return getRedirectUrl(request, applicationId);
        }
    }

    private boolean markQuestion(HttpServletRequest request, Map<String, String[]> params, Long applicationId, Long processRoleId, ValidationMessages errors, ValidationMessages errorsSoFar) {
        if (processRoleId == null) {
            return false;
        }

        if (params.containsKey(MARK_AS_COMPLETE)) {
            String questionId = request.getParameter(MARK_AS_COMPLETE);

            if (errorsSoFar.hasErrorWithKey("formInput[" + questionId + "]")) {
                errors.addError(fieldError(questionId, "Please enter valid data before marking a question as complete."));
                return false;
            }

            questionService.markAsComplete(Long.valueOf(questionId), applicationId, processRoleId);
            return true;
        }

        if (params.containsKey(MARK_AS_INCOMPLETE)) {
            Long questionId = Long.valueOf(request.getParameter(MARK_AS_INCOMPLETE));
            questionService.markAsInComplete(questionId, applicationId, processRoleId);
            return true;
        }

        return false;
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
        ValidationMessages validationMessages = new ValidationMessages();

        questions.stream()
                .forEach(question -> question.getFormInputs()
                        .stream()
                        .filter(formInput1 -> !"fileupload".equals(formInputService.getOne(formInput1).getFormInputTypeTitle()))
                        .forEach(formInput -> {
                                    if (params.containsKey("formInput[" + formInput + "]")) {
                                        String value = request.getParameter("formInput[" + formInput + "]");
                                        ValidationMessages errors = formInputResponseService.save(userId, applicationId, formInput, value, ignoreEmpty);
                                        List<Error> rescopedErrors = simpleMap(errors.getErrors(), e -> fieldError("formInput[" + formInput + "]", e.getErrorMessage(), e.getArguments()));
                                        validationMessages.addErrors(rescopedErrors);
                                    }
                                }
                        )
                );
        return validationMessages;
    }

    private ValidationMessages saveFileUploadQuestionsIfAny(List<QuestionResource> questions,
                                                                 final Map<String, String[]> params,
                                                                 HttpServletRequest request,
                                                                 Long applicationId,
                                                                 Long processRoleId){
        ValidationMessages errorMap = new ValidationMessages();
        questions.stream()
                .forEach(question -> question.getFormInputs()
                        .stream()
                        .filter(formInput1 -> "fileupload".equals(formInputService.getOne(formInput1).getFormInputTypeTitle()) && request instanceof StandardMultipartHttpServletRequest)
                        .forEach(formInput -> errorMap.addAll(processFormInput(formInput, params, applicationId, processRoleId, request))));
        return errorMap;
    }

    private ValidationMessages processFormInput(Long formInputId, Map<String, String[]> params, Long applicationId, Long processRoleId, HttpServletRequest request){

        if (params.containsKey(REMOVE_UPLOADED_FILE)) {
            formInputResponseService.removeFile(formInputId, applicationId, processRoleId).getSuccessObjectOrThrowException();
        } else {
            final Map<String, MultipartFile> fileMap = ((StandardMultipartHttpServletRequest) request).getFileMap();
            final MultipartFile file = fileMap.get("formInput[" + formInputId + "]");
            if (file != null && !file.isEmpty()) {
                try {
                    RestResult<FileEntryResource> result = formInputResponseService.createFile(formInputId,
                            applicationId,
                            processRoleId,
                            file.getContentType(),
                            file.getSize(),
                            file.getOriginalFilename(),
                            file.getBytes());

                    if (result.isFailure()) {

                        List<Error> errors = simpleMap(result.getFailure().getErrors(), e -> {
                            String lookedUpMessage = MessageUtil.getFromMessageBundle(messageSource, e.getErrorKey(), "Unknown error on file upload", request.getLocale());
                            return fieldError(formInputId + "", lookedUpMessage);
                        });

                        return new ValidationMessages(errors);
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

        if (updatedApplication.getStartDate() != null) {
            LOG.debug("setApplicationDetails date 123: " + updatedApplication.getStartDate().toString());
            if (updatedApplication.getStartDate().isEqual(LocalDate.MIN)
                    || updatedApplication.getStartDate().isBefore(LocalDate.now())) {
                // user submitted a empty date field or date before today
                application.setStartDate(null);
            }else{
                application.setStartDate(updatedApplication.getStartDate());
            }
        }else{
            application.setStartDate(null);
        }
        if (updatedApplication.getDurationInMonths() != null) {
            LOG.debug("setApplicationDetails: " + updatedApplication.getDurationInMonths());
            application.setDurationInMonths(updatedApplication.getDurationInMonths());
        }
    }

    /**
     * This method is for supporting ajax saving from the application form.
     */
    @ProfileExecution
    @RequestMapping(value = "/saveFormElement", method = RequestMethod.POST)
    public @ResponseBody JsonNode saveFormElement(@RequestParam("formInputId") String inputIdentifier,
                                                  @RequestParam("value") String value,
                                                  @PathVariable(APPLICATION_ID) Long applicationId,
                                                  HttpServletRequest request) {
        try {
            String fieldName = request.getParameter("fieldName");
            LOG.info(String.format("saveFormElement: %s / %s", fieldName, value));

            UserResource user = userAuthenticationService.getAuthenticatedUser(request);
            ValidationMessages validationMessages = storeField(applicationId, user.getId(), fieldName, inputIdentifier, value);

            if (validationMessages.hasErrors()) {
                return this.createJsonObjectNode(false, simpleMap(validationMessages.getErrors(), Error::getErrorMessage));
            } else {
                return this.createJsonObjectNode(true, null);
            }
        } catch (Exception e) {
            AutosaveElementException ex = new AutosaveElementException(inputIdentifier, value, applicationId, e);
            String autosaveMessage = handleAutosaveException(e, ex);
            return this.createJsonObjectNode(false, singletonList(autosaveMessage));
        }
    }

    private String handleAutosaveException(Exception e, AutosaveElementException ex) {
        if(e.getClass().equals(IntegerNumberFormatException.class) || e.getClass().equals(BigDecimalNumberFormatException.class)){
            return messageSource.getMessage(e.getMessage(), new Object[] {ex.getErrorMessage()}, Locale.UK);
        }else{
            LOG.error("Got a exception on autosave : "+ e.getMessage());
            LOG.debug("Autosave exception: ", e);
            return ex.getErrorMessage();
        }
    }

    private ValidationMessages storeField(Long applicationId, Long userId, String fieldName, String inputIdentifier, String value) {

        String organisationType = organisationService.getOrganisationType(userId, applicationId);

        if (fieldName.startsWith("application.")) {
            return this.saveApplicationDetails(applicationId, fieldName, value);
        }

        if (inputIdentifier.startsWith("financePosition-") || fieldName.startsWith("financePosition-")) {
            financeHandler.getFinanceFormHandler(organisationType).updateFinancePosition(userId, applicationId, fieldName, value);
            return noErrors();
        }

        if (inputIdentifier.startsWith("cost-") || fieldName.startsWith("cost-")) {

            ValidationMessages fullFinanceValidationErrors = financeHandler.getFinanceFormHandler(organisationType).storeCost(userId, applicationId, fieldName, value);

            if(!fullFinanceValidationErrors.hasErrors()) {
                LOG.debug("no errors");
                return noErrors();
            } else {
                String[] fieldNameParts = fieldName.split("-");
                // fieldname = other_costs-description-34-219
                // filter out the messages that are related to other fields.
                Predicate<Error> onlyThisFieldCheck = e -> fieldNameParts[1].toLowerCase().contains(e.getErrorKey().toLowerCase());
                List<Error> onlyThisFieldErrors = simpleFilter(fullFinanceValidationErrors.getErrors(), onlyThisFieldCheck);
                return new ValidationMessages(onlyThisFieldErrors);
            }
        } else {
            Long formInputId = Long.valueOf(inputIdentifier);
            return formInputResponseService.save(userId, applicationId, formInputId, value, false);
        }
    }

    private ObjectNode createJsonObjectNode(boolean success, List<String> errors) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");
        if (!success) {
            ArrayNode errorsNode = mapper.createArrayNode();
            errors.stream().forEach(errorsNode::add);
            node.set("validation_errors", errorsNode);
        }
        return node;
    }

    private ValidationMessages saveApplicationDetails(Long applicationId, String fieldName, String value) {

        ApplicationResource application = applicationService.getById(applicationId);

        if ("application.name".equals(fieldName)) {
            String trimmedValue = value.trim();
            if (StringUtils.isEmpty(trimmedValue)) {
                return new ValidationMessages(fieldError("application.name", "Please enter the full title of the project"));
            } else {
                application.setName(trimmedValue);
                applicationService.save(application);
                return noErrors();
            }
        } else if (fieldName.startsWith("application.durationInMonths")) {
            Long durationInMonth = Long.valueOf(value);
            if (durationInMonth == null || durationInMonth < 1L) {
                return new ValidationMessages(fieldError("application.durationInMonths", "Please enter a valid duration"));
            } else {
                application.setDurationInMonths(durationInMonth);
                applicationService.save(application);
                return noErrors();
            }
        } else if (fieldName.startsWith(APPLICATION_START_DATE)) {
            return this.saveApplicationStartDate(application, fieldName, value);
        } else {
            return noErrors();
        }
    }

    private ValidationMessages saveApplicationStartDate(ApplicationResource application, String fieldName, String value) {

        ValidationMessages errors = new ValidationMessages();

        LocalDate startDate = application.getStartDate();

        if (fieldName.endsWith(".dayOfMonth")) {
            startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), Integer.parseInt(value));
        } else if (fieldName.endsWith(".monthValue")) {
            startDate = LocalDate.of(startDate.getYear(), Integer.parseInt(value), startDate.getDayOfMonth());
        } else if (fieldName.endsWith(".year")) {
            startDate = LocalDate.of(Integer.parseInt(value), startDate.getMonth(), startDate.getDayOfMonth());
        } else if ("application.startDate".equals(fieldName)){
            String[] parts = value.split("-");
            startDate = LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
        }

        if (startDate.isBefore(LocalDate.now())) {
            errors.addError(fieldError(fieldName, "Please enter a future date"));
            application.setStartDate(null);
            applicationService.save(application);
        } else {
            LOG.debug("Save startdate: "+ startDate.toString());
            application.setStartDate(startDate);
            applicationService.save(application);
        }

        return errors;
    }


    private void assignQuestion(@PathVariable(APPLICATION_ID) final Long applicationId,
                               HttpServletRequest request) {
        assignQuestion(request, applicationId);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application, final CompetitionResource competition, final Long userId, Optional<SectionResource> section, final Model model, final ApplicationForm form) {
        organisationDetailsModelPopulator.populateModel(model, application.getId());
        addApplicationAndSections(application, competition, userId, section, Optional.empty(), model, form);
    }
}
