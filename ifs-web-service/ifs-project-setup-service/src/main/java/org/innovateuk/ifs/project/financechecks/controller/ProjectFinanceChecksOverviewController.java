package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckOverviewViewModel;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckSummariesViewModel;
import org.innovateuk.ifs.financecheck.viewmodel.ProjectFinanceCostBreakdownViewModel;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    private PartnerOrganisationRestService partnerOrganisationRestService;

    private FinanceCheckService financeCheckService;

    private ProjectFinanceService financeService;

    private ProjectService projectService;

    private CompetitionRestService competitionRestService;

    ProjectFinanceChecksOverviewController() {}

    @Autowired
    public ProjectFinanceChecksOverviewController(PartnerOrganisationRestService partnerOrganisationRestService, FinanceCheckService financeCheckService, ProjectFinanceService financeService, ProjectService projectService, CompetitionRestService competitionRestService) {
        this.partnerOrganisationRestService = partnerOrganisationRestService;
        this.financeCheckService = financeCheckService;
        this.financeService = financeService;
        this.projectService = projectService;
        this.competitionRestService = competitionRestService;
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping
    public String viewOverview(Model model, @P("projectId")@PathVariable("projectId") final Long projectId, UserResource loggedInUser) {
        Long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);
        ProjectResource project = projectService.getById(projectId);
        Long applicationId = project.getApplication();
        FinanceCheckOverviewViewModel financeCheckOverviewViewModel = buildFinanceCheckOverviewViewModel(project);
        model.addAttribute("model", financeCheckOverviewViewModel);
        model.addAttribute("organisation", organisationId);
        model.addAttribute("project", project);

        return "project/finance-checks-overview";
    }

    private FinanceCheckOverviewViewModel buildFinanceCheckOverviewViewModel(final ProjectResource project) {
        List<PartnerOrganisationResource> partnerOrgs = partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId()).getSuccess();
        return new FinanceCheckOverviewViewModel(null, getProjectFinanceSummaries(project, partnerOrgs),
                getProjectFinanceCostBreakdown(project.getId(), partnerOrgs), project.getApplication());
    }

    private FinanceCheckSummariesViewModel getProjectFinanceSummaries(ProjectResource project, List<PartnerOrganisationResource> partnerOrgs) {
        List<FinanceCheckEligibilityResource> summaries = mapWithIndex(partnerOrgs, (i, org) ->
                financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), org.getOrganisation()));
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        return new FinanceCheckSummariesViewModel(summaries, partnerOrgs, competition.getFundingType());
    }

    private ProjectFinanceCostBreakdownViewModel getProjectFinanceCostBreakdown(Long projectId, List<PartnerOrganisationResource> partnerOrgs) {
        List<ProjectFinanceResource> finances = financeService.getProjectFinances(projectId);
        return new ProjectFinanceCostBreakdownViewModel(finances, partnerOrgs);
    }
}
