package com.worth.ifs.application;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.finance.model.OrganisationFinance;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;
import com.worth.ifs.application.helper.ApplicationHelper;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ResponseService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.application.service.UserService;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.security.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public abstract class AbstractApplicationController {

    @Autowired
    protected ResponseService responseService;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected SectionService sectionService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserAuthenticationService userAuthenticationService;

    @Autowired
    FinanceService financeService;


    protected void assignQuestion(HttpServletRequest request, Long applicationId, Long userId) {
        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey("assign_question")){
            String assign = request.getParameter("assign_question");
            Long questionId = Long.valueOf(assign.split("_")[0]);
            Long assigneeId = Long.valueOf(assign.split("_")[1]);

            responseService.assignQuestion(applicationId, questionId, userId, assigneeId);
        }
    }

    protected void addFinanceDetails(Model model, Application application, Long userId) {
        OrganisationFinance organisationFinance = getOrganisationFinances(application.getId(), userId);
        model.addAttribute("organisationFinance", organisationFinance.getCostCategories());
        model.addAttribute("organisationFinanceTotal", organisationFinance.getTotal());


        Section section = sectionService.getByName("Your finances");
        sectionService.removeSectionsQuestionsWithType(section, "empty");
        model.addAttribute("financeSection", section);

        ApplicationHelper applicationHelper = new ApplicationHelper();
        Boolean userIsLeadApplicant = applicationHelper.isLeadApplicant(userId, application);
        if(userIsLeadApplicant) {
            OrganisationFinanceOverview organisationFinanceOverview = new OrganisationFinanceOverview(financeService, application.getId());
            model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
            model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType());
            model.addAttribute("organisationFinances", organisationFinanceOverview.getOrganisationFinances());
        }
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
