package com.worth.ifs.finance.spendprofile.partnerorganisation.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.finance.spendprofile.partnerorganisation.form.PartnerOrganisationSpendProfileForm;
import com.worth.ifs.finance.spendprofile.partnerorganisation.viewmodel.PartnerOrganisationSpendProfileViewModel;
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

import java.util.function.Supplier;

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

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @RequestMapping(method = GET)
    public String viewSpendProfile(@PathVariable Long projectId, @PathVariable Long partnerOrganisationId, Model model) {
        PartnerOrganisationSpendProfileForm form = new PartnerOrganisationSpendProfileForm();
        return doViewSpendProfile(projectId, partnerOrganisationId, model, form);
    }

    @RequestMapping(value = "/generate", method = POST)
    public String generateSpendProfile(@PathVariable Long projectId, @PathVariable Long partnerOrganisationId, Model model,
                                       @ModelAttribute PartnerOrganisationSpendProfileForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewSpendProfile(projectId, partnerOrganisationId, model, form);
        ServiceResult<Void> generateResult = projectFinanceService.generateSpendProfile(projectId, partnerOrganisationId);

        return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () ->
            redirectToViewSpendProfile(projectId, partnerOrganisationId)
        );
    }

    private String doViewSpendProfile(@PathVariable Long projectId, @PathVariable Long partnerOrganisationId, Model model, PartnerOrganisationSpendProfileForm form) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
        OrganisationResource partnerOrganisation = organisationService.getOrganisationById(partnerOrganisationId);

        model.addAttribute("model", new PartnerOrganisationSpendProfileViewModel(projectId, partnerOrganisation, competitionSummary));
        model.addAttribute("form", form);
        return "project/finance/spend-profile/partner-organisation";
    }

    private String redirectToViewSpendProfile(Long projectId, Long partnerOrganisationId) {
        return "redirect:/project/" + projectId + "/partner-organisation/" + partnerOrganisationId + "/spend-profile";
    }
}
