package com.worth.ifs.finance.spendprofile.approval.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.finance.spendprofile.approval.form.ProjectSpendProfileApprovalForm;
import com.worth.ifs.finance.spendprofile.approval.viewmodel.ProjectSpendProfileApprovalViewModel;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.resource.ApprovalType;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.function.Supplier;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_SPEND_PROFILE_SECTION')")
    @RequestMapping(value = "/approval", method = GET)
    public String viewSpendProfileApproval(@PathVariable Long projectId, Model model) {
        return doViewSpendProfileApproval(projectId, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_SPEND_PROFILE_SECTION')")
    @RequestMapping(value = "/approval/{approvalType}", method = POST)
    public String saveSpendProfileApproval(@PathVariable Long projectId,
                                           @PathVariable ApprovalType approvalType,
                                           @ModelAttribute ProjectSpendProfileApprovalForm form,
                                           Model model,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewSpendProfileApproval(projectId, model);
        ServiceResult<Void> generateResult = projectFinanceService.approveOrRejectSpendProfile(projectId, approvalType);

        return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () ->
                redirectToCompetitionSummaryPage(projectId)
        );
    }

    private String doViewSpendProfileApproval(Long projectId, Model model) {
        ProjectSpendProfileApprovalViewModel viewModel = populateSpendProfileApprovalViewModel(projectId);

        model.addAttribute("model", viewModel);

        return "project/finance/spend-profile/approval";
    }

    private ProjectSpendProfileApprovalViewModel populateSpendProfileApprovalViewModel(Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        String leadTechnologist = competition.getLeadTechnologist() != null ? userService.findById(competition.getLeadTechnologist()).getName() : "";
        ApprovalType approvalType = projectFinanceService.getSpendProfileStatusByProjectId(projectId);

        List<OrganisationResource> organisationResources = projectService.getPartnerOrganisationsForProject(projectId);

        return new ProjectSpendProfileApprovalViewModel(competitionSummary, leadTechnologist, approvalType, organisationResources);
    }

    private String redirectToCompetitionSummaryPage(Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        return "redirect:/competition/" + competition.getId() + "/status";
    }
}
