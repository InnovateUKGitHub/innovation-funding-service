package com.worth.ifs.application;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.finance.model.OrganisationFinance;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;
import com.worth.ifs.application.helper.ApplicationHelper;
import com.worth.ifs.application.service.*;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.security.UserAuthenticationService;
import com.worth.ifs.user.domain.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    @Autowired
    FinanceService financeService;


    protected void assignQuestion(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey("assign_question")){
            String assign = request.getParameter("assign_question");
            Long questionId = Long.valueOf(assign.split("_")[0]);
            Long assigneeId = Long.valueOf(assign.split("_")[1]);

            questionService.assign(questionId, assigneeId);
        }
    }

    protected void addOrganisationDetails(Model model, Application application, Organisation userOrganisation) {
        ApplicationHelper applicationHelper = new ApplicationHelper();

        model.addAttribute("userOrganisation", userOrganisation);
        model.addAttribute("applicationOrganisations", applicationHelper.getApplicationOrganisations(application));
        Optional<Organisation> organisation = applicationHelper.getApplicationLeadOrganisation(application);
        if(organisation.isPresent()) {
            model.addAttribute("leadOrganisation", organisation.get());
        }
    }

    protected void addQuestionsDetails(Model model, Application application, Long userOrganisationId) {
        List<Question> questions = questionService.findByCompetition(application.getCompetition().getId());
        List<Response> responses = responseService.getByApplication(application.getId());
        model.addAttribute("responses", responseService.mapResponsesToQuestion(responses));
        model.addAttribute("assignableUsers", processRoleService.findAssignableProcessRoles(application.getId()));
        model.addAttribute("questionAssignees", questionService.mapAssigneeToQuestion(questions, userOrganisationId));
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

    protected void addDateDetails(Model model) {
        int todayDay =  LocalDateTime.now().getDayOfYear();
        model.addAttribute("todayDay", todayDay);
        model.addAttribute("yesterdayDay", todayDay-1);
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
