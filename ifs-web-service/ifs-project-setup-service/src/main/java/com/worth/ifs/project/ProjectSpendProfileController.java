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
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller will handle all requests that are related to spend profile.
 */
@Controller
@RequestMapping("/project/{projectId}/spend-profile")
public class ProjectSpendProfileController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @RequestMapping(method = GET)
    public String viewSpendProfile(Model model, @PathVariable("projectId") final Long projectId,
                                        @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectSpendProfileViewModel viewModel = populateSpendProfileViewModel(projectId);
        model.addAttribute("model", viewModel);

        return "project/spend-profile";
    }

    private ProjectSpendProfileViewModel populateSpendProfileViewModel(Long projectId) {

        ProjectResource projectResource = projectService.getById(projectId);

        // At the moment we call this dummy method to populate the data - this will be replaced with a call to the data layer
        // Here we will call a data service to get the Eligible Project costs for each category.
        SpendProfileResource spendProfileResource = tempGetSpendProfileEligibleCosts();

        SpendProfileTableResource table = buildSpendProfileTable(spendProfileResource, projectResource);

        return new ProjectSpendProfileViewModel(projectResource);
    }

    private SpendProfileResource tempGetSpendProfileEligibleCosts() {

        SpendProfileResource spendProfileResource = new SpendProfileResource();
        spendProfileResource.setEligibleLabourCost(new BigDecimal("240"));
        spendProfileResource.setEligibleAdminSupportCost(new BigDecimal("120"));
        spendProfileResource.setEligibleMaterialCost(new BigDecimal("180"));
        spendProfileResource.setEligibleCapitalCost(new BigDecimal("190"));
        spendProfileResource.setEligibleSubcontractingCost(new BigDecimal("160"));
        spendProfileResource.setEligibleTravelAndSubsistenceCost(new BigDecimal("850"));
        spendProfileResource.setEligibleOtherCost(new BigDecimal("210"));

        return spendProfileResource;

    }

    private SpendProfileTableResource buildSpendProfileTable(SpendProfileResource spendProfileResource, ProjectResource projectResource) {


        SpendProfileTableResource table = new SpendProfileTableResource();

        Long duration = projectResource.getDurationInMonths();

        table.setMonths(buildSpendProfileMonths(projectResource.getTargetStartDate(), duration));

        table.setMonthlyLabourCost(splitCostsAcrossMonths(spendProfileResource.getEligibleLabourCost(), duration));
        table.setMonthlyAdminSupportCost(splitCostsAcrossMonths(spendProfileResource.getEligibleAdminSupportCost(), duration));
        table.setMonthlyMaterialCost(splitCostsAcrossMonths(spendProfileResource.getEligibleMaterialCost(), duration));
        table.setMonthlyCapitalCost(splitCostsAcrossMonths(spendProfileResource.getEligibleCapitalCost(), duration));
        table.setMonthlySubcontractingCost(splitCostsAcrossMonths(spendProfileResource.getEligibleSubcontractingCost(), duration));
        table.setMonthlyTravelAndSubsistenceCost(splitCostsAcrossMonths(spendProfileResource.getEligibleTravelAndSubsistenceCost(), duration));
        table.setMonthlyOtherCost(splitCostsAcrossMonths(spendProfileResource.getEligibleOtherCost(), duration));

        return table;

    }

    private List<LocalDate> buildSpendProfileMonths(LocalDate startDate, Long duration) {

        List<LocalDate> months = new ArrayList<>();

        // The project start date is always the first of the month (not that it matters here). Adding a month will move us to the next month
        Stream.iterate(startDate, localDate -> localDate.plusMonths(1L)).limit(duration).forEach(date -> months.add(date));

        return months;
    }


    // If we don't want fractional numbers, I "could" set the scale to 0 and it works fine, but don't think it will total up to the correct value.
    // Also I believe that if we get a fraction then we need some sort of logic to adjust it. For example with value as 10 and 3 months, we should get 4, 3, 3 instead of
    // 3.33, 3.33, 3.33
    // At the moment, this logic nicely spits the value equally
    private List<BigDecimal> splitCostsAcrossMonths(BigDecimal totalCost, Long duration) {

        List<BigDecimal> result = new ArrayList<>();

        BigDecimal costPerMonth = totalCost.divide(new BigDecimal(duration.toString()), 2, RoundingMode.HALF_EVEN);

        Stream.generate(() -> costPerMonth).limit(duration).forEach(result::add);

        return result;

    }

    // TODO - For testing purpose only - will be deleted later - Ignore
    public static void main(String[] args) {


        ProjectResource projectResource = new ProjectResource();
        projectResource.setDurationInMonths(15L);
        projectResource.setTargetStartDate(LocalDate.now());

        ProjectSpendProfileController controller = new ProjectSpendProfileController();
        SpendProfileResource spendProfileResource = controller.tempGetSpendProfileEligibleCosts();


        SpendProfileTableResource table = controller.buildSpendProfileTable(spendProfileResource, projectResource);
    }
}
