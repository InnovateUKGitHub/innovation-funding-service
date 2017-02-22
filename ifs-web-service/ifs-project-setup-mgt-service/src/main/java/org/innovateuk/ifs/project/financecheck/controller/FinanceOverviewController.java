package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummariesResource;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.financecheck.viewmodel.FinanceCheckOverviewViewModel;
import org.innovateuk.ifs.project.financecheck.viewmodel.FinanceCheckSummariesViewModel;
import org.innovateuk.ifs.project.financecheck.viewmodel.ProjectFinanceOverviewViewModel;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.mapWithIndex;

/**
 * This controller is for allowing internal users to view the finance checks overview for a project.
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check-overview")
public class FinanceOverviewController {

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationService organisationService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
    public String view(@PathVariable("projectId") Long projectId,
                       Model model) {
        FinanceCheckOverviewViewModel financeCheckOverviewViewModel = buildFinanceCheckOverviewViewModel(projectId);
        model.addAttribute("model", financeCheckOverviewViewModel);
        return "project/financecheck/overview";
    }

    private FinanceCheckOverviewViewModel buildFinanceCheckOverviewViewModel(final Long projectId) {
        List<Long> organisationIds = projectService.getProjectUsersForProject(projectId).stream().map(ProjectUserResource::getOrganisation).distinct().collect(Collectors.toList());
        return new FinanceCheckOverviewViewModel(buildProjectFinanceOverviewViewModel(projectId), buildProjectFinanceSummaries(projectId, organisationIds));
    }

    private ProjectFinanceOverviewViewModel buildProjectFinanceOverviewViewModel(Long projectId) {
        FinanceCheckOverviewResource financeCheckOverviewResource = financeCheckService.getFinanceCheckOverview(projectId).getSuccessObjectOrThrowException();
        return new ProjectFinanceOverviewViewModel(financeCheckOverviewResource);
    }

    private FinanceCheckSummariesViewModel buildProjectFinanceSummaries(Long projectId, List<Long> organisationIds) {
        Long leadOrganisationId = projectService.getLeadOrganisation(projectId).getId();
        List<FinanceCheckSummariesResource> summaries = mapWithIndex(organisationIds, (i, org) -> {

            FinanceCheckEligibilityResource eligibilityResource = financeCheckService.getFinanceCheckEligibilityDetails(projectId, org);
            String organisationName = organisationService.getOrganisationById(org).getName();

            return new FinanceCheckSummariesResource(eligibilityResource, organisationName, leadOrganisationId.equals(org));
        });

        return new FinanceCheckSummariesViewModel(summaries);
    }
}
