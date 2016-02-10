package com.worth.ifs.application;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.model.UserApplicationRole;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.service.*;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.security.CookieFlashMessageFilter;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.application.service.Futures.call;

/**
 * This object contains shared methods for all the Controllers related to the {@link ApplicationResource} data.
 */
public abstract class AbstractApplicationController {
    public static final String ASSIGN_QUESTION_PARAM = "assign_question";
    public static final String FORM_MODEL_ATTRIBUTE = "form";
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    protected ApplicationFinanceRestService applicationFinanceRestService;

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
    protected CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    protected FinanceService financeService;

    @Autowired
    protected CompetitionService competitionService;

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
        User user = userAuthenticationService.getAuthenticatedUser(request);
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
                                                        Competition competition,
                                                        Long userId,
                                                        Optional<Section> section,
                                                        Optional<Long> currentQuestionId,
                                                        Model model,
                                                        ApplicationForm form,
                                                        List<ProcessRole> userApplicationRoles) {
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);

        Optional<Organisation> userOrganisation = getUserOrganisation(userId, userApplicationRoles);

        if(form == null){
            form = new ApplicationForm();
        }
        form.application = application;

        addOrganisationDetails(model, application, userOrganisation, userApplicationRoles);
        addQuestionsDetails(model, application, form);
        addUserDetails(model, application, userId);
        addApplicationFormDetailInputs(application, form);

        userOrganisation.ifPresent(org ->
            addAssigneableDetails(model, application, org, userId, section, currentQuestionId)
        );
        addMappedSectionsDetails(model, application, competition, section, userOrganisation, userApplicationRoles);
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

    protected void addOrganisationDetails(Model model, ApplicationResource application, Optional<Organisation> userOrganisation,
                                          List<ProcessRole> userApplicationRoles) {


        model.addAttribute("userOrganisation", userOrganisation.orElse(null));
        model.addAttribute("applicationOrganisations", getApplicationOrganisations(userApplicationRoles));

        Optional<Organisation> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
                        model.addAttribute("leadOrganisation", org)
        );
    }

    protected void addQuestionsDetails(Model model, ApplicationResource application, Form form) {
        log.info("*********************");
        log.info(application.getId());
        List<FormInputResponse> responses = getFormInputResponses(application);
        Map<Long, FormInputResponse> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);
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

    protected List<FormInputResponse> getFormInputResponses(ApplicationResource application) {
        return formInputResponseService.getByApplication(application.getId());
    }

    protected void addUserDetails(Model model, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        model.addAttribute("leadApplicant", userService.getLeadApplicantProcessRoleOrNull(application));
    }

    protected Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application, Optional<Organisation> userOrganisation) {
        Long organisationId=0L;
        if(userOrganisation.isPresent()) {
            organisationId = userOrganisation.get().getId();
        }
        return questionService.getMarkedAsComplete(application.getId(), organisationId);
    }

    protected void addAssigneableDetails(Model model, ApplicationResource application, Organisation userOrganisation,
                                         Long userId, Optional<Section> currentSection, Optional<Long> currentQuestionId) {
        Map<Long, QuestionStatusResource> questionAssignees;
        if(currentQuestionId.isPresent()){
            QuestionStatusResource questionStatusResource = questionService.getByQuestionIdAndApplicationIdAndOrganisationId(currentQuestionId.get(), application.getId(), userOrganisation.getId());
            questionAssignees = new HashMap<>();
            if(questionStatusResource != null) {
                questionAssignees.put(currentQuestionId.get(), questionStatusResource);
            }
        }else if(currentSection.isPresent()){
            Section section = currentSection.get();
            questionAssignees = questionService.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(getQuestionIds(section.getQuestions()), application.getId(), userOrganisation.getId());
        }else{
            questionAssignees = questionService.getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.getId());
        }

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);

        model.addAttribute("assignableUsers", processRoleService.findAssignableProcessRoles(application.getId()));
        model.addAttribute("questionAssignees", questionAssignees);
        model.addAttribute("notifications", notifications);
    }

    private List<Long> getQuestionIds(final List<Question> questions){
        return questions.stream().map(Question::getId).collect(Collectors.toList());
    }

    protected void addOrganisationFinanceDetails(Model model, ApplicationResource application, Long userId, Form form) {
        ApplicationFinanceResource applicationFinanceResource = getOrganisationFinances(application.getId(), userId);
        Organisation organisation = organisationService.getOrganisationById(applicationFinanceResource.getOrganisation());
        model.addAttribute("organisationFinance", applicationFinanceResource.getFinanceOrganisationDetails());
        model.addAttribute("organisationFinanceSize", applicationFinanceResource.getOrganisationSize());
        model.addAttribute("organisationType", organisation.getOrganisationType());
        model.addAttribute("organisationFinanceId", applicationFinanceResource.getId());
        model.addAttribute("organisationFinanceTotal", applicationFinanceResource.getTotal());
        if(applicationFinanceResource.getGrantClaim()!=null) {
            model.addAttribute("organisationGrantClaimPercentage", applicationFinanceResource.getGrantClaim().getGrantClaimPercentage());
            model.addAttribute("organisationgrantClaimPercentageId", applicationFinanceResource.getGrantClaim().getId());
            String formInputKey = "finance-grantclaim-" + applicationFinanceResource.getGrantClaim();
            String formInputValue = applicationFinanceResource.getGrantClaimPercentage() != null ? applicationFinanceResource.getGrantClaimPercentage().toString() : "";
            form.addFormInput(formInputKey, formInputValue);
        }
    }

    protected void addFinanceDetails(Model model, ApplicationResource application) {
        Section section = sectionService.getByName("Your finances");
        sectionService.removeSectionsQuestionsWithType(section, "empty");
        model.addAttribute("financeSection", section);

        OrganisationFinanceOverview organisationFinanceOverview = new OrganisationFinanceOverview(financeService, application.getId());
        model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
        model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType());
        model.addAttribute("organisationFinances", organisationFinanceOverview.getApplicationFinances());
        model.addAttribute("totalFundingSought", organisationFinanceOverview.getTotalFundingSought());
        model.addAttribute("totalContribution", organisationFinanceOverview.getTotalContribution());
        model.addAttribute("totalOtherFunding", organisationFinanceOverview.getTotalOtherFunding());

        model.addAttribute("researchParticipationPercentage", applicationFinanceRestService.getResearchParticipationPercentage(application.getId()));
    }

    protected void addMappedSectionsDetails(Model model, ApplicationResource application, Competition competition,
                                            Optional<Section> currentSection,
                                            Optional<Organisation> userOrganisation,
                                            List<ProcessRole> userApplicationRoles) {
        List<Section> sectionsList = sectionService.getParentSections(competition.getSections());
        Future<Section> previousSection = sectionService.getPreviousSection(currentSection);
        Future<Section> nextSection = sectionService.getNextSection(currentSection);

        Map<Long, Section> sections =
                sectionsList.stream().collect(Collectors.toMap(Section::getId,
                        Function.identity()));

        userOrganisation.ifPresent(org -> model.addAttribute("completedSections", sectionService.getCompleted(application.getId(), org.getId())));

        model.addAttribute("previousSection", previousSection);
        model.addAttribute("nextSection", nextSection);
        model.addAttribute("sections", sections);


        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        model.addAttribute("markedAsComplete", markedAsComplete);

        TreeSet<Organisation> organisations = getApplicationOrganisations(userApplicationRoles);
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
        model.addAttribute("applicationReadyForSubmit", applicationService.isApplicationReadyForSubmit(application.getId()));
    }

    protected void addSectionDetails(Model model, Optional<Section> currentSection) {
        model.addAttribute("currentSectionId", currentSection.map(Section::getId).orElse(null));
        model.addAttribute("currentSection", currentSection.orElse(null));
    }

    protected Optional<Section> getSection(List<Section> sections, Optional<Long> sectionId, boolean selectFirstSectionIfNoneCurrentlySelected) {

        if (sectionId.isPresent()) {
            Long id = sectionId.get();

            // get the section that we want to show, so we can use this on to show the correct questions.
            return sections.stream().filter(x -> x.getId().equals(id)).findFirst();

        } else if (selectFirstSectionIfNoneCurrentlySelected) {
            return sections.isEmpty() ? Optional.empty() : Optional.ofNullable(sections.get(0));
        }

        return Optional.empty();
    }

    protected ApplicationFinanceResource getOrganisationFinances(Long applicationId, Long userId) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(applicationId, userId);
        if(applicationFinanceResource == null) {
            applicationFinanceResource = financeService.addApplicationFinance(applicationId, userId);
        }

        return applicationFinanceResource;
    }

    protected ApplicationResource addApplicationAndSections(ApplicationResource application,
                                                                             Competition competition,
                                                                             Long userId,
                                                                             Optional<Section> section,
                                                                             Optional<Long> currentQuestionId,
                                                                             Model model,
                                                                             ApplicationForm form) {

        List<ProcessRole> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        application = addApplicationDetails(application, competition, userId, section, currentQuestionId, model, form, userApplicationRoles);

        model.addAttribute("completedQuestionsPercentage", applicationService.getCompleteQuestionsPercentage(application.getId()));
        addSectionDetails(model, section);

        return application;
    }

    protected ApplicationResource addOrganisationAndUserFinanceDetails(ApplicationResource application,
                                                                             Long userId,
                                                                             Model model,
                                                                             ApplicationForm form) {
        addOrganisationFinanceDetails(model, application, userId, form);
        addFinanceDetails(model, application);
        return application;
    }

    public TreeSet<Organisation> getApplicationOrganisations(List<ProcessRole> userApplicationRoles) {
        Comparator<Organisation> compareById =
                Comparator.comparingLong(Organisation::getId);
        Supplier<TreeSet<Organisation>> supplier = () -> new TreeSet<>(compareById);

        return userApplicationRoles.stream()
                .filter(uar -> (uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) || uar.getRole().getName().equals(UserApplicationRole.COLLABORATOR.getRoleName())))
                .map(ProcessRole::getOrganisation)
                .collect(Collectors.toCollection(supplier));
    }

    public Optional<Organisation> getApplicationLeadOrganisation(List<ProcessRole> userApplicationRoles) {
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(ProcessRole::getOrganisation)
                .findFirst();
    }

    public Optional<Organisation> getUserOrganisation(Long userId, List<ProcessRole> userApplicationRoles) {
        return userApplicationRoles.stream()
                .filter(uar -> uar.getUser().getId().equals(userId))
                .map(ProcessRole::getOrganisation)
                .findFirst();
    }

    public UserAuthenticationService getUserAuthenticationService() {
        return userAuthenticationService;
    }
}
