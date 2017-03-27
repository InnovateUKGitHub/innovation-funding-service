package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.util.PrioritySorting;
import org.innovateuk.ifs.project.PartnerOrganisationService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.financecheck.viewmodel.FinanceCheckOverviewViewModel;
import org.innovateuk.ifs.project.financecheck.viewmodel.FinanceCheckSummariesViewModel;
import org.innovateuk.ifs.project.financecheck.viewmodel.ProjectFinanceCostBreakdownViewModel;
import org.innovateuk.ifs.project.financecheck.viewmodel.ProjectFinanceOverviewViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
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
    private FinanceCheckService financeCheckService;

    @Autowired
    private PartnerOrganisationService partnerOrganisationService;

    @Autowired
    private ProjectFinanceService financeService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationService organisationService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
    public String view(@PathVariable("projectId") Long projectId,
                       Model model) {
        model.addAttribute("model", buildFinanceCheckOverviewViewModel(projectId));
        return "project/financecheck/overview";
    }

    private FinanceCheckOverviewViewModel buildFinanceCheckOverviewViewModel(final Long projectId) {
        List<PartnerOrganisationResource> partnerOrgs = partnerOrganisationService.getPartnerOrganisations(projectId).getSuccessObject();
        final PartnerOrganisationResource lead = simpleFindFirst(partnerOrgs, PartnerOrganisationResource::isLeadOrganisation).orElse(null);
        final List<PartnerOrganisationResource> sortedOrganisations
                = new PrioritySorting<>(partnerOrgs, lead, PartnerOrganisationResource::getOrganisationName).unwrap();

        return new FinanceCheckOverviewViewModel(getProjectFinanceOverviewViewModel(projectId), getProjectFinanceSummaries(projectId, sortedOrganisations),
                getProjectFinanceCostBreakdown(projectId, sortedOrganisations));
    }

    private ProjectFinanceOverviewViewModel getProjectFinanceOverviewViewModel(Long projectId) {
        FinanceCheckOverviewResource financeCheckOverviewResource = financeCheckService.getFinanceCheckOverview(projectId).getSuccessObjectOrThrowException();
        return new ProjectFinanceOverviewViewModel(financeCheckOverviewResource);
    }

    private FinanceCheckSummariesViewModel getProjectFinanceSummaries(Long projectId, List<PartnerOrganisationResource> partnerOrgs) {
        List<FinanceCheckEligibilityResource> summaries = mapWithIndex(partnerOrgs, (i, org) -> {
            return financeCheckService.getFinanceCheckEligibilityDetails(projectId, org.getOrganisation());
        });
        return new FinanceCheckSummariesViewModel(summaries, partnerOrgs);
    }

    private ProjectFinanceCostBreakdownViewModel getProjectFinanceCostBreakdown(Long projectId, List<PartnerOrganisationResource> partnerOrgs) {
        List<ProjectFinanceResource> finances = financeService.getProjectFinances(projectId);
        return new ProjectFinanceCostBreakdownViewModel(finances, partnerOrgs);
    }

}
