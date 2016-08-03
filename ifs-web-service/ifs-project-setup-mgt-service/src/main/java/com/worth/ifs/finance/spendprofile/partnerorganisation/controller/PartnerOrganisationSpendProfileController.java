package com.worth.ifs.finance.spendprofile.partnerorganisation.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.finance.spendprofile.partnerorganisation.viewmodel.PartnerOrganisationSpendProfileViewModel;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This Controller handles Spend Profile activity for the Internal Finance Team users
 */
@Controller
@RequestMapping("/project/{projectId}/partner-organisation/{partnerOrganisationId}/spend-profile")
public class PartnerOrganisationSpendProfileController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private OrganisationService organisationService;

    @RequestMapping(method = GET)
    public String viewSpendProfile(@PathVariable Long projectId, @PathVariable Long partnerOrganisationId, Model model) {

        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
        OrganisationResource partnerOrganisation = organisationService.getOrganisationById(partnerOrganisationId);

        model.addAttribute("model", new PartnerOrganisationSpendProfileViewModel(projectId, partnerOrganisation, competitionSummary));
        return "project/finance/spend-profile/partner-organisation";
    }

    @RequestMapping(value = "/generate", method = POST)
    public String generateSpendProfile(@PathVariable Long projectId, @PathVariable Long partnerOrganisationId, Model model) {

        System.out.println("Generated!");

//        ProjectResource project = projectService.getById(projectId);
//        ApplicationResource application = applicationService.getById(project.getApplication());
//        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
//        OrganisationResource partnerOrganisation = organisationService.getOrganisationById(partnerOrganisationId);
//
//        model.addAttribute("model", new PartnerOrganisationSpendProfileViewModel(projectId, partnerOrganisation, competitionSummary));
        return "redirect:/project/" + projectId + "/partner-organisation/" + partnerOrganisationId + "/spend-profile";
    }
}
