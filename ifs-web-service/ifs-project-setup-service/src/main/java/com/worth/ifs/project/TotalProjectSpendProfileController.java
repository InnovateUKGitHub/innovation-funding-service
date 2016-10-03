package com.worth.ifs.project;

import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.util.SpendProfileTableCalculator;
import com.worth.ifs.project.viewmodel.*;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static com.worth.ifs.util.CollectionFunctions.simpleMapValue;

/**
 * This controller will handle all requests that are related to spend profile.
 */
@Controller
@RequestMapping("/" + TotalProjectSpendProfileController.BASE_DIR + "/{projectId}/spend-profile/total")
public class TotalProjectSpendProfileController {

    public static final String BASE_DIR = "project";

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectFinanceService projectFinanceService;
    @Autowired
    private SpendProfileTableCalculator spendProfileTableCalculator;

    @PreAuthorize("hasPermission(#projectId, 'PROJECT_MANAGER_PERMISSIONS')")
    @RequestMapping(method = GET)
    public String totals(Model model, @PathVariable("projectId") final Long projectId) {
        model.addAttribute("model", buildTotalViewModel(projectId));
        return BASE_DIR + "/spend-profile-totals";
    }

    //TODO permissions
    @RequestMapping(value="confirmation", method = GET)
    public String confirmation(@PathVariable("projectId") final Long projectId) {
        return BASE_DIR + "/spend-profile-total-confirmation";
    }

    //TODO permissions
    @RequestMapping(method = POST)
    public String submitForReview(@PathVariable("projectId") final Long projectId) {
        projectFinanceService.completeSpendProfilesReview(projectId);
        return "redirect:/project/"+projectId;
    }



    private TotalSpendProfileViewModel buildTotalViewModel(final Long projectId) {
        ProjectResource projectResource = projectService.getById(projectId);
        TotalProjectSpendTableViewModel tableView = buildTableViewModel(projectId);
        SpendProfileSummaryModel summary  = spendProfileTableCalculator.createSpendProfileSummary(projectResource, tableView.getMonthlyCostsPerOrganisationMap(), tableView.getMonths());
        return new TotalSpendProfileViewModel(projectResource, tableView, summary);
    }

    private TotalProjectSpendTableViewModel buildTableViewModel(final Long projectId) {
        List<OrganisationResource> organisations = projectService.getPartnerOrganisationsForProject(projectId);
        Map<String, SpendProfileTableResource> organisationSpendProfiles = organisations.stream().collect(Collectors.toMap(OrganisationResource::getName, organisation -> {
            return projectFinanceService.getSpendProfileTable(projectId, organisation.getId());
        }));

        Map<String, List<BigDecimal>> monthlyCostsPerOrganisationMap = simpleMapValue(organisationSpendProfiles, tableResource -> {
            return spendProfileTableCalculator.calculateMonthlyTotals(tableResource.getMonthlyCostsPerCategoryMap(), tableResource.getMonths().size());
        });

        Map<String, BigDecimal> eligibleCostPerOrganisationMap = simpleMapValue(organisationSpendProfiles, tableResource -> {
            return spendProfileTableCalculator.calculateTotalOfAllEligibleTotals(tableResource.getEligibleCostPerCategoryMap());
        });

        List<LocalDateResource> months = organisationSpendProfiles.values().iterator().next().getMonths();

        Map<String, BigDecimal> organisationToActualTotal = spendProfileTableCalculator.calculateRowTotal(monthlyCostsPerOrganisationMap);
        List<BigDecimal> totalForEachMonth = spendProfileTableCalculator.calculateMonthlyTotals(monthlyCostsPerOrganisationMap, months.size());
        BigDecimal totalOfAllActualTotals = spendProfileTableCalculator.calculateTotalOfAllActualTotals(monthlyCostsPerOrganisationMap);
        BigDecimal totalOfAllEligibleTotals = spendProfileTableCalculator.calculateTotalOfAllEligibleTotals(eligibleCostPerOrganisationMap);


        return new TotalProjectSpendTableViewModel(months, monthlyCostsPerOrganisationMap, eligibleCostPerOrganisationMap, organisationToActualTotal, totalForEachMonth, totalOfAllActualTotals, totalOfAllEligibleTotals);

    }


}