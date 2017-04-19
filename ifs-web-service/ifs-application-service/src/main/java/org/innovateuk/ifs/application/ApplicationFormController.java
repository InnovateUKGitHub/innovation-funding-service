package org.innovateuk.ifs.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.service.FinanceRowService;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.viewmodel.AcademicFinanceViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.*;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.QuestionOrganisationDetailsViewModel;
import org.innovateuk.ifs.application.viewmodel.QuestionViewModel;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.exception.AutosaveElementException;
import org.innovateuk.ifs.exception.BigDecimalNumberFormatException;
import org.innovateuk.ifs.exception.IntegerNumberFormatException;
import org.innovateuk.ifs.exception.UnableToReadUploadedFile;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.profiling.ProfileExecution;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.AjaxResult;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.ErrorConverterFactory.toField;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.collectValidationMessages;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.noErrors;
import static org.innovateuk.ifs.controller.ErrorLookupHelper.lookupErrorMessageResourceBundleEntries;
import static org.innovateuk.ifs.controller.ErrorLookupHelper.lookupErrorMessageResourceBundleEntry;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.HttpUtils.requestParameterPresent;
import static org.springframework.util.StringUtils.hasText;

/**
 * This controller will handle all requests that are related to the application form.
 */
@Controller
@RequestMapping(ApplicationFormController.APPLICATION_BASE_URL + "{applicationId}/form")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationFormController {

    private static final Log LOG = LogFactory.getLog(ApplicationFormController.class);

    public static final String QUESTION_URL = "/question/";
    public static final String QUESTION_ID = "questionId";
    public static final String MODEL_ATTRIBUTE_MODEL = "model";
    public static final String MODEL_ATTRIBUTE_FORM = "form";
    public static final String APPLICATION_ID = "applicationId";
    public static final String APPLICATION_FORM = "application-form";
    public static final String SECTION_URL = "/section/";
    public static final String EDIT_QUESTION = "edit_question";
    public static final String ASSIGN_QUESTION_PARAM = "assign_question";
    public static final String MARK_AS_COMPLETE = "mark_as_complete";
    public static final String MARK_SECTION_AS_COMPLETE = "mark_section_as_complete";
    public static final String ADD_COST = "add_cost";
    public static final String REMOVE_COST = "remove_cost";
    public static final String MARK_SECTION_AS_INCOMPLETE = "mark_section_as_incomplete";
    public static final String MARK_AS_INCOMPLETE = "mark_as_incomplete";
    public static final String NOT_REQUESTING_FUNDING = "not_requesting_funding";
    public static final String ACADEMIC_FINANCE_REMOVE = "remove_finance_document";
    public static final String REQUESTING_FUNDING = "requesting_funding";
    public static final String UPLOAD_FILE = "upload_file";
    public static final String REMOVE_UPLOADED_FILE = "remove_uploaded_file";
    public static final String TERMS_AGREED_KEY = "termsAgreed";
    public static final String STATE_AID_AGREED_KEY = "stateAidAgreed";
    public static final String ORGANISATION_SIZE_KEY = "organisationSize";
    public static final String APPLICATION_BASE_URL = "/application/";
    public static final String APPLICATION_START_DATE = "application.startDate";

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private QuestionModelPopulator questionModelPopulator;

    @Autowired
    private OpenSectionModelPopulator openSectionModel;

    @Autowired
    private OrganisationDetailsViewModelPopulator organisationDetailsViewModelPopulator;

    @Autowired
    private OpenApplicationFinanceSectionModelPopulator openFinanceSectionModel;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OverheadFileSaver overheadFileSaver;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder, WebRequest webRequest) {
        dataBinder.registerCustomEditor(String.class, new StringMultipartFileEditor());
    }

    @ProfileExecution
    @GetMapping(value = {QUESTION_URL + "{" + QUESTION_ID + "}", QUESTION_URL + "edit/{" + QUESTION_ID + "}"})
    public String showQuestion(@ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               @SuppressWarnings("unused") ValidationHandler validationHandler,
                               Model model,
                               @PathVariable(APPLICATION_ID) final Long applicationId,
                               @PathVariable(QUESTION_ID) final Long questionId,
                               HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);

        QuestionOrganisationDetailsViewModel organisationDetailsViewModel = organisationDetailsViewModelPopulator.populateModel(applicationId);
        QuestionViewModel questionViewModel = questionModelPopulator.populateModel(questionId, applicationId, user, model, form, organisationDetailsViewModel);

        model.addAttribute(MODEL_ATTRIBUTE_MODEL, questionViewModel);
        applicationNavigationPopulator.addAppropriateBackURLToModel(applicationId, model, null);

        return APPLICATION_FORM;
    }

    @ProfileExecution
    @GetMapping(QUESTION_URL + "{" + QUESTION_ID + "}/forminput/{formInputId}/download")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadApplicationFinanceFile(
            @PathVariable(APPLICATION_ID) final Long applicationId,
            @PathVariable("formInputId") final Long formInputId,
            HttpServletRequest request) {
        final UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), applicationId);
        final ByteArrayResource resource = formInputResponseService.getFile(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        final FormInputResponseFileEntryResource fileDetails = formInputResponseService.getFileDetails(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileDetails.getFileEntryResource());
    }

    @GetMapping("/{applicationFinanceId}/finance-download")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadApplicationFinanceFile(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId) {

        final ByteArrayResource resource = financeService.getFinanceDocumentByApplicationFinance(applicationFinanceId).getSuccessObjectOrThrowException();
        final FileEntryResource fileDetails = financeService.getFinanceEntryByApplicationFinanceId(applicationFinanceId).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileDetails);
    }

    @ProfileExecution
    @GetMapping(SECTION_URL + "{sectionId}")
    public String applicationFormWithOpenSection(@Valid @ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form, BindingResult bindingResult, Model model,
                                                 @PathVariable(APPLICATION_ID) final Long applicationId,
                                                 @PathVariable("sectionId") final Long sectionId,
                                                 HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());
        SectionResource section = simpleFilter(allSections, s -> sectionId.equals(s.getId())).get(0);

        Long organisationId = userService.getUserOrganisationId(user.getId(), applicationId);

        populateSection(model, form, bindingResult, application, user, organisationId, section, allSections);

        return APPLICATION_FORM;
    }

    private void populateSection(Model model,
                                 ApplicationForm form,
                                 BindingResult bindingResult,
                                 ApplicationResource application,
                                 UserResource user,
                                 Long organisationId,
                                 SectionResource section,
                                 List<SectionResource> allSections) {
        if (SectionType.GENERAL.equals(section.getType())
                || SectionType.OVERVIEW_FINANCES.equals(section.getType())) {
            OpenSectionViewModel viewModel = (OpenSectionViewModel) openSectionModel.populateModel(
                    form, model, application, section, user, bindingResult, allSections, organisationId);
            model.addAttribute(MODEL_ATTRIBUTE_MODEL, viewModel);
        } else {
            OpenFinanceSectionViewModel viewModel = (OpenFinanceSectionViewModel) openFinanceSectionModel.populateModel(
                    form, model, application, section, user, bindingResult, allSections, organisationId);

            if (viewModel.getFinanceViewModel() instanceof AcademicFinanceViewModel) {
                viewModel.setNavigationViewModel(applicationNavigationPopulator.addNavigation(section, application.getId(),
                        Collections.singletonList(SectionType.ORGANISATION_FINANCES)));
            }

            model.addAttribute(MODEL_ATTRIBUTE_MODEL, viewModel);
        }
        applicationNavigationPopulator.addAppropriateBackURLToModel(application.getId(), model, section);
    }

    @ProfileExecution
    @PostMapping(value = {QUESTION_URL + "{" + QUESTION_ID + "}", QUESTION_URL + "edit/{" + QUESTION_ID + "}"})
    public String questionFormSubmit(@ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     @PathVariable(APPLICATION_ID) final Long applicationId,
                                     @PathVariable(QUESTION_ID) final Long questionId,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        Map<String, String[]> params = request.getParameterMap();

        // Check if the request is to just open edit view or to save
        if (params.containsKey(EDIT_QUESTION)) {

            ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), applicationId);
            if (processRole != null) {
                questionService.markAsInComplete(questionId, applicationId, processRole.getId());
            } else {
                LOG.error("Not able to find process role for user " + user.getName() + " for application id " + applicationId);
            }

            return showQuestion(form, bindingResult, validationHandler, model, applicationId, questionId, request);

        } else {

            QuestionResource question = questionService.getById(questionId);
            ApplicationResource application = applicationService.getById(applicationId);
            CompetitionResource competition = competitionService.getById(application.getCompetition());

            if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
                assignQuestion(applicationId, request);
                cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
            }

            ValidationMessages errors = new ValidationMessages();

            // First check if any errors already exist in bindingResult
            if (isAllowedToUpdateQuestion(questionId, applicationId, user.getId()) || isMarkQuestionRequest(params)) {
                /* Start save action */
                errors.addAll(saveApplicationForm(application, competition, form, null, question, request, response, bindingResult, true));
            }

            model.addAttribute("form", form);

            /* End save action */
            if (isUploadWithValidationErrors(request, errors) || isMarkAsCompleteRequestWithValidationErrors(params, errors, bindingResult)) {
                validationHandler.addAnyErrors(errors);

                // Add any validated fields back in invalid entries are displayed on re-render
                QuestionOrganisationDetailsViewModel organisationDetailsViewModel = organisationDetailsViewModelPopulator.populateModel(applicationId);
                QuestionViewModel questionViewModel = questionModelPopulator.populateModel(questionId, applicationId, user, model, form, organisationDetailsViewModel);

                model.addAttribute(MODEL_ATTRIBUTE_MODEL, questionViewModel);
                applicationNavigationPopulator.addAppropriateBackURLToModel(applicationId, model, null);
                return APPLICATION_FORM;
            } else {
                return getRedirectUrl(request, applicationId, Optional.empty());
            }
        }
    }

    private Boolean isMarkAsCompleteRequestWithValidationErrors(Map<String, String[]> params, ValidationMessages errors, BindingResult bindingResult) {
        return ((errors.hasErrors() || bindingResult.hasErrors()) && isMarkQuestionRequest(params));
    }

    private Boolean isUploadWithValidationErrors(HttpServletRequest request, ValidationMessages errors) {
        return (request.getParameter(UPLOAD_FILE) != null && errors.hasErrors());
    }

    private Boolean isAllowedToUpdateQuestion(Long questionId, Long applicationId, Long userId) {
        List<QuestionStatusResource> questionStatuses = questionService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId);
        return questionStatuses.isEmpty() || questionStatuses.stream()
                .anyMatch(questionStatusResource -> (
                        questionStatusResource.getAssignee() == null || questionStatusResource.getAssigneeUserId().equals(userId))
                        && (questionStatusResource.getMarkedAsComplete() == null || !questionStatusResource.getMarkedAsComplete()));
    }

    private String getRedirectUrl(HttpServletRequest request, Long applicationId, Optional<SectionType> sectionType) {
        if (request.getParameter("submit-section") == null
                && (request.getParameter(ASSIGN_QUESTION_PARAM) != null ||
                request.getParameter(MARK_AS_INCOMPLETE) != null ||
                request.getParameter(MARK_SECTION_AS_INCOMPLETE) != null ||
                request.getParameter(ADD_COST) != null ||
                request.getParameter(REMOVE_COST) != null ||
                request.getParameter(MARK_AS_COMPLETE) != null ||
                request.getParameter(REMOVE_UPLOADED_FILE) != null ||
                request.getParameter(UPLOAD_FILE) != null ||
                request.getParameter(EDIT_QUESTION) != null ||
                request.getParameter(REQUESTING_FUNDING) != null ||
                request.getParameter(NOT_REQUESTING_FUNDING) != null ||
                request.getParameter(ACADEMIC_FINANCE_REMOVE) != null)) {
            // user did a action, just display the same page.
            LOG.debug("redirect: " + request.getRequestURI());
            return "redirect:" + request.getRequestURI();
        } else if (request.getParameter("submit-section-redirect") != null) {
            return "redirect:" + APPLICATION_BASE_URL + applicationId + request.getParameter("submit-section-redirect");
        } else {
            if (sectionType.isPresent() && sectionType.get().getParent().isPresent()) {
                return redirectToSection(sectionType.get().getParent().get(), applicationId);
            }
            // add redirect, to make sure the user cannot resubmit the form by refreshing the page.
            LOG.debug("default redirect: ");
            return "redirect:" + APPLICATION_BASE_URL + applicationId;
        }
    }

    @GetMapping(value = "/add_cost/{" + QUESTION_ID + "}")
    public String addCostRow(@ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
                             BindingResult bindingResult,
                             Model model,
                             @PathVariable(APPLICATION_ID) final Long applicationId,
                             @PathVariable(QUESTION_ID) final Long questionId,
                             HttpServletRequest request) {
        FinanceRowItem costItem = addCost(applicationId, questionId, request);
        FinanceRowType costType = costItem.getCostType();
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        Long organisationId = userService.getUserOrganisationId(user.getId(), applicationId);

        Set<Long> markedAsComplete = new TreeSet<>();
        model.addAttribute("markedAsComplete", markedAsComplete);
        Long organisationType = organisationService.getOrganisationType(user.getId(), applicationId);

        financeHandler.getFinanceModelManager(organisationType).addCost(model, costItem, applicationId, organisationId, user.getId(), questionId, costType);

        form.setBindingResult(bindingResult);
        return String.format("finance/finance :: %s_row", costType.getType());
    }

    @GetMapping("/remove_cost/{costId}")
    public @ResponseBody
    String removeCostRow(@PathVariable("costId") final Long costId) throws JsonProcessingException {
        financeRowService.delete(costId);
        AjaxResult ajaxResult = new AjaxResult(HttpStatus.OK, "true");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ajaxResult);
    }

    private FinanceRowItem addCost(Long applicationId, Long questionId, HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        Long organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
        return financeHandler.getFinanceFormHandler(organisationType).addCostWithoutPersisting(applicationId, user.getId(), questionId);
    }

    private ValidationMessages saveApplicationForm(ApplicationResource application,
                                                   CompetitionResource competition,
                                                   ApplicationForm form,
                                                   Long sectionId, QuestionResource question,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response, BindingResult bindingResult, Boolean validFinanceTerms) {

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
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

        if (null != selectedSection) {
            if (isMarkSectionAsCompleteRequest(params)) {
                application.setStateAidAgreed(form.isStateAidAgreed());
            } else if (isMarkSectionAsIncompleteRequest(params) && selectedSection.getType() == SectionType.FINANCE) {
                application.setStateAidAgreed(Boolean.FALSE);
            }
        }

        // Prevent saving question when it's a unmark question request (INFUND-2936)
        if (!isMarkQuestionAsInCompleteRequest(params)) {
            if (question != null) {
                errors.addAll(saveQuestionResponses(request, singletonList(question), user.getId(), processRole.getId(), application.getId(), ignoreEmpty));
            } else {
                List<QuestionResource> questions = simpleMap(selectedSection.getQuestions(), questionService::getById);
                errors.addAll(saveQuestionResponses(request, questions, user.getId(), processRole.getId(), application.getId(), ignoreEmpty));
            }
        }

        if (isNotRequestingFundingRequest(params)) {
            setRequestingFunding(NOT_REQUESTING_FUNDING, user.getId(), application.getId(), competition.getId(), processRole.getId(), errors);
        }

        if (isRequestingFundingRequest(params)) {
            setRequestingFunding(REQUESTING_FUNDING, user.getId(), application.getId(), competition.getId(), processRole.getId(), errors);
        }

        setApplicationDetails(application, form.getApplication());

        if (applicationModelPopulator.userIsLeadApplicant(application, user.getId())) {
            applicationService.save(application);
        }

        errors.addAll(overheadFileSaver.handleOverheadFileRequest(request));

        if (!isMarkSectionAsIncompleteRequest(params)) {
            Long organisationType = organisationService.getOrganisationType(user.getId(), application.getId());
            ValidationMessages saveErrors = financeHandler.getFinanceFormHandler(organisationType).update(request, user.getId(), application.getId(), competition.getId());

            if (!overheadFileSaver.isOverheadFileRequest(request)) {
                errors.addAll(saveErrors);
            }

            markOrganisationFinancesAsNotRequired(organisationType, selectedSection, application.getId(), competition.getId(), processRole.getId());
        }

        if (isMarkQuestionRequest(params)) {
            errors.addAll(handleApplicationDetailsMarkCompletedRequest(application, request, response, processRole, errors, bindingResult));
        } else if (isMarkSectionRequest(params)) {
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
        errors.addAll(financeRowService.add(finance.getId(), financeQuestion.getId(), finance.getGrantClaim()));

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

    private void markOrganisationFinancesAsNotRequired(Long organisationType, SectionResource selectedSection, Long applicationId, Long competitionId, Long processRoleId) {

        if (selectedSection != null && (SectionType.FUNDING_FINANCES.equals(selectedSection.getType()) || SectionType.PROJECT_COST_FINANCES.equals(selectedSection.getType()))
                && OrganisationTypeEnum.RESEARCH.getOrganisationTypeId().equals(organisationType)) {
            SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES).get(0);
            sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
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
        } else {

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

    private boolean isMarkQuestionRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_AS_COMPLETE) || params.containsKey(MARK_AS_INCOMPLETE);
    }

    private boolean isMarkQuestionAsInCompleteRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_AS_INCOMPLETE);
    }

    private boolean isNotRequestingFundingRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(NOT_REQUESTING_FUNDING);
    }

    private boolean isRequestingFundingRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(REQUESTING_FUNDING);
    }

    private boolean isMarkSectionRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_SECTION_AS_COMPLETE) || params.containsKey(MARK_SECTION_AS_INCOMPLETE);
    }

    private boolean isMarkSectionAsIncompleteRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_SECTION_AS_INCOMPLETE);
    }

    private boolean isMarkSectionAsCompleteRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_SECTION_AS_COMPLETE);
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

    @ProfileExecution
    @PostMapping(SECTION_URL + "{sectionId}")
    public String applicationFormSubmit(@Valid @ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
                                        BindingResult bindingResult, ValidationHandler validationHandler,
                                        Model model,
                                        @PathVariable(APPLICATION_ID) final Long applicationId,
                                        @PathVariable("sectionId") final Long sectionId,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {

        logSaveApplicationBindingErrors(validationHandler);

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());
        SectionResource section = sectionService.getById(sectionId);
        Long organisationId = userService.getUserOrganisationId(user.getId(), applicationId);

        model.addAttribute("form", form);

        Map<String, String[]> params = request.getParameterMap();

        Boolean validFinanceTerms = validFinanceTermsForMarkAsComplete(form, bindingResult, section, params, user.getId(), applicationId);
        ValidationMessages saveApplicationErrors = saveApplicationForm(application, competition, form, sectionId, null, request, response, bindingResult, validFinanceTerms);
        logSaveApplicationErrors(bindingResult);

        if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
            assignQuestion(applicationId, request);
            cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
        }

        if (saveApplicationErrors.hasErrors() || !validFinanceTerms || overheadFileSaver.isOverheadFileRequest(request)) {
            validationHandler.addAnyErrors(saveApplicationErrors);
            populateSection(model, form, bindingResult, application, user, organisationId, section, allSections);
            return APPLICATION_FORM;
        } else {
            return getRedirectUrl(request, applicationId, Optional.of(section.getType()));
        }
    }

    private Boolean validFinanceTermsForMarkAsComplete(ApplicationForm form, BindingResult bindingResult, SectionResource section, Map<String, String[]> params, Long userId, Long applicationId) {
        Boolean valid = Boolean.TRUE;

        if (!isMarkSectionAsCompleteRequest(params)) {
            return valid;
        }

        if (SectionType.FUNDING_FINANCES.equals(section.getType())) {
            if (!form.isTermsAgreed()) {
                bindingResult.rejectValue(TERMS_AGREED_KEY, "APPLICATION_AGREE_TERMS_AND_CONDITIONS");
                valid = Boolean.FALSE;
            }
        }

        if (SectionType.PROJECT_COST_FINANCES.equals(section.getType())) {
            if (!form.isStateAidAgreed()) {
                bindingResult.rejectValue(STATE_AID_AGREED_KEY, "APPLICATION_AGREE_STATE_AID_CONDITIONS");
                valid = Boolean.FALSE;
            }
        }

        if (SectionType.ORGANISATION_FINANCES.equals(section.getType())) {
            List<String> financePositionKeys = params.keySet().stream().filter(k -> k.contains("financePosition-")).collect(Collectors.toList());
            Long organisationType = organisationService.getOrganisationType(userId, applicationId);
            if (financePositionKeys.isEmpty() && !OrganisationTypeEnum.RESEARCH.getOrganisationTypeId().equals(organisationType)) {
                bindingResult.rejectValue(ORGANISATION_SIZE_KEY, "APPLICATION_ORGANISATION_SIZE_REQUIRED");
                valid = Boolean.FALSE;
            }
        }

        return valid;
    }

    private void logSaveApplicationBindingErrors(ValidationHandler validationHandler) {
        if (LOG.isDebugEnabled())
            validationHandler.getAllErrors().forEach(e -> LOG.debug("Validations on application : " + e.getObjectName() + " v: " + e.getDefaultMessage()));
    }

    private void logSaveApplicationErrors(BindingResult bindingResult) {
        if (LOG.isDebugEnabled()) {
            bindingResult.getFieldErrors().forEach(e -> LOG.debug("Remote validation field: " + e.getObjectName() + " v: " + e.getField() + " v: " + e.getDefaultMessage()));
            bindingResult.getGlobalErrors().forEach(e -> LOG.debug("Remote validation global: " + e.getObjectName() + " v: " + e.getCode() + " v: " + e.getDefaultMessage()));
        }
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
        questions.stream()
                .forEach(question ->
                        {
                            List<FormInputResource> formInputs = formInputService.findApplicationInputsByQuestion(question.getId());
                            formInputs
                                    .stream()
                                    .filter(formInput1 -> FILEUPLOAD != formInput1.getType())
                                    .forEach(formInput -> {
                                        String formInputKey = "formInput[" + formInput.getId() + "]";

                                        requestParameterPresent(formInputKey, request).ifPresent(value -> {
                                            ValidationMessages errors = formInputResponseService.save(userId, applicationId, formInput.getId(), value, ignoreEmpty);
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
        questions.stream()
                .forEach(question -> {
                    List<FormInputResource> formInputs = formInputService.findApplicationInputsByQuestion(question.getId());
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
            formInputResponseService.removeFile(formInputId, applicationId, processRoleId).getSuccessObjectOrThrowException();
            return noErrors();
        } else {
            final Map<String, MultipartFile> fileMap = ((MultipartHttpServletRequest) request).getFileMap();
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

    /**
     * This method is for supporting ajax saving from the application form.
     */
    @ProfileExecution
    @PostMapping("/{competitionId}/saveFormElement")
    @ResponseBody
    public JsonNode saveFormElement(@RequestParam("formInputId") String inputIdentifier,
                                    @RequestParam("value") String value,
                                    @PathVariable(APPLICATION_ID) Long applicationId,
                                    @PathVariable("competitionId") Long competitionId,
                                    HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        Long fieldId = null;
        try {
            String fieldName = request.getParameter("fieldName");
            LOG.info(String.format("saveFormElement: %s / %s", fieldName, value));

            UserResource user = userAuthenticationService.getAuthenticatedUser(request);
            StoreFieldResult storeFieldResult = storeField(applicationId, user.getId(), competitionId, fieldName, inputIdentifier, value);

            fieldId = storeFieldResult.getFieldId();

            return this.createJsonObjectNode(true, fieldId);

        } catch (Exception e) {
            AutosaveElementException ex = new AutosaveElementException(inputIdentifier, value, applicationId, e);
            handleAutosaveException(errors, e, ex);
            return this.createJsonObjectNode(false, fieldId);
        }
    }

    private void handleAutosaveException(List<String> errors, Exception e, AutosaveElementException ex) {
        List<Object> args = new ArrayList<>();
        args.add(ex.getErrorMessage());
        if (e.getClass().equals(IntegerNumberFormatException.class) || e.getClass().equals(BigDecimalNumberFormatException.class)) {
            errors.add(lookupErrorMessageResourceBundleEntry(messageSource, e.getMessage(), args));
        } else {
            LOG.error("Got an exception on autosave : " + e.getMessage());
            LOG.debug("Autosave exception: ", e);
            errors.add(ex.getErrorMessage());
        }
    }

    private StoreFieldResult storeField(Long applicationId, Long userId, Long competitionId, String fieldName, String inputIdentifier, String value) {
        Long organisationType = organisationService.getOrganisationType(userId, applicationId);

        if (fieldName.startsWith("application.")) {

            // this does not need id
            List<String> errors = this.saveApplicationDetails(applicationId, fieldName, value);
            return new StoreFieldResult(errors);
        } else if (inputIdentifier.startsWith("financePosition-") || fieldName.startsWith("financePosition-")) {
            financeHandler.getFinanceFormHandler(organisationType).updateFinancePosition(userId, applicationId, fieldName, value, competitionId);
            return new StoreFieldResult();
        } else if (inputIdentifier.startsWith("cost-") || fieldName.startsWith("cost-")) {
            ValidationMessages validationMessages = financeHandler.getFinanceFormHandler(organisationType).storeCost(userId, applicationId, fieldName, value, competitionId);

            if (validationMessages == null || validationMessages.getErrors() == null || validationMessages.getErrors().isEmpty()) {
                LOG.debug("no errors");
                if (validationMessages == null) {
                    return new StoreFieldResult();
                } else {
                    return new StoreFieldResult(validationMessages.getObjectId());
                }
            } else {
                String[] fieldNameParts = fieldName.split("-");
                // fieldname = other_costs-description-34-219
                List<String> errors = validationMessages.getErrors()
                        .stream()
                        .peek(e -> LOG.debug(String.format("Compare: %s => %s ", fieldName.toLowerCase(), e.getFieldName().toLowerCase())))
                        .filter(e -> fieldNameParts[1].toLowerCase().contains(e.getFieldName().toLowerCase())) // filter out the messages that are related to other fields.
                        .map(this::lookupErrorMessage)
                        .collect(toList());
                return new StoreFieldResult(validationMessages.getObjectId(), errors);
            }
        } else {
            Long formInputId = Long.valueOf(inputIdentifier);
            ValidationMessages saveErrors = formInputResponseService.save(userId, applicationId, formInputId, value, false);
            List<String> lookedUpErrorMessages = lookupErrorMessageResourceBundleEntries(messageSource, saveErrors);
            return new StoreFieldResult(lookedUpErrorMessages);
        }
    }

    private String lookupErrorMessage(Error e) {
        return lookupErrorMessageResourceBundleEntry(messageSource, e);
    }

    private ObjectNode createJsonObjectNode(boolean success, Long fieldId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");

        if (fieldId != null) {
            node.set("fieldId", new LongNode(fieldId));
        }
        return node;
    }

    private List<String> saveApplicationDetails(Long applicationId, String fieldName, String value) {
        List<String> errors = new ArrayList<>();
        ApplicationResource application = applicationService.getById(applicationId);

        if ("application.name".equals(fieldName)) {
            String trimmedValue = value.trim();
            if (StringUtils.isEmpty(trimmedValue)) {
                errors.add("Please enter the full title of the project");
            } else {

                application.setName(trimmedValue);
                applicationService.save(application);
            }
        } else if (fieldName.startsWith("application.durationInMonths")) {
            Long durationInMonth = Long.valueOf(value);
            if (durationInMonth < 1L || durationInMonth > 36L) {
                errors.add("Your project should last between 1 and 36 months");
                application.setDurationInMonths(durationInMonth);
            } else {
                application.setDurationInMonths(durationInMonth);
                applicationService.save(application);
            }
        } else if (fieldName.startsWith(APPLICATION_START_DATE)) {
            errors = this.saveApplicationStartDate(application, fieldName, value);
        } else if (fieldName.equals("application.resubmission")) {
            application.setResubmission(Boolean.valueOf(value));
            applicationService.save(application);
        } else if (fieldName.equals("application.previousApplicationNumber")) {
            application.setPreviousApplicationNumber(value);
            applicationService.save(application);
        } else if (fieldName.equals("application.previousApplicationTitle")) {
            application.setPreviousApplicationTitle(value);
            applicationService.save(application);
        }
        return errors;
    }

    private List<String> saveApplicationStartDate(ApplicationResource application, String fieldName, String value) {
        List<String> errors = new ArrayList<>();
        LocalDate startDate = application.getStartDate();
        if (fieldName.endsWith(".dayOfMonth")) {
            startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), Integer.parseInt(value));
        } else if (fieldName.endsWith(".monthValue")) {
            startDate = LocalDate.of(startDate.getYear(), Integer.parseInt(value), startDate.getDayOfMonth());
        } else if (fieldName.endsWith(".year")) {
            startDate = LocalDate.of(Integer.parseInt(value), startDate.getMonth(), startDate.getDayOfMonth());
        } else if ("application.startDate".equals(fieldName)) {
            String[] parts = value.split("-");
            startDate = LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
        }
        if (startDate.isBefore(LocalDate.now())) {
            errors.add("Please enter a future date");
            startDate = null;
        } else {
            LOG.debug("Save startdate: " + startDate.toString());
        }
        application.setStartDate(startDate);
        applicationService.save(application);
        return errors;
    }

    private void assignQuestion(@PathVariable(APPLICATION_ID) final Long applicationId,
                                HttpServletRequest request) {

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRoleResource assignedBy = processRoleService.findProcessRole(user.getId(), applicationId);

        questionService.assignQuestion(applicationId, request, assignedBy);
    }

    private static class StoreFieldResult {
        private Long fieldId;
        private List<String> errors = new ArrayList<>();

        public StoreFieldResult() {
        }

        public StoreFieldResult(Long fieldId) {
            this.fieldId = fieldId;
        }

        public StoreFieldResult(List<String> errors) {
            this.errors = errors;
        }

        public StoreFieldResult(Long fieldId, List<String> errors) {
            this.fieldId = fieldId;
            this.errors = errors;
        }

        public List<String> getErrors() {
            return errors;
        }

        public Long getFieldId() {
            return fieldId;
        }
    }


    @ProfileExecution
    @GetMapping("/{sectionType}")
    public String redirectToSection(@PathVariable("sectionType") SectionType type,
                                    @PathVariable(APPLICATION_ID) Long applicationId) {
        ApplicationResource application = applicationService.getById(applicationId);
        List<SectionResource> sections = sectionService.getSectionsForCompetitionByType(application.getCompetition(), type);
        if (sections.size() == 1) {
            return "redirect:/application/" + applicationId + "/form/section/" + sections.get(0).getId();
        }
        return "redirect:/application/" + applicationId;
    }
}
