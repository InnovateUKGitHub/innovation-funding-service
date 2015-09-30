package com.worth.ifs.application;

import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.finance.model.OrganisationFinance;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;
import com.worth.ifs.application.service.*;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.security.CookieFlashMessageFilter;
import com.worth.ifs.security.UserAuthenticationService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractApplicationController {

    @Autowired
    protected ResponseService responseService;

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

    protected void addOrganisationDetails(Model model, Application application, Organisation userOrganisation) {
        model.addAttribute("userOrganisation", userOrganisation);
        model.addAttribute("applicationOrganisations", organisationService.getApplicationOrganisations(application));
        Optional<Organisation> organisation = organisationService.getApplicationLeadOrganisation(application);
        if(organisation.isPresent()) {
            model.addAttribute("leadOrganisation", organisation.get());
        }
    }

    protected void addUserDetails(Model model, Application application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
    }

    protected void addQuestionsDetails(Model model, Application application, Long userOrganisationId, Long userId) {
        List<Response> responses = responseService.getByApplication(application.getId());
        model.addAttribute("responses", responseService.mapResponsesToQuestion(responses));
        addAssigneableDetails(model, application, userOrganisationId, userId);
    }

    protected void addAssigneableDetails(Model model, Application application, Long userOrganisationId, Long userId) {
        List<Question> questions = questionService.findByCompetition(application.getCompetition().getId());
        model.addAttribute("assignableUsers", processRoleService.findAssignableProcessRoles(application.getId()));
        HashMap<Long, QuestionStatus> questionAssignees = questionService.mapAssigneeToQuestion(questions, userOrganisationId);
        model.addAttribute("questionAssignees", questionAssignees);
        List<QuestionStatus> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        model.addAttribute("notifications",notifications);
        questionService.removeNotifications(notifications);
        List<Long> assignedSections = sectionService.getUserAssignedSections(application.getCompetition().getSections(), questionAssignees, userId);
        model.addAttribute("assignedSections", assignedSections);
    }

    protected void addFinanceDetails(Model model, Application application, Long userId) {
        OrganisationFinance organisationFinance = getOrganisationFinances(application.getId(), userId);
        model.addAttribute("organisationFinance", organisationFinance.getCostCategories());
        model.addAttribute("organisationFinanceTotal", organisationFinance.getTotal());

        Section section = sectionService.getByName("Your finances");
        sectionService.removeSectionsQuestionsWithType(section, "empty");
        model.addAttribute("financeSection", section);

        OrganisationFinanceOverview organisationFinanceOverview = new OrganisationFinanceOverview(financeService, application.getId());
        model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
        model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType());
        model.addAttribute("organisationFinances", organisationFinanceOverview.getOrganisationFinances());
    }

    protected void addMappedSectionsDetails(Model model, Application application, Long currentSectionId, Long userOrganisationId) {
        addSectionDetails(model, application, currentSectionId, userOrganisationId);
        List<Section> sectionsList = sectionService.getParentSections(application.getCompetition().getSections());
        Map<Long, Section> sections =
                sectionsList.stream().collect(Collectors.toMap(Section::getId,
                        Function.identity()));
        model.addAttribute("sections", sections);
    }
    protected void addSectionsDetails(Model model, Application application, Long currentSectionId, Long userOrganisationId) {
        addSectionDetails(model, application, currentSectionId, userOrganisationId);
        List<Section> sections = sectionService.getParentSections(application.getCompetition().getSections());
        model.addAttribute("sections", sections);
    }
    private void addSectionDetails(Model model, Application application, Long currentSectionId, Long userOrganisationId) {
        Section currentSection = getSection(application.getCompetition().getSections(), currentSectionId);
        model.addAttribute("currentSectionId", currentSectionId);
        model.addAttribute("currentSection", currentSection);
        model.addAttribute("completedSections", sectionService.getCompleted(application.getId(), userOrganisationId));
    }

    protected Section getSection(List<Section> sections, Long sectionId) {
        // get the section that we want to show, so we can use this on to show the correct questions.
        Optional<Section> section = sections.stream().
                filter(x -> x.getId().equals(sectionId))
                .findFirst();

        return section.isPresent() ? section.get() : null;
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
