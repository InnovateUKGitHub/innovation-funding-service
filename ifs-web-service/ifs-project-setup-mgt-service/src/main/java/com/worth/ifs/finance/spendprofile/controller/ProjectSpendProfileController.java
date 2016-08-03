package com.worth.ifs.finance.spendprofile.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.finance.spendprofile.viewmodel.ProjectSpendProfileViewModel;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This Controller handles Spend Profile activity for the Internal Finance Team users
 */
@Controller
@RequestMapping("/project/{projectId}/spend-profile")
public class ProjectSpendProfileController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @RequestMapping(value = "/summary", method = GET)
    public String viewSpendProfileSummary(@PathVariable Long projectId, Model model) {

        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);

        model.addAttribute("model", new ProjectSpendProfileViewModel(projectId, competitionSummary, partnerOrganisations));
        return "project/finance/spend-profile/summary";
    }
}
