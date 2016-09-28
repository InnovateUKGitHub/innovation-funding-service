package com.worth.ifs.finance.spendprofile.approval.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.finance.spendprofile.approval.viewmodel.ProjectSpendProfileApprovalViewModel;
import com.worth.ifs.finance.spendprofile.summary.form.ProjectSpendProfileForm;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This Controller handles Spend Profile activity for the Internal Competition team members
 */
@Controller
@RequestMapping("/project/{projectId}/spend-profile")
public class ProjectSpendProfileApprovalController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/approval", method = GET)
    public String viewSpendProfileApproval(@PathVariable Long projectId, Model model) {
        return doViewSpendProfileApproval(projectId, model, new ProjectSpendProfileForm());
    }

    private String doViewSpendProfileApproval(Long projectId, Model model, ProjectSpendProfileForm form) {

        ProjectSpendProfileApprovalViewModel viewModel = populateSpendProfileApprovalViewModel(projectId);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);

        return "project/finance/spend-profile/approval";
    }

    private ProjectSpendProfileApprovalViewModel populateSpendProfileApprovalViewModel(Long projectId) {

        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        String leadTechnologist = competition.getLeadTechnologist() != null ? userService.findById(competition.getLeadTechnologist()).getName() : "";

        Boolean isApproved = false;
        Boolean isRejected = false;
        Boolean isNotApprovedOrRejected = !isApproved && !isRejected;

        return new ProjectSpendProfileApprovalViewModel(competitionSummary, leadTechnologist, isApproved, isRejected, isNotApprovedOrRejected);
    }

    private String redirectToViewSpendProfileApproval(Long projectId) {
        return "redirect:/project/" + projectId + "/spend-profile/approval";
    }
}
