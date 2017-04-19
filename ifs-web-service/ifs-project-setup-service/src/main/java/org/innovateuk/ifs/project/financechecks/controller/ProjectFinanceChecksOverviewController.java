package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.PartnerOrganisationService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.financecheck.viewmodel.FinanceCheckOverviewViewModel;
import org.innovateuk.ifs.project.financecheck.viewmodel.FinanceCheckSummariesViewModel;
import org.innovateuk.ifs.project.financecheck.viewmodel.ProjectFinanceCostBreakdownViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.mapWithIndex;

/**
 * This controller will handle requests related to the finance checks overview page
 */
@Controller
@RequestMapping(ProjectFinanceChecksOverviewController.PROJECT_FINANCE_CHECKS_BASE_URL)
public class ProjectFinanceChecksOverviewController {

    public static final String PROJECT_FINANCE_CHECKS_BASE_URL = "/project/{projectId}/finance-checks/overview";

    private PartnerOrganisationService partnerOrganisationService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private ProjectFinanceService financeService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationService organisationService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping
    public String viewOverview(Model model, @PathVariable("projectId") final Long projectId, @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        Long organisationId = organisationService.getOrganisationIdFromUser(projectId, loggedInUser);
        FinanceCheckOverviewViewModel financeCheckOverviewViewModel = buildFinanceCheckOverviewViewModel(projectId);
        ProjectResource project = projectService.getById(projectId);
        model.addAttribute("model", financeCheckOverviewViewModel);
        model.addAttribute("organisation", organisationId);
        model.addAttribute("project", project);

        return "project/finance-checks-overview";
    }

    private FinanceCheckOverviewViewModel buildFinanceCheckOverviewViewModel(final Long projectId) {
        List<PartnerOrganisationResource> partnerOrgs = partnerOrganisationService.getPartnerOrganisations(projectId).getSuccessObject();
        return new FinanceCheckOverviewViewModel(null, getProjectFinanceSummaries(projectId, partnerOrgs),
                getProjectFinanceCostBreakdown(projectId, partnerOrgs));
    }

    private FinanceCheckSummariesViewModel getProjectFinanceSummaries(Long projectId, List<PartnerOrganisationResource> partnerOrgs) {
        List<FinanceCheckEligibilityResource> summaries = mapWithIndex(partnerOrgs, (i, org) -> financeCheckService.getFinanceCheckEligibilityDetails(projectId, org.getOrganisation()));
        return new FinanceCheckSummariesViewModel(summaries, partnerOrgs);
    }

    private ProjectFinanceCostBreakdownViewModel getProjectFinanceCostBreakdown(Long projectId, List<PartnerOrganisationResource> partnerOrgs) {
        List<ProjectFinanceResource> finances = financeService.getProjectFinances(projectId);
        return new ProjectFinanceCostBreakdownViewModel(finances, partnerOrgs);
    }
}
