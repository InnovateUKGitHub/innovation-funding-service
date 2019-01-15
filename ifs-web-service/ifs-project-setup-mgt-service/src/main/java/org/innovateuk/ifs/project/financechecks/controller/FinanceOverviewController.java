package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckOverviewViewModel;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckSummariesViewModel;
import org.innovateuk.ifs.financecheck.viewmodel.ProjectFinanceCostBreakdownViewModel;
import org.innovateuk.ifs.financecheck.viewmodel.ProjectFinanceOverviewViewModel;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
    private ProjectFinanceService financeService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @SecuredBySpring(value = "TODO", description = "TODO")
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
    public String view(@PathVariable("projectId") Long projectId,
                       Model model) {
        model.addAttribute("model", buildFinanceCheckOverviewViewModel(projectId));
        return "project/financecheck/overview";
    }

    private FinanceCheckOverviewViewModel buildFinanceCheckOverviewViewModel(final Long projectId) {
        final List<PartnerOrganisationResource> partnerOrgs = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();
        final PartnerOrganisationResource lead = simpleFindFirst(partnerOrgs, PartnerOrganisationResource::isLeadOrganisation).orElse(null);
        final List<PartnerOrganisationResource> sortedOrganisations
                = new PrioritySorting<>(partnerOrgs, lead, PartnerOrganisationResource::getOrganisationName).unwrap();
        ProjectResource project = projectService.getById(projectId);
        Long applicationId = project.getApplication();
        return new FinanceCheckOverviewViewModel(getProjectFinanceOverviewViewModel(projectId), getProjectFinanceSummaries(project, sortedOrganisations),
                getProjectFinanceCostBreakdown(projectId, sortedOrganisations), applicationId);
    }

    private ProjectFinanceOverviewViewModel getProjectFinanceOverviewViewModel(Long projectId) {
        FinanceCheckOverviewResource financeCheckOverviewResource = financeCheckService.getFinanceCheckOverview(projectId).getSuccess();
        return new ProjectFinanceOverviewViewModel(financeCheckOverviewResource);
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
