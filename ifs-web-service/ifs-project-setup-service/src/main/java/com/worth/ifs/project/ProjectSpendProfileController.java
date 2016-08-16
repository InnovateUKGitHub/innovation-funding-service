package com.worth.ifs.project;

import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryYearModel;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;
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
    private ProjectFinanceService projectFinanceService;

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
        SpendProfileTableResource table = projectFinanceService.getSpendProfileTable(projectId, organisationId);
        List<SpendProfileSummaryYearModel> years = createSpendProfileSummaryYears(projectResource);
        SpendProfileSummaryModel summary = new SpendProfileSummaryModel(years);
        return new ProjectSpendProfileViewModel(projectResource, table, summary);
    }

    private List<SpendProfileSummaryYearModel> createSpendProfileSummaryYears(ProjectResource project){
        Integer startYear = project.getTargetStartDate().getYear();
        Integer endYear = project.getTargetStartDate().plusMonths(project.getDurationInMonths()).getYear()+1;
        //TODO add logic for populating the table with the correct values after this has been implemented
        return LongStream.range(startYear, endYear).mapToObj(year -> new SpendProfileSummaryYearModel(year, "123456.78")).collect(toList());
    }
}
