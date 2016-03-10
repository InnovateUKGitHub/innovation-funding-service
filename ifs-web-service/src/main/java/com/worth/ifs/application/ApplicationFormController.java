package com.worth.ifs.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.view.DefaultFinanceFormHandler;
import com.worth.ifs.application.finance.view.FinanceHandler;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.exception.AutosaveElementException;
import com.worth.ifs.exception.UnableToReadUploadedFile;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.profiling.ProfileExecution;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.util.AjaxResult;
import com.worth.ifs.util.MessageUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * This controller will handle all requests that are related to the application form.
 */
@Controller
@RequestMapping("/application/{applicationId}/form")
public class ApplicationFormController extends AbstractApplicationController {
    public static final String MARK_AS_COMPLETE = "mark_as_complete";
    public static final String MARK_AS_INCOMPLETE = "mark_as_incomplete";
    public static final String UPLOAD_FILE = "upload_file";
    public static final String REMOVE_UPLOADED_FILE = "remove_uploaded_file";
    public static final String ADD_COST = "add_cost";
    public static final String REMOVE_COST = "remove_cost";
    public static final String EDIT_QUESTION = "edit_question";

    private static final Log log = LogFactory.getLog(ApplicationFormController.class);

    @Autowired
    private CostService costService;

    @Autowired
    private FinanceHandler financeHandler;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder, WebRequest webRequest) {
        dataBinder.registerCustomEditor(LocalDate.class, "application.startDate", new LocalDatePropertyEditor(webRequest));
        dataBinder.registerCustomEditor(String.class, new StringMultipartFileEditor());
    }

    @ProfileExecution
    @RequestMapping
    public String applicationForm(@ModelAttribute("form") ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                  HttpServletRequest request) throws Exception {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRole> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        this.addFormAttributes(application, competition, Optional.empty(), user.getId(), model, form, Optional.empty(),
                userApplicationRoles);
        return "application-form";
    }

    @ProfileExecution
    @RequestMapping(value = {"/question/{questionId}", "/question/edit/{questionId}"}, method = RequestMethod.GET)
    public String showQuestion(@ModelAttribute("form") ApplicationForm form,
                               BindingResult bindingResult, Model model,
                               @PathVariable("applicationId") final Long applicationId,
                               @PathVariable("questionId") final Long questionId,
                               HttpServletRequest request) throws Exception {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        Question question = questionService.getById(questionId);
        SectionResource section = sectionService.getSectionByQuestionId(questionId);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRole> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        this.addFormAttributes(application, competition, Optional.ofNullable(section), user.getId(), model, form,
                Optional.ofNullable(question), userApplicationRoles);
        form.bindingResult = bindingResult;
        form.objectErrors = bindingResult.getAllErrors();
        return "application-form";
    }

    @ProfileExecution
    @RequestMapping(value = "/question/{questionId}/forminput/{formInputId}/download", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> downloadQuestionFile(
                                @PathVariable("applicationId") final Long applicationId,
                                @PathVariable("questionId") final Long questionId,
                                @PathVariable("formInputId") final Long formInputId,
                                HttpServletRequest request) throws Exception {
        final User user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRole processRole = processRoleService.findProcessRole(user.getId(), applicationId);
        final ByteArrayResource resource = formInputResponseService.getFile(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(resource.contentLength());
        httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
        return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
    }

    @ProfileExecution
    @RequestMapping(value = "/section/{sectionId}", method = RequestMethod.GET)
    public String applicationFormWithOpenSection(@Valid @ModelAttribute("form") ApplicationForm form, BindingResult bindingResult, Model model,
                                                 @PathVariable("applicationId") final Long applicationId,
                                                 @PathVariable("sectionId") final Long sectionId,
                                                 HttpServletRequest request) throws Exception {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        SectionResource section = sectionService.getById(sectionId);

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSections(application, competition, user.getId(), Optional.ofNullable(section), Optional.empty(), model, form);
        addOrganisationAndUserFinanceDetails(applicationId, user.getId(), model, form);

        addNavigation(section, applicationId, model);

        form.bindingResult = bindingResult;
        form.objectErrors = bindingResult.getAllErrors();

        return "application-form";
    }

    private void addFormAttributes(ApplicationResource application,
                                   CompetitionResource competition,
                                   Optional<SectionResource> section,
                                   Long userId, Model model,
                                   ApplicationForm form, Optional<Question> question,
                                   List<ProcessRole> userApplicationRoles){
        addApplicationDetails(application, competition, userId, section, Optional.ofNullable(question.get().getId()), model, form, userApplicationRoles);
        addNavigation(question.get(), application.getId(), model);
        model.addAttribute("currentQuestion", question.get());
        if(question.isPresent()) {
            model.addAttribute("title", question.get().getShortName());
        }
    }

    @ProfileExecution
    @RequestMapping(value = {"/question/{questionId}", "/question/edit/{questionId}"}, method = RequestMethod.POST)
    public String questionFormSubmit(@Valid @ModelAttribute("form") ApplicationForm form,
                                     BindingResult bindingResult,
                                     Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("questionId") final Long questionId,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        User user = userAuthenticationService.getAuthenticatedUser(request);

        Map<String, String[]> params = request.getParameterMap();

        // Check if the request is to just open edit view or to save
        if(params.containsKey(EDIT_QUESTION)){
            ProcessRole processRole = processRoleService.findProcessRole(user.getId(), applicationId);
            if (processRole != null) {
                questionService.markAsInComplete(questionId, applicationId, processRole.getId());
            } else {
                log.error("Not able to find process role for user " + user.getName() + " for application id " + applicationId);
            }
            return showQuestion(form, bindingResult, model, applicationId, questionId, request);
        } else {
            Question question = questionService.getById(questionId);
            SectionResource section = sectionService.getSectionByQuestionId(questionId);
            ApplicationResource application = applicationService.getById(applicationId);
            CompetitionResource competition = competitionService.getById(application.getCompetition());
            List<ProcessRole> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

            /* Start save action */
            bindingResult = saveApplicationForm(application, competition, form, applicationId, null, question, request, response, bindingResult);

            if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
                assignQuestion(applicationId, request);
                cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
            }

            form.bindingResult = bindingResult;
            form.objectErrors = bindingResult.getAllErrors();
            /* End save action */

            if (bindingResult.hasErrors()) {
                this.addFormAttributes(application, competition, Optional.ofNullable(section), user.getId(), model, form,
                        Optional.ofNullable(question), userApplicationRoles);
                return "application-form";
            } else {
                return getRedirectUrl(request, applicationId);
            }
        }
    }

    private String getRedirectUrl(HttpServletRequest request, Long applicationId) {
        if (request.getParameter(ASSIGN_QUESTION_PARAM) != null ||
                request.getParameter(MARK_AS_INCOMPLETE) != null ||
                request.getParameter(ADD_COST) != null ||
                request.getParameter(REMOVE_COST) != null ||
                request.getParameter(MARK_AS_COMPLETE) != null ||
                request.getParameter(REMOVE_UPLOADED_FILE) != null ||
                request.getParameter(UPLOAD_FILE) != null ||
                request.getParameter(EDIT_QUESTION) != null) {
            // user did a action, just display the same page.
            log.info("redirect: " + request.getRequestURI());
            return "redirect:" + request.getRequestURI();
        } else {
            // add redirect, to make sure the user cannot resubmit the form by refreshing the page.
            log.info("default redirect: ");
            return "redirect:/application/" + applicationId;
        }
    }

    private void addNavigation(SectionResource section, Long applicationId, Model model) {
        if (section == null) {
            return;
        }
        Question previousQuestion = questionService.getPreviousQuestionBySection(section.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, model);
        Question nextQuestion = questionService.getNextQuestionBySection(section.getId());
        addNextQuestionToModel(nextQuestion, applicationId, model);
    }

    private void addNavigation(Question question, Long applicationId, Model model) {
        if (question == null) {
            return;
        }
        Question previousQuestion = questionService.getPreviousQuestion(question.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, model);
        Question nextQuestion = questionService.getNextQuestion(question.getId());
        addNextQuestionToModel(nextQuestion, applicationId, model);
    }

    private void addPreviousQuestionToModel(Question previousQuestion, Long applicationId, Model model) {
        String previousUrl;
        String previousText;

        if (previousQuestion != null) {
            SectionResource previousSection = sectionService.getSectionByQuestionId(previousQuestion.getId());
            if (previousSection.isQuestionGroup()) {
                previousUrl = "/application/" + applicationId + "/form/section/" + previousSection.getId();
                previousText = previousSection.getName();
            } else {
                previousUrl = "/application/" + applicationId + "/form/question/" + previousQuestion.getId();
                previousText = previousQuestion.getShortName();
            }
            model.addAttribute("previousUrl", previousUrl);
            model.addAttribute("previousText", previousText);
        }
    }

    private void addNextQuestionToModel(Question nextQuestion, Long applicationId, Model model) {
        String nextUrl;
        String nextText;

        if (nextQuestion != null) {
            SectionResource nextSection = sectionService.getSectionByQuestionId(nextQuestion.getId());

            if (nextSection.isQuestionGroup()) {
                nextUrl = "/application/" + applicationId + "/form/section/" + nextSection.getId();
                nextText = nextSection.getName();
            } else {
                nextUrl = "/application/" + applicationId + "/form/question/" + nextQuestion.getId();
                nextText = nextQuestion.getShortName();
            }

            model.addAttribute("nextUrl", nextUrl);
            model.addAttribute("nextText", nextText);
        }
    }

    @RequestMapping(value = "/add_cost/{questionId}")
    public String addCostRow(@ModelAttribute("form") ApplicationForm form, Model model,
                             @PathVariable("applicationId") final Long applicationId,
                             @PathVariable("questionId") final Long questionId,
                             HttpServletRequest request) {
        CostItem costItem = addCost(applicationId, questionId, request);
        String type = costItem.getCostType().getType();
        User user = userAuthenticationService.getAuthenticatedUser(request);

        Set<Long> markedAsComplete = new TreeSet<>();
        model.addAttribute("markedAsComplete", markedAsComplete);
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        organisationService.getUserOrganisation(applicationResource, user.getId());
        String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
        financeHandler.getFinanceModelManager(organisationType).addCost(model, costItem, applicationId, user.getId(), questionId, type);
        return String.format("finance/finance :: %s_row", type);
    }

    @RequestMapping(value = "/remove_cost/{costId}")
    public @ResponseBody String removeCostRow(@ModelAttribute("form") ApplicationForm form,
                                              @PathVariable("costId") final Long costId) throws JsonProcessingException {
        costService.delete(costId);
        AjaxResult ajaxResult = new AjaxResult(HttpStatus.OK, "true");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ajaxResult);
    }

    private CostItem addCost(Long applicationId, Long questionId, HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
        return financeHandler.getFinanceFormHandler(organisationType).addCost(applicationId, user.getId(), questionId);
    }

    private BindingResult saveApplicationForm(ApplicationResource application,
                                              CompetitionResource competition,
                                              ApplicationForm form,
                                              Long applicationId, Long sectionId, Question question,
                                              HttpServletRequest request,
                                              HttpServletResponse response,
                                              BindingResult bindingResult) throws Exception {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRole processRole = processRoleService.findProcessRole(user.getId(), applicationId);

        Map<Long, List<String>> errors;
        if(question != null) {
            errors = saveQuestionResponses(application, Collections.singletonList(question), user.getId(), processRole.getId(), bindingResult, request, response);
        } else {
            SectionResource selectedSection = getSelectedSection(competition.getSections(), sectionId);
            List<Question> questions = simpleMap(selectedSection.getQuestions(), questionService::getById);
            errors = saveQuestionResponses(application, questions, user.getId(), processRole.getId(), bindingResult, request, response);
        }

        Map<String, String[]> params = request.getParameterMap();
        params.forEach((key, value) -> log.debug(String.format("saveApplicationForm key %s   => value %s", key, value[0])));

        setApplicationDetails(application, form.getApplication());
        applicationService.save(application);
        markApplicationQuestions(application, processRole.getId(), request, response, errors);

        String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
        financeHandler.getFinanceFormHandler(organisationType).update(request, user.getId(), applicationId);
        cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");

        return bindingResult;
    }

    private SectionResource getSelectedSection(List<Long> sectionIds, Long sectionId) {
        return sectionIds.stream()
                .map(sectionService::getById)
                .filter(x -> x.getId().equals(sectionId))
                .findFirst()
                .get();
    }

    private Map<Long, List<String>> saveQuestionResponses(ApplicationResource application, List<Question> questions, Long userId, Long processRoleId, BindingResult bindingResult,  HttpServletRequest request, HttpServletResponse response) {
        Map<Long, List<String>> errors = saveQuestionResponses(request, questions, userId, processRoleId, application.getId());
        errors.forEach((k, errorsList) -> errorsList.forEach(e -> bindingResult.rejectValue("formInput[" + k + "]", e, e)));
        return errors;
    }

    private void markApplicationQuestions(ApplicationResource application, Long processRoleId, HttpServletRequest request, HttpServletResponse response, Map<Long, List<String>> errors) {
        // if a question is marked as complete, don't show the field saved message.
        Map<String, String[]> params = request.getParameterMap();

        boolean marked = markQuestion(request, params, application.getId(), processRoleId, errors);

        if (!marked) {
            cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");
        }
    }

    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button or reassigns a question to another user.
     */
    @ProfileExecution
    @RequestMapping(value = "/section/{sectionId}", method = RequestMethod.POST)
    public String applicationFormSubmit(@Valid @ModelAttribute("form") ApplicationForm form,
                                        BindingResult bindingResult,
                                        Model model,
                                        @PathVariable("applicationId") final Long applicationId,
                                        @PathVariable("sectionId") final Long sectionId,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        Map<String, String[]> params = request.getParameterMap();

        bindingResult.getAllErrors().forEach((e) -> log.info("Validations on application : " + e.getObjectName() + " v: " + e.getDefaultMessage()));
        bindingResult = saveApplicationForm(application, competition, form, applicationId, sectionId, null, request, response, bindingResult);
        bindingResult.getAllErrors().forEach((e) -> log.info("Remote validation: " + e.getObjectName() + " v: " + e.getDefaultMessage()));

        if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
            assignQuestion(applicationId, request);
            cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
        }

        form.bindingResult = bindingResult;
        form.objectErrors = bindingResult.getAllErrors();

        if(bindingResult.hasErrors()){
            addApplicationAndSections(application, competition, user.getId(), Optional.empty(), Optional.empty(), model, form);
            addOrganisationAndUserFinanceDetails(application.getId(), user.getId(), model, form);
            return "application-form";
        } else {
            return getRedirectUrl(request, applicationId);
        }
    }

    private boolean markQuestion(HttpServletRequest request, Map<String, String[]> params, Long applicationId, Long processRoleId, Map<Long, List<String>> errors) {
        if (processRoleId == null) {
            return false;
        }
        boolean success = false;
        if (params.containsKey(MARK_AS_COMPLETE)) {
            Long questionId = Long.valueOf(request.getParameter(MARK_AS_COMPLETE));

            if (errors.containsKey(questionId) && !errors.get(questionId).isEmpty()) {
                List<String> fieldErrors = errors.get(questionId);
                fieldErrors.add("Please enter valid data before marking a question as complete.");
            } else {
                questionService.markAsComplete(questionId, applicationId, processRoleId);
                success = true;
            }
        }
        if (params.containsKey(MARK_AS_INCOMPLETE)) {
            Long questionId = Long.valueOf(request.getParameter(MARK_AS_INCOMPLETE));
            questionService.markAsInComplete(questionId, applicationId, processRoleId);
            success = true;

        }
        return success;
    }

    private Map<Long, List<String>> saveQuestionResponses(HttpServletRequest request,
                                                          List<Question> questions,
                                                          Long userId,
                                                          Long processRoleId,
                                                          Long applicationId) {
        final Map<String, String[]> params = request.getParameterMap();

        Map<Long, List<String>> errorMap = new HashMap<>();

        errorMap.putAll(saveNonFileUploadQuestions(questions, params, request, userId, applicationId));

        errorMap.putAll(saveFileUploadQuestionsIfAny(questions, params, request, applicationId, processRoleId));

        return errorMap;
    }

    private Map<Long, List<String>> saveNonFileUploadQuestions(List<Question> questions,
                                                               Map<String, String[]> params,
                                                               HttpServletRequest request,
                                                               Long userId,
                                                               Long applicationId){
        Map<Long, List<String>> errorMap = new HashMap<>();
        questions.stream()
                .forEach(question -> question.getFormInputs()
                                .stream()
                                .filter(formInput1 -> (!formInput1.getFormInputType().getTitle().equals("fileupload")))
                                .forEach(formInput -> {
                                            if (params.containsKey("formInput[" + formInput.getId() + "]")) {
                                                String value = request.getParameter("formInput[" + formInput.getId() + "]");
                                                List<String> errors = formInputResponseService.save(userId, applicationId, formInput.getId(), value);
                                                if (errors.size() != 0) {
                                                    log.error("save failed. " + question.getId());
                                                    errorMap.put(question.getId(), new ArrayList<>(errors));
                                                }
                                            }
                                        }
                                )
                );
        return errorMap;
    }

    private Map<Long, List<String>> saveFileUploadQuestionsIfAny(List<Question> questions,
                                                                 final Map<String, String[]> params,
                                                                 HttpServletRequest request,
                                                                 Long applicationId,
                                                                 Long processRoleId){
        Map<Long, List<String>> errorMap = new HashMap<>();
        questions.stream()
                .forEach(question -> question.getFormInputs()
                        .stream()
                        .filter(formInput1 -> (formInput1.getFormInputType().getTitle().equals("fileupload") && request instanceof StandardMultipartHttpServletRequest))
                        .forEach(formInput -> {
                            if (params.containsKey(REMOVE_UPLOADED_FILE)) {
                                formInputResponseService.removeFile(formInput.getId(), applicationId, processRoleId).getSuccessObjectOrThrowException();
                            } else {
                                final Map<String, MultipartFile> fileMap = ((StandardMultipartHttpServletRequest) request).getFileMap();
                                final MultipartFile file = fileMap.get("formInput[" + formInput.getId() + "]");
                                if (file != null && !file.isEmpty()) {
                                    try {
                                        RestResult<FileEntryResource> result = formInputResponseService.createFile(formInput.getId(),
                                                applicationId,
                                                processRoleId,
                                                file.getContentType(),
                                                file.getSize(),
                                                file.getOriginalFilename(),
                                                file.getBytes());
                                        if (result.isFailure()) {
                                            errorMap.put(formInput.getId(),
                                                    result.getFailure().getErrors().stream()
                                                            .map(e -> MessageUtil.getFromMessageBundle(messageSource, e.getErrorKey(), "Unknown error on file upload", request.getLocale())).collect(Collectors.toList()));
                                        }
                                    } catch (IOException e) {
                                        throw new UnableToReadUploadedFile();
                                    }
                                }
                            }
                        }));
        return errorMap;
    }

    /**
     * Set the submitted values, if not null. If they are null, then probably the form field was not in the current html form.
     */
    private void setApplicationDetails(ApplicationResource application, ApplicationResource updatedApplication) {
        if (updatedApplication == null) {
            return;
        }

        if (updatedApplication.getName() != null) {
            log.debug("setApplicationDetails: " + updatedApplication.getName());
            application.setName(updatedApplication.getName());
        }
        if (updatedApplication.getStartDate() != null) {
            log.debug("setApplicationDetails: " + updatedApplication.getStartDate());
            application.setStartDate(updatedApplication.getStartDate());
        }
        if (updatedApplication.getDurationInMonths() != null) {
            log.debug("setApplicationDetails: " + updatedApplication.getDurationInMonths());
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
                                                  @PathVariable("applicationId") Long applicationId,
                                                  HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        try {
            String fieldName = request.getParameter("fieldName");
            log.info(String.format("saveFormElement: %s / %s", fieldName, value));

            User user = userAuthenticationService.getAuthenticatedUser(request);
            errors = storeField(applicationId, user.getId(), fieldName, inputIdentifier, value);

            if (!errors.isEmpty()) {
                return this.createJsonObjectNode(false, errors);
            } else {
                return this.createJsonObjectNode(true, null);
            }
        } catch (Exception e) {
            AutosaveElementException ex = new AutosaveElementException(inputIdentifier, value, applicationId, e);
            errors.add(ex.getErrorMessage());
            return this.createJsonObjectNode(false, errors);
        }
    }

    private List<String> storeField(Long applicationId, Long userId, String fieldName, String inputIdentifier, String value) throws Exception {
        List<String> errors = new ArrayList<>();
        String organisationType = organisationService.getOrganisationType(userId, applicationId);

        if (fieldName.startsWith("application.")) {
            errors = this.saveApplicationDetails(applicationId, fieldName, value, errors);
        } else if (inputIdentifier.startsWith("financePosition-") || fieldName.startsWith("financePosition-")) {
            financeHandler.getFinanceFormHandler(organisationType).updateFinancePosition(userId, applicationId, fieldName, value);
        } else if (inputIdentifier.startsWith("cost-") || fieldName.startsWith("cost-")) {
            financeHandler.getFinanceFormHandler(organisationType).storeCost(userId, applicationId, fieldName, value);
        } else {
            Long formInputId = Long.valueOf(inputIdentifier);
            errors = formInputResponseService.save(userId, applicationId, formInputId, value);
        }
        return errors;
    }

    private ObjectNode createJsonObjectNode(boolean success, List<String> errors) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", (success ? "true" : "false"));
        if (!success) {
            ArrayNode errorsNode = mapper.createArrayNode();
            errors.stream().forEach(errorsNode::add);
            node.set("validation_errors", errorsNode);
        }
        return node;
    }

    private List<String> saveApplicationDetails(Long applicationId, String fieldName, String value, List<String> errors) throws Exception {
        ApplicationResource application = applicationService.getById(applicationId);

        if ("application.name".equals(fieldName)) {
            String trimmedValue = value.trim();
            if (StringUtils.isEmpty(trimmedValue)) {
                errors.add("Please enter the full title of the project.");
            } else {

                application.setName(trimmedValue);
                applicationService.save(application);
            }
        } else if (fieldName.startsWith("application.durationInMonths")) {
            Long durationInMonth = Long.valueOf(value);
            if (durationInMonth == null || durationInMonth < 1L) {
                errors.add("Please enter a valid duration.");
            } else {
                application.setDurationInMonths(durationInMonth);
                applicationService.save(application);
            }
        } else if (fieldName.startsWith("application.startDate")) {
            errors = this.saveApplicationStartDate(application, fieldName, value, errors);

        }
        return errors;
    }

    private List<String> saveApplicationStartDate(ApplicationResource application, String fieldName, String value, List<String> errors) {
        LocalDate startDate = application.getStartDate();

        if (startDate == null) {
            startDate = LocalDate.now();
        }
        try {
            if (fieldName.endsWith(".dayOfMonth")) {
                startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), Integer.parseInt(value));
            } else if (fieldName.endsWith(".monthValue")) {
                startDate = LocalDate.of(startDate.getYear(), Integer.parseInt(value), startDate.getDayOfMonth());
            } else if (fieldName.endsWith(".year")) {
                startDate = LocalDate.of(Integer.parseInt(value), startDate.getMonth(), startDate.getDayOfMonth());
            }
            if (startDate.isBefore(LocalDate.now())) {
                errors.add("Please enter a future date.");
            }

            application.setStartDate(startDate);
            applicationService.save(application);
        } catch (DateTimeException | NumberFormatException e) {
            log.error(e);
            errors.add("Please enter a valid date.");
        }
        return errors;
    }

    public void assignQuestion(@PathVariable("applicationId") final Long applicationId,
                               HttpServletRequest request) {
        assignQuestion(request, applicationId);
    }
}
