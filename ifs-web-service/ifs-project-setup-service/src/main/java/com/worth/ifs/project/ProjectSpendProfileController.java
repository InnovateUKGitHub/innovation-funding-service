package com.worth.ifs.project;

import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller will handle all requests that are related to spend profile.
 */
@Controller
@RequestMapping("/project/{projectId}/partner-organisation/{organisationId}/spend-profile")
public class ProjectSpendProfileController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @RequestMapping(method = GET)
    public String viewSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectSpendProfileViewModel viewModel = populateSpendProfileViewModel(projectId, organisationId);
        model.addAttribute("model", viewModel);

        return "project/spend-profile";
    }

    private ProjectSpendProfileViewModel populateSpendProfileViewModel(final Long projectId, final Long organisationId) {

        ProjectResource projectResource = projectService.getById(projectId);

        // Here we will call a data service to get the eligible project costs for each category.
        SpendProfileResource spendProfileResource = projectService.getSpendProfile(projectId, organisationId);

        SpendProfileTableResource table = buildSpendProfileTable(spendProfileResource, projectResource);

        return new ProjectSpendProfileViewModel(projectResource, table);
    }

    private SpendProfileTableResource buildSpendProfileTable(SpendProfileResource spendProfileResource, ProjectResource projectResource) {


        SpendProfileTableResource table = new SpendProfileTableResource();

        Long duration = projectResource.getDurationInMonths();

        table.setMonths(buildSpendProfileMonths(projectResource.getTargetStartDate(), duration));
        table.setMonthlyCostsPerCategoryMap(buildMonthlyCostsPerCategory(spendProfileResource, duration));

        return table;

    }

    private List<LocalDate> buildSpendProfileMonths(LocalDate startDate, Long duration) {

        List<LocalDate> months = new ArrayList<>();

        // The project start date is always the first of the month (not that it matters here). Adding a month will move us to the next month
        Stream.iterate(startDate, localDate -> localDate.plusMonths(1L)).limit(duration).forEach(date -> months.add(date));

        return months;
    }


    private Map<String, List<BigDecimal>> buildMonthlyCostsPerCategory(SpendProfileResource spendProfileResource, Long duration) {

        // Used a LinkedHashMap to preserve the order of insertion
        Map<String, List<BigDecimal>> monthlyCostsPerCategoryMap = new LinkedHashMap<>();

        Map<String, BigDecimal> eligibleCostPerCategoryMap = spendProfileResource.getEligibleCostPerCategoryMap();
        eligibleCostPerCategoryMap.forEach((category, totalEligibleCost) -> {
            List<BigDecimal> result = splitCostsAcrossMonths(totalEligibleCost, duration);
            monthlyCostsPerCategoryMap.put(category, result);
        });


        return monthlyCostsPerCategoryMap;

    }

    /*
     * This method will split the total cost dynamically based on the number of months and will add it to the list.
     * The splitting process will ensure that we do not get any fractional numbers for any month.
     * Therefore the surplus will be added over to the first month to ensure non-fractional splitting.
     * For example. the value 10 over 3 months will be split as 4, 3, 3 instead of 3.33, 3.33, 3.33
     */
    private List<BigDecimal> splitCostsAcrossMonths(BigDecimal totalCost, Long duration) {

        List<BigDecimal> result = new ArrayList<>();

        BigDecimal remainder = totalCost.remainder(new BigDecimal(duration.toString()));
        BigDecimal totalCostMinusRemainder = totalCost.subtract(remainder);

        // Here we have used the scale as 0 since we are not interested in the fractional part (there isn't going to be a fractional part anyways,
        // as now the total cost would be perfectly divisible by the number of months.
        // Which also means that the Rounding Mode of HALF_EVEN will have no effect - but is here as part of a standard process of division
        BigDecimal costPerMonth = totalCostMinusRemainder.divide(new BigDecimal(duration.toString()), 0, RoundingMode.HALF_EVEN);

        Stream.generate(() -> costPerMonth).limit(duration).forEach(result::add);

        if (!result.isEmpty()) {
            BigDecimal firstMonth = result.get(0);
            firstMonth = firstMonth.add(remainder);

            result.set(0, firstMonth);
        }
        //add the total as last row
        result.add(totalCost);
        return result;

    }
}
