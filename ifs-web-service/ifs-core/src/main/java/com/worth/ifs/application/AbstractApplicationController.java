package com.worth.ifs.application;

import com.worth.ifs.BaseController;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.finance.view.FinanceHandler;
import com.worth.ifs.application.finance.view.FinanceOverviewModelManager;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.service.*;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.user.domain.OrganisationTypeEnum;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.application.service.Futures.call;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * This object contains shared methods for all the Controllers related to the {@link ApplicationResource} data.
 */
public abstract class AbstractApplicationController extends BaseController {
    public static final String MARK_AS_COMPLETE = "mark_as_complete";
    public static final String MARK_SECTION_AS_COMPLETE = "mark_section_as_complete";
    public static final String MARK_SECTION_AS_INCOMPLETE = "mark_section_as_incomplete";
    public static final String MARK_AS_INCOMPLETE = "mark_as_incomplete";
    public static final String UPLOAD_FILE = "upload_file";
    public static final String REMOVE_UPLOADED_FILE = "remove_uploaded_file";
    public static final String ADD_COST = "add_cost";
    public static final String REMOVE_COST = "remove_cost";
    public static final String EDIT_QUESTION = "edit_question";
    public static final String APPLICATION_FORM = "application-form";
    public static final String MODEL_ATTRIBUTE_FORM = "form";
    public static final String QUESTION_ID = "questionId";
    public static final String APPLICATION_ID = "applicationId";
    public static final String APPLICATION_BASE_URL = "/application/";

    public static final String ASSIGN_QUESTION_PARAM = "assign_question";
    public static final String FORM_MODEL_ATTRIBUTE = "form";
    public static final String APPLICATION_START_DATE = "application.startDate";
    public static final String QUESTION_URL = "/question/";
    public static final String SECTION_URL = "/section/";
    private static final Log LOG = LogFactory.getLog(AbstractApplicationController.class);

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected ResponseService responseService;

    @Autowired
    protected FormInputResponseService formInputResponseService;

    @Autowired
    protected QuestionService questionService;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected SectionService sectionService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ProcessRoleService processRoleService;

    @Autowired
    protected UserAuthenticationService userAuthenticationService;

    @Autowired
    protected OrganisationService organisationService;

    @Autowired
    protected OrganisationRestService organisationRestService;

    @Autowired
    protected CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    protected CompetitionService competitionService;

    @Autowired
    protected InviteRestService inviteRestService;
    
    @Autowired
    protected FinanceOverviewModelManager financeOverviewModelManager;

    @Autowired
    protected FinanceHandler financeHandler;

    protected Long extractAssigneeProcessRoleIdFromAssignSubmit(HttpServletRequest request) {
        Long assigneeId = null;
        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey(ASSIGN_QUESTION_PARAM)){
            String assign = request.getParameter(ASSIGN_QUESTION_PARAM);
            assigneeId = Long.valueOf(assign.split("_")[1]);
        }

        return assigneeId;
    }

    protected Long extractQuestionProcessRoleIdFromAssignSubmit(HttpServletRequest request) {
        Long questionId = null;
        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey(ASSIGN_QUESTION_PARAM)){
            String assign = request.getParameter(ASSIGN_QUESTION_PARAM);
            questionId = Long.valueOf(assign.split("_")[0]);
        }

        return questionId;
    }

    protected void assignQuestion(HttpServletRequest request, Long applicationId) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRole assignedBy = processRoleService.findProcessRole(user.getId(), applicationId);

        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey(ASSIGN_QUESTION_PARAM)){
            Long questionId = extractQuestionProcessRoleIdFromAssignSubmit(request);
            Long assigneeId = extractAssigneeProcessRoleIdFromAssignSubmit(request);

            questionService.assign(questionId, applicationId, assigneeId, assignedBy.getId());
        }
    }

    /**
     * Get the details of the current application, add this to the model so we can use it in the templates.
     */
    protected ApplicationResource addApplicationDetails(ApplicationResource application,
                                                        CompetitionResource competition,
                                                        Long userId,
                                                        Optional<SectionResource> section,
                                                        Optional<Long> currentQuestionId,
                                                        Model model,
                                                        ApplicationForm form,
                                                        List<ProcessRole> userApplicationRoles) {
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);

        Optional<OrganisationResource> userOrganisation = getUserOrganisation(userId, userApplicationRoles);

        if(form == null){
            form = new ApplicationForm();
        }
        form.setApplication(application);

        addOrganisationDetails(model, application, userOrganisation, userApplicationRoles);
        addQuestionsDetails(model, application, form);
        addUserDetails(model, application, userId);
        addApplicationFormDetailInputs(application, form);
        addMappedSectionsDetails(model, application, competition, section, userOrganisation);

        addAssignableDetails(model, application, userOrganisation.orElse(null), userId, section, currentQuestionId);
        addCompletedDetails(model, application, userOrganisation, userApplicationRoles);

        model.addAttribute(FORM_MODEL_ATTRIBUTE, form);
        return application;
    }

    protected  void addApplicationFormDetailInputs(ApplicationResource application, Form form) {
        Map<String, String> formInputs = form.getFormInput();
        formInputs.put("application_details-title", application.getName());
        formInputs.put("application_details-duration", String.valueOf(application.getDurationInMonths()));
        if(application.getStartDate() != null){
            formInputs.put("application_details-startdate_day", String.valueOf(application.getStartDate().getDayOfMonth()));
            formInputs.put("application_details-startdate_month", String.valueOf(application.getStartDate().getMonthValue()));
            formInputs.put("application_details-startdate_year", String.valueOf(application.getStartDate().getYear()));
        }
        form.setFormInput(formInputs);
    }

    protected  void addApplicationInputs(ApplicationResource application, Model model) {
        model.addAttribute("application_title", application.getName());
        model.addAttribute("application_duration", String.valueOf(application.getDurationInMonths()));
        if(application.getStartDate() != null){
            model.addAttribute("application_startdate_day", String.valueOf(application.getStartDate().getDayOfMonth()));
            model.addAttribute("application_startdate_month", String.valueOf(application.getStartDate().getMonthValue()));
            model.addAttribute("application_startdate_year", String.valueOf(application.getStartDate().getYear()));
        }
    }

    protected void addOrganisationDetails(Model model, ApplicationResource application, Optional<OrganisationResource> userOrganisation,
                                          List<ProcessRole> userApplicationRoles) {

        model.addAttribute("userOrganisation", userOrganisation.orElse(null));
        SortedSet<OrganisationResource> organisations = getApplicationOrganisations(userApplicationRoles);
        model.addAttribute("applicationOrganisations", organisations);
        model.addAttribute("academicOrganisations", getAcademicOrganisations(organisations));
        
        model.addAttribute("applicationOrganisations", organisations);
        
        List<String> activeApplicationOrganisationNames = organisations.stream().map(OrganisationResource::getName).collect(Collectors.toList());
        
        List<String> pendingOrganisationNames = pendingInvitations(application).stream()
        		.map(InviteResource::getInviteOrganisationName)
        		.distinct()
        		.filter(orgName -> StringUtils.hasText(orgName)
                        && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());

        model.addAttribute("pendingOrganisationNames", pendingOrganisationNames);
        
        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
                        model.addAttribute("leadOrganisation", org)
        );
    }

    protected void addQuestionsDetails(Model model, ApplicationResource application, Form form) {
        LOG.info("*********************");
        LOG.info(application.getId());
        List<FormInputResponseResource> responses = getFormInputResponses(application);
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);
        model.addAttribute("responses",mappedResponses);

        if(form == null){
            form = new Form();
        }
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
                        values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);
        model.addAttribute(FORM_MODEL_ATTRIBUTE, form);
    }

    protected List<Response> getResponses(ApplicationResource application) {
        return responseService.getByApplication(application.getId());
    }

    protected List<FormInputResponseResource> getFormInputResponses(ApplicationResource application) {
        return formInputResponseService.getByApplication(application.getId());
    }

    protected void addUserDetails(Model model, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        model.addAttribute("leadApplicant", userService.getLeadApplicantProcessRoleOrNull(application));
    }

    protected Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Long organisationId=0L;
        if(userOrganisation.isPresent()) {
            organisationId = userOrganisation.get().getId();
        }
        return questionService.getMarkedAsComplete(application.getId(), organisationId);
    }

    protected void addAssignableDetails(Model model, ApplicationResource application, OrganisationResource userOrganisation,
                                         Long userId, Optional<SectionResource> currentSection, Optional<Long> currentQuestionId) {

        if (isApplicationInViewMode(model, application, userOrganisation))
            return;

        Map<Long, QuestionStatusResource> questionAssignees;
        if(currentQuestionId.isPresent()){
            QuestionStatusResource questionStatusResource = questionService.getByQuestionIdAndApplicationIdAndOrganisationId(currentQuestionId.get(), application.getId(), userOrganisation.getId());
            questionAssignees = new HashMap<>();
            if(questionStatusResource != null) {
                questionAssignees.put(currentQuestionId.get(), questionStatusResource);
            }
        }else if(currentSection.isPresent()){
            SectionResource section = currentSection.get();
            questionAssignees = questionService.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(section.getQuestions(), application.getId(), userOrganisation.getId());
        }else{
            questionAssignees = questionService.getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.getId());
        }

        if(currentQuestionId.isPresent()) {
            QuestionStatusResource questionAssignee = questionAssignees.get(currentQuestionId.get());
            model.addAttribute("questionAssignee", questionAssignee);
        }

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);

        List<InviteResource> pendingAssignableUsers = pendingInvitations(application);
        
        model.addAttribute("assignableUsers", processRoleService.findAssignableProcessRoles(application.getId()));
        model.addAttribute("pendingAssignableUsers", pendingAssignableUsers);
        model.addAttribute("questionAssignees", questionAssignees);
        model.addAttribute("notifications", notifications);
    }

    private boolean isApplicationInViewMode(Model model, ApplicationResource application, OrganisationResource userOrganisation) {
        if(!application.isOpen() || userOrganisation == null){
            //Application Not open, so add empty lists
            model.addAttribute("assignableUsers", new ArrayList<ProcessRole>());
            model.addAttribute("pendingAssignableUsers", new ArrayList<InviteResource>());
            model.addAttribute("questionAssignees", new HashMap<Long, QuestionStatusResource>());
            model.addAttribute("notifications", new ArrayList<QuestionStatusResource>());
            return true;
        }
        return false;
    }

    private List<InviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
        		failure -> new ArrayList<>(0),
        		success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                    .filter(item -> !InviteStatusConstants.ACCEPTED.equals(item.getStatus()))
                    .collect(Collectors.toList()));
	}

    protected void addMappedSectionsDetails(Model model, ApplicationResource application, CompetitionResource competition,
                                            Optional<SectionResource> currentSection,
                                            Optional<OrganisationResource> userOrganisation) {
        List<SectionResource> sectionsList = sectionService.filterParentSections(competition.getSections());

        Map<Long, SectionResource> sections =
                sectionsList.stream().collect(Collectors.toMap(SectionResource::getId,
                        Function.identity()));

        userOrganisation.ifPresent(org -> model.addAttribute("completedSections", sectionService.getCompleted(application.getId(), org.getId())));

        model.addAttribute("sections", sections);
        Map<Long, List<Question>> sectionQuestions = sectionsList.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> simpleMap(s.getQuestions(), questionService::getById)
                ));
        model.addAttribute("sectionQuestions", sectionQuestions);

        if(currentSection.isPresent()){
            Map<Long, List<SectionResource>>  subSections = new HashMap<>();
            subSections.put(currentSection.get().getId(), currentSection.get().getChildSections().stream().map(sectionService::getById).collect(Collectors.toList()));

            model.addAttribute("subSections", subSections);
            Map<Long, List<Question>> subsectionQuestions = subSections.get(currentSection.get().getId()).stream()
                    .collect(Collectors.toMap(SectionResource::getId,
                            ss -> simpleMap(ss.getQuestions(), questionService::getById)
                    ));
            model.addAttribute("subsectionQuestions", subsectionQuestions);
        }else{
            Map<Long, List<SectionResource>>   subSections = sectionsList.stream()
                    .collect(Collectors.toMap(
                            SectionResource::getId, s -> simpleMap(s.getChildSections(), sectionService::getById)
                    ));
            model.addAttribute("subSections", subSections);
            Map<Long, List<Question>> subsectionQuestions = sectionsList.stream()
                    .collect(Collectors.toMap(SectionResource::getId,
                            ss -> simpleMap(ss.getQuestions(), questionService::getById)
                    ));
            model.addAttribute("subsectionQuestions", subsectionQuestions);
        }

    }

    private void addCompletedDetails(Model model, ApplicationResource application, Optional<OrganisationResource> userOrganisation, List<ProcessRole> userApplicationRoles) {
        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        model.addAttribute("markedAsComplete", markedAsComplete);

        SortedSet<OrganisationResource> organisations = getApplicationOrganisations(userApplicationRoles);
        Set<Long> questionsCompletedByAllOrganisation = new TreeSet<>(call(getMarkedAsCompleteDetails(application, Optional.ofNullable(organisations.first()))));
        // only keep the questionIDs of questions that are complete by all organisations
        organisations.forEach(o -> questionsCompletedByAllOrganisation.retainAll(call(getMarkedAsCompleteDetails(application, Optional.ofNullable(o)))));
        model.addAttribute("questionsCompletedByAllOrganisation", questionsCompletedByAllOrganisation);

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = new TreeSet<>(completedSectionsByOrganisation.get(completedSectionsByOrganisation.keySet().stream().findFirst().get()));
        completedSectionsByOrganisation.forEach((key, values) -> sectionsMarkedAsComplete.retainAll(values));

        model.addAttribute("completedSectionsByOrganisation", completedSectionsByOrganisation);
        model.addAttribute("sectionsMarkedAsComplete", sectionsMarkedAsComplete);
        model.addAttribute("allQuestionsCompleted", sectionService.allSectionsMarkedAsComplete(application.getId()));
        
        SectionResource financeSection = sectionService.getFinanceSectionForCompetition(application.getCompetition());
        boolean hasFinanceSection = financeSection != null;
        Long financeSectionId;
        if(hasFinanceSection) {
        	financeSectionId = financeSection.getId();
        } else {
        	financeSectionId = null;
        }
        
        model.addAttribute("hasFinanceSection", hasFinanceSection);
        model.addAttribute("financeSectionId", financeSectionId);
    }

    protected void addSectionDetails(Model model, Optional<SectionResource> currentSection) {
        model.addAttribute("currentSectionId", currentSection.map(SectionResource::getId).orElse(null));
        model.addAttribute("currentSection", currentSection.orElse(null));
        if(currentSection.isPresent()) {
            List<Question> questions = simpleMap(currentSection.get().getQuestions(), questionService::getById);
            Map<Long, List<Question>> sectionQuestions = new HashMap<>();
            sectionQuestions.put(currentSection.get().getId(), questions);

            model.addAttribute("sectionQuestions", sectionQuestions);
            model.addAttribute("title", currentSection.get().getName());
        }
    }

    protected Optional<SectionResource> getSectionByIds(List<Long> sections, Optional<Long> sectionId, boolean selectFirstSectionIfNoneCurrentlySelected) {
        List<SectionResource> sectionObjects = sections.stream().map(sectionService::getById).collect(Collectors.toList());
        return getSection(sectionObjects, sectionId, selectFirstSectionIfNoneCurrentlySelected);
    }

    protected Optional<SectionResource> getSection(List<SectionResource> sections, Optional<Long> sectionId, boolean selectFirstSectionIfNoneCurrentlySelected) {

        if (sectionId.isPresent()) {
            Long id = sectionId.get();

            // get the section that we want to show, so we can use this on to show the correct questions.
            return sections.stream().filter(x -> x.getId().equals(id)).findFirst();

        } else if (selectFirstSectionIfNoneCurrentlySelected) {
            return sections.isEmpty() ? Optional.empty() : Optional.ofNullable(sections.get(0));
        }

        return Optional.empty();
    }

    protected ApplicationResource addApplicationAndSections(ApplicationResource application,
                                                                             CompetitionResource competition,
                                                                             Long userId,
                                                                             Optional<SectionResource> section,
                                                                             Optional<Long> currentQuestionId,
                                                                             Model model,
                                                                             ApplicationForm form) {

        List<ProcessRole> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        application = addApplicationDetails(application, competition, userId, section, currentQuestionId, model, form, userApplicationRoles);
        
        model.addAttribute("completedQuestionsPercentage", applicationService.getCompleteQuestionsPercentage(application.getId()));
        addSectionDetails(model, section);

        return application;
    }

    protected void addOrganisationAndUserFinanceDetails(Long competitionId, Long applicationId, UserResource user,
                                                        Model model, ApplicationForm form) {
        model.addAttribute("currentUser", user);
        
        SectionResource financeSection = sectionService.getFinanceSectionForCompetition(competitionId);
        boolean hasFinanceSection = financeSection != null;
        
        if(hasFinanceSection) {
        
	        financeOverviewModelManager.addFinanceDetails(model, competitionId, applicationId);
	        if(!form.isAdminMode()){
	            String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
	            financeHandler.getFinanceModelManager(organisationType).addOrganisationFinanceDetails(model, applicationId, user.getId(), form);
	        } else if(form.getImpersonateOrganisationId() != null){
                // find user in the organisation we want to impersonate.
                String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
                financeHandler.getFinanceModelManager(organisationType).addOrganisationFinanceDetails(model, applicationId, user.getId(), form);
            }
        }
    }

    public SortedSet<OrganisationResource> getApplicationOrganisations(List<ProcessRole> userApplicationRoles) {
        Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        // TODO DW - INFUND-1604 - remove organisation rest service call when ProcessRoles converted to DTOs
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName())
                            || uar.getRole().getName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
                .map(ProcessRole::getOrganisation)
                .map(organisation -> organisationRestService.getOrganisationById(organisation.getId()).getSuccessObjectOrThrowException())
                .collect(Collectors.toCollection(supplier));
    }

    public SortedSet<OrganisationResource> getAcademicOrganisations(SortedSet<OrganisationResource> organisations) {
        Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        Supplier<TreeSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);
        ArrayList<OrganisationResource> organisationList = new ArrayList<>(organisations);

        return organisationList.stream()
                .filter(o -> OrganisationTypeEnum.ACADEMIC.getOrganisationTypeId().equals(o.getOrganisationType()))
                .collect(Collectors.toCollection(supplier));
    }

    public Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRole> userApplicationRoles) {

        // TODO DW - INFUND-1604 - remove organisation rest service call when ProcessRoles converted to DTOs
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(ProcessRole::getOrganisation)
                .map(organisation -> organisationRestService.getOrganisationById(organisation.getId()).getSuccessObjectOrThrowException())
                .findFirst();
    }

    public Optional<OrganisationResource> getUserOrganisation(Long userId, List<ProcessRole> userApplicationRoles) {

        // TODO DW - INFUND-1604 - remove organisation rest service call when ProcessRoles converted to DTOs
        return userApplicationRoles.stream()
                .filter(uar -> uar.getUser().getId().equals(userId))
                .map(ProcessRole::getOrganisation)
                .map(organisation -> organisationRestService.getOrganisationById(organisation.getId()).getSuccessObjectOrThrowException())
                .findFirst();
    }

    public UserAuthenticationService getUserAuthenticationService() {
        return userAuthenticationService;
    }

    protected void addNavigation(SectionResource section, Long applicationId, Model model) {
        if (section == null) {
            return;
        }
        Optional<Question> previousQuestion = questionService.getPreviousQuestionBySection(section.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, model);
        Optional<Question> nextQuestion = questionService.getNextQuestionBySection(section.getId());
        addNextQuestionToModel(nextQuestion, applicationId, model);
    }

    protected void addNavigation(Question question, Long applicationId, Model model) {
        if (question == null) {
            return;
        }

        Optional<Question> previousQuestion = questionService.getPreviousQuestion(question.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, model);
        Optional<Question> nextQuestion = questionService.getNextQuestion(question.getId());
        addNextQuestionToModel(nextQuestion, applicationId, model);
    }

    protected void addPreviousQuestionToModel(Optional<Question> previousQuestionOptional, Long applicationId, Model model) {
        String previousUrl;
        String previousText;

        if (previousQuestionOptional.isPresent()) {
            Question previousQuestion = previousQuestionOptional.get();
            SectionResource previousSection = sectionService.getSectionByQuestionId(previousQuestion.getId());
            if (previousSection.isQuestionGroup()) {
                previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + previousSection.getId();
                previousText = previousSection.getName();
            } else {
                previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + previousQuestion.getId();
                previousText = previousQuestion.getShortName();
            }
            model.addAttribute("previousUrl", previousUrl);
            model.addAttribute("previousText", previousText);
        }
    }

    protected void addNextQuestionToModel(Optional<Question> nextQuestionOptional, Long applicationId, Model model) {
        String nextUrl;
        String nextText;

        if (nextQuestionOptional.isPresent()) {
            Question nextQuestion = nextQuestionOptional.get();
            SectionResource nextSection = sectionService.getSectionByQuestionId(nextQuestion.getId());

            if (nextSection.isQuestionGroup()) {
                nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + nextSection.getId();
                nextText = nextSection.getName();
            } else {
                nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + nextQuestion.getId();
                nextText = nextQuestion.getShortName();
            }

            model.addAttribute("nextUrl", nextUrl);
            model.addAttribute("nextText", nextText);
        }
    }
}
