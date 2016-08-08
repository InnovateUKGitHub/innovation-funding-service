package com.worth.ifs.finance.spendprofile.summary.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.finance.spendprofile.summary.form.ProjectSpendProfileForm;
import com.worth.ifs.finance.spendprofile.summary.viewmodel.ProjectSpendProfileSummaryViewModel;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
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
 * This Controller handles Spend Profile activity for the Internal Finance Team users
 */
@Controller
@RequestMapping("/project/{projectId}/spend-profile")
public class ProjectSpendProfileSummaryController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

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

        model.addAttribute("model", new ProjectSpendProfileSummaryViewModel(projectId, competitionSummary, partnerOrganisations));
        return "project/finance/spend-profile/summary";
    }

    @RequestMapping(value = "/generate", method = POST)
    public String generateSpendProfile(@PathVariable Long projectId, Model model,
                                       @ModelAttribute ProjectSpendProfileForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewSpendProfileSummary(projectId, model, form);
        ServiceResult<Void> generateResult = projectFinanceService.generateSpendProfile(projectId);

        return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () ->
                redirectToViewSpendProfile(projectId)
        );
    }

    private String doViewSpendProfileSummary(@PathVariable Long projectId, Model model, ProjectSpendProfileForm form) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);

        model.addAttribute("model", new ProjectSpendProfileSummaryViewModel(projectId, competitionSummary, partnerOrganisations));
        model.addAttribute("form", form);
        return "project/finance/spend-profile/summary";
    }

    private String redirectToViewSpendProfile(Long projectId) {
        return "redirect:/project/" + projectId + "/spend-profile/summary";
    }
}
