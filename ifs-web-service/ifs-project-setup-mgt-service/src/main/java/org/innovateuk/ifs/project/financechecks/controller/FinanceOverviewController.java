package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckOverviewViewModel;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckSummariesViewModel;
import org.innovateuk.ifs.financecheck.viewmodel.ProjectFinanceCostBreakdownViewModel;
import org.innovateuk.ifs.financecheck.viewmodel.ProjectFinanceOverviewViewModel;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.mapWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This controller is for allowing internal users to view the finance checks overview for a project.
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check-overview")
public class FinanceOverviewController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;


    @SecuredBySpring(value = "TODO", description = "TODO")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('comp_admin', 'external_finance')")
    public String view(@PathVariable("projectId") Long projectId,
                       @RequestParam(required = false, defaultValue = "false") boolean showFundingLevelMessage,
                       @RequestParam(required = false, defaultValue = "false") boolean showFundingAmountMessage,
                       Model model, UserResource loggedInUser) {
        model.addAttribute("showFundingLevelMessage", showFundingLevelMessage);
        model.addAttribute("showFundingAmountMessage", showFundingAmountMessage);
        model.addAttribute("model", buildFinanceCheckOverviewViewModel(projectId, loggedInUser));
        return "project/financecheck/overview";
    }

    private FinanceCheckOverviewViewModel buildFinanceCheckOverviewViewModel(long projectId, UserResource loggedInUser) {
        final FinanceCheckSummaryResource financeCheckSummary = financeCheckService.getFinanceCheckSummary(projectId).getSuccess();
        final List<PartnerOrganisationResource> partnerOrgs = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();
        final PartnerOrganisationResource lead = simpleFindFirst(partnerOrgs, PartnerOrganisationResource::isLeadOrganisation).orElse(null);
        final List<PartnerOrganisationResource> sortedOrganisations
                = new PrioritySorting<>(partnerOrgs, lead, PartnerOrganisationResource::getOrganisationName).unwrap();
        ProjectResource project = projectService.getById(projectId);
        long applicationId = project.getApplication();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        boolean canChangeFundingSought =
                competition.getFinanceRowTypes().contains(FinanceRowType.GRANT_CLAIM_AMOUNT) && !financeCheckSummary.isSpendProfilesGenerated();
        boolean canChangeFundingLevel =
                competition.getFinanceRowTypes().contains(FinanceRowType.FINANCE) && financeCheckSummary.isAllEligibilityAndViabilityInReview();

        ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModelPopulator.populateFromProject(project, loggedInUser);

        return new FinanceCheckOverviewViewModel(
                        getProjectFinanceOverviewViewModel(projectId),
                        getProjectFinanceSummaries(project, sortedOrganisations, competition),
                        getProjectFinanceCostBreakdown(projectId, sortedOrganisations, competition),
                        applicationId,
                        canChangeFundingSought,
                        competition.isLoan(),
                        competition.isKtp(),
                        canChangeFundingLevel,
                        competition.getFinanceRowTypes().contains(FinanceRowType.FINANCE),
                        applicationFundingBreakdownViewModel);
    }

    private ProjectFinanceOverviewViewModel getProjectFinanceOverviewViewModel(long projectId) {
        FinanceCheckOverviewResource financeCheckOverviewResource = financeCheckService.getFinanceCheckOverview(projectId).getSuccess();
        return new ProjectFinanceOverviewViewModel(financeCheckOverviewResource);
    }

    private FinanceCheckSummariesViewModel getProjectFinanceSummaries(ProjectResource project, List<PartnerOrganisationResource> partnerOrgs, CompetitionResource competition) {
        List<FinanceCheckEligibilityResource> summaries = mapWithIndex(partnerOrgs, (i, org) ->
                financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), org.getOrganisation()));
        return new FinanceCheckSummariesViewModel(summaries, partnerOrgs, competition.getFundingType());
    }

    private ProjectFinanceCostBreakdownViewModel getProjectFinanceCostBreakdown(long projectId, List<PartnerOrganisationResource> partnerOrgs, CompetitionResource competition) {
        List<ProjectFinanceResource> finances = projectFinanceRestService.getProjectFinances(projectId).getSuccess();
        return new ProjectFinanceCostBreakdownViewModel(finances, partnerOrgs, competition);
    }
}