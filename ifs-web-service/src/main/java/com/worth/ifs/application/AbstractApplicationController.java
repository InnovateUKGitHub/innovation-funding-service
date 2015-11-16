package com.worth.ifs.application;

import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.finance.model.OrganisationFinance;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;
import com.worth.ifs.application.service.*;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.security.CookieFlashMessageFilter;
import com.worth.ifs.security.UserAuthenticationService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This object contains shared methods for all the Controllers related to the {@link Application} data.
 */
public abstract class AbstractApplicationController {
    private final Log log = LogFactory.getLog(getClass());

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

    @Autowired OrganisationService organisationService;

    @Autowired CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    FinanceService financeService;

    protected Long extractAssigneeProcessRoleIdFromAssignSubmit(HttpServletRequest request) {
        Long assigneeId = null;
        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey("assign_question")){
            String assign = request.getParameter("assign_question");
            assigneeId = Long.valueOf(assign.split("_")[1]);
        }

        return assigneeId;
    }

    protected Long extractQuestionProcessRoleIdFromAssignSubmit(HttpServletRequest request) {
        Long questionId = null;
        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey("assign_question")){
            String assign = request.getParameter("assign_question");
            questionId = Long.valueOf(assign.split("_")[0]);
        }

        return questionId;
    }

    protected void assignQuestion(HttpServletRequest request, Long applicationId) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRole assignedBy = processRoleService.findProcessRole(user.getId(), applicationId);

        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey("assign_question")){
            String assign = request.getParameter("assign_question");
            Long questionId = extractQuestionProcessRoleIdFromAssignSubmit(request);
            Long assigneeId = extractAssigneeProcessRoleIdFromAssignSubmit(request);

            questionService.assign(questionId, applicationId, assigneeId, assignedBy.getId());
        }
    }

    /**
     * Get the details of the current application, add this to the model so we can use it in the templates.
     */
    protected Application addApplicationDetails(Long applicationId, Long userId, Optional<Long> currentSectionId, Model model, boolean selectFirstSectionIfNoneCurrentlySelected, ApplicationForm applicationForm) {


            Application application = applicationService.getById(applicationId);
        Competition competition = application.getCompetition();

        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);

        Optional<Organisation> userOrganisation = organisationService.getUserOrganisation(application, userId);

        addOrganisationDetails(model, application, userOrganisation);
        addQuestionsDetails(model, application, applicationForm);
        addUserDetails(model, application, userId);
        addMarkedAsCompleteDetails(model, application, userOrganisation);

        userOrganisation.ifPresent(org -> {
            addAssigneableDetails(model, application, org, userId);
        });

        addMappedSectionsDetails(model, application, currentSectionId, userOrganisation, selectFirstSectionIfNoneCurrentlySelected);
        return application;
    }

    protected void addOrganisationDetails(Model model, Application application, Optional<Organisation> userOrganisation) {

        model.addAttribute("userOrganisation", userOrganisation.orElse(null));
        model.addAttribute("applicationOrganisations", organisationService.getApplicationOrganisations(application));

        Optional<Organisation> leadOrganisation = organisationService.getApplicationLeadOrganisation(application);
        leadOrganisation.ifPresent(org -> {
            model.addAttribute("leadOrganisation", org);
        });
    }

    protected void addQuestionsDetails(Model model, Application application, ApplicationForm applicationForm) {
        List<FormInputResponse> responses = getFormInputResponses(application);
        HashMap<Long, FormInputResponse> mappedResponses = formInputResponseService.mapResponsesToQuestion(responses);
        model.addAttribute("responses",mappedResponses);

        if(applicationForm == null){
            applicationForm = new ApplicationForm();
        }
        Map<String, String> values = applicationForm.getFormInput();
        mappedResponses.forEach((k, v) ->
             values.put(k.toString(), v.getValue())
        );
        applicationForm.setFormInput(values);
        model.addAttribute("applicationForm",applicationForm);
    }

    protected List<Response> getResponses(Application application) {
        return responseService.getByApplication(application.getId());
    }

    protected List<FormInputResponse> getFormInputResponses(Application application) {
        return formInputResponseService.getByApplication(application.getId());
    }

    protected void addUserDetails(Model model, Application application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
    }

    protected  void addMarkedAsCompleteDetails(Model model, Application application, Optional<Organisation> userOrganisation) {
        Long organisationId=0L;
        if(userOrganisation.isPresent()) {
            organisationId = userOrganisation.get().getId();
        }
        model.addAttribute("markedAsComplete", questionService.getMarkedAsComplete(application.getId(), organisationId));
    }
    protected void addAssigneableDetails(Model model, Application application, Organisation userOrganisation, Long userId) {
        List<Question> questions = questionService.findByCompetition(application.getCompetition().getId());
        model.addAttribute("assignableUsers", processRoleService.findAssignableProcessRoles(application.getId()));
        HashMap<Long, QuestionStatus> questionAssignees = questionService.mapAssigneeToQuestion(questions, userOrganisation.getId());
        model.addAttribute("questionAssignees", questionAssignees);
        List<QuestionStatus> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        model.addAttribute("notifications", notifications);
        questionService.removeNotifications(notifications);
        List<Long> assignedSections = sectionService.getUserAssignedSections(application.getCompetition().getSections(), questionAssignees, userId);
        model.addAttribute("assignedSections", assignedSections);
    }

    protected void addOrganisationFinanceDetails(Model model, Application application, Long userId) {
        OrganisationFinance organisationFinance = getOrganisationFinances(application.getId(), userId);
        model.addAttribute("organisationFinance", organisationFinance.getCostCategories());
        model.addAttribute("organisationFinanceTotal", organisationFinance.getTotal());
        model.addAttribute("organisationGrantClaimPercentage", organisationFinance.getGrantClaimPercentage());
        model.addAttribute("organisationgrantClaimPercentageId", organisationFinance.getGrantClaimPercentageId());


    }

    protected void addFinanceDetails(Model model, Application application) {
        Section section = sectionService.getByName("Your finances");
        sectionService.removeSectionsQuestionsWithType(section, "empty");
        log.info("FINANCE DETAILS : " + section);
        model.addAttribute("financeSection", section);

        OrganisationFinanceOverview organisationFinanceOverview = new OrganisationFinanceOverview(financeService, application.getId());
        model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
        model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType());
        model.addAttribute("organisationFinances", organisationFinanceOverview.getOrganisationFinances());
        model.addAttribute("grantTotalPercentage", organisationFinanceOverview.getTotalGrantPercentage());
    }

    protected void addMappedSectionsDetails(Model model, Application application, Optional<Long> currentSectionId, Optional<Organisation> userOrganisation, boolean selectFirstSectionIfNoneCurrentlySelected) {

        List<Section> sectionsList = sectionService.getParentSections(application.getCompetition().getSections());

        Map<Long, Section> sections =
                sectionsList.stream().collect(Collectors.toMap(Section::getId,
                        Function.identity()));

        model.addAttribute("sections", sections);
        addSectionDetails(model, application, currentSectionId, userOrganisation, selectFirstSectionIfNoneCurrentlySelected);
    }

    protected void addSectionsDetails(Model model, Application application, Optional<Long> currentSectionId, Optional<Organisation> userOrganisation, boolean selectFirstSectionIfNoneCurrentlySelected) {
        addSectionDetails(model, application, currentSectionId, userOrganisation, selectFirstSectionIfNoneCurrentlySelected);
        List<Section> sections = sectionService.getParentSections(application.getCompetition().getSections());
        model.addAttribute("sections", sections);
    }
    private void addSectionDetails(Model model, Application application, Optional<Long> currentSectionId, Optional<Organisation> userOrganisation, boolean selectFirstSectionIfNoneCurrentlySelected) {
        Optional<Section> currentSection = getSection(application.getCompetition().getSections(), currentSectionId, selectFirstSectionIfNoneCurrentlySelected);
        model.addAttribute("currentSectionId", currentSection.map(Section::getId).orElse(null));
        model.addAttribute("currentSection", currentSection.orElse(null));

        userOrganisation.ifPresent(org -> {
            model.addAttribute("completedSections", sectionService.getCompleted(application.getId(), org.getId()));
        });
    }

    protected Optional<Section> getSection(List<Section> sections, Optional<Long> sectionId, boolean selectFirstSectionIfNoneCurrentlySelected) {

        if (sectionId.isPresent()) {

            Long id = sectionId.get();

            // get the section that we want to show, so we can use this on to show the correct questions.
            return sections.stream().filter(x -> x.getId().equals(id)).findFirst();

        } else if (selectFirstSectionIfNoneCurrentlySelected) {
            return sections.size() > 0 ? Optional.of(sections.get(0)) : Optional.empty();
        }

        return Optional.empty();
    }

    protected OrganisationFinance getOrganisationFinances(Long applicationId, Long userId) {
        ApplicationFinance applicationFinance = financeService.getApplicationFinance(userId, applicationId);
        if(applicationFinance==null) {
            applicationFinance = financeService.addApplicationFinance(userId, applicationId);
        }

        List<Cost> organisationCosts = financeService.getCosts(applicationFinance.getId());
        return new OrganisationFinance(applicationFinance.getId(),applicationFinance.getOrganisation(),organisationCosts);
    }
}
