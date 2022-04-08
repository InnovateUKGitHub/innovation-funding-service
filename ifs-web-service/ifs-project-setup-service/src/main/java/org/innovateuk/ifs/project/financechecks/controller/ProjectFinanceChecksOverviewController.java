package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckOverviewViewModel;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckSummariesViewModel;
import org.innovateuk.ifs.financecheck.viewmodel.ProjectFinanceCostBreakdownViewModel;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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

    public static final String PROJECT_FINANCE_CHECKS_BASE_URL = "/project/{projectId}/finance-check/overview";

    private PartnerOrganisationRestService partnerOrganisationRestService;

    private FinanceCheckService financeCheckService;

    private ProjectFinanceRestService projectFinanceRestService;

    private ProjectService projectService;

    private CompetitionRestService competitionRestService;

    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;

    ProjectFinanceChecksOverviewController() {}

    @Autowired
    public ProjectFinanceChecksOverviewController(PartnerOrganisationRestService partnerOrganisationRestService, FinanceCheckService financeCheckService, ProjectFinanceRestService projectFinanceRestService, ProjectService projectService, CompetitionRestService competitionRestService, ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator) {
        this.partnerOrganisationRestService = partnerOrganisationRestService;
        this.financeCheckService = financeCheckService;
        this.projectFinanceRestService = projectFinanceRestService;
        this.projectService = projectService;
        this.competitionRestService = competitionRestService;
        this.applicationFundingBreakdownViewModelPopulator = applicationFundingBreakdownViewModelPopulator;
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping
    public String viewOverview(Model model, @P("projectId")@PathVariable("projectId") final Long projectId, UserResource loggedInUser) {
        Long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);
        ProjectResource project = projectService.getById(projectId);
        FinanceCheckOverviewViewModel financeCheckOverviewViewModel = buildFinanceCheckOverviewViewModel(project, loggedInUser);
        model.addAttribute("model", financeCheckOverviewViewModel);
        model.addAttribute("organisation", organisationId);
        model.addAttribute("project", project);

        return "project/finance-checks-overview";
    }

    private FinanceCheckOverviewViewModel buildFinanceCheckOverviewViewModel(final ProjectResource project, final UserResource loggedInUser) {
        List<PartnerOrganisationResource> partnerOrgs = partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId()).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModelPopulator.populateFromProject(project, loggedInUser);

        return new FinanceCheckOverviewViewModel(null,
                getProjectFinanceSummaries(project, partnerOrgs, competition),
                getProjectFinanceCostBreakdown(project.getId(), partnerOrgs, competition),
                project.getApplication(),
                false,
                competition.isLoan(),
                competition.isKtp(),
                false,
                competition.getFinanceRowTypes().contains(FinanceRowType.FINANCE),
                applicationFundingBreakdownViewModel,
                competition.isThirdPartyOfgem(),
                competition.isHorizonEuropeGuarantee());
    }

    private FinanceCheckSummariesViewModel getProjectFinanceSummaries(ProjectResource project, List<PartnerOrganisationResource> partnerOrgs, CompetitionResource competition) {
        List<FinanceCheckEligibilityResource> summaries = mapWithIndex(partnerOrgs, (i, org) ->
                financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), org.getOrganisation()));
        return new FinanceCheckSummariesViewModel(summaries, partnerOrgs, competition.getFundingType());
    }

    private ProjectFinanceCostBreakdownViewModel getProjectFinanceCostBreakdown(Long projectId, List<PartnerOrganisationResource> partnerOrgs, CompetitionResource competition) {
        List<ProjectFinanceResource> finances = projectFinanceRestService.getProjectFinances(projectId).getSuccess();
        return new ProjectFinanceCostBreakdownViewModel(finances, partnerOrgs, competition);
    }
}
