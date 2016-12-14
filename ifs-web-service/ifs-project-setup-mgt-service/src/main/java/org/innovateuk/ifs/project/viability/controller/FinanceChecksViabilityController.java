package org.innovateuk.ifs.project.viability.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.finance.resource.ViabilityResource;
import org.innovateuk.ifs.project.viability.form.FinanceChecksViabilityForm;
import org.innovateuk.ifs.project.viability.viewmodel.FinanceChecksViabilityViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_EVEN;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller serves the Viability page where internal users can confirm the viability of a partner organisation's
 * financial position on a Project
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/viability")
public class FinanceChecksViabilityController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProjectFinanceService financeService;

    @RequestMapping(method = GET)
    public String viewViability(@PathVariable("projectId") Long projectId,
                                @PathVariable("organisationId") Long organisationId, Model model) {

        populateViewModel(projectId, organisationId, model);
        populateForm(projectId, organisationId, model);
        return "project/financecheck/viability";
    }

    @RequestMapping(method = POST, params = "save-and-continue")
    public String saveAndContinue(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute("form") FinanceChecksViabilityForm form) {

        financeService.saveViability(projectId, organisationId, Viability.PENDING);

        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/viability";
    }

    @RequestMapping(method = POST, params = "confirm-viability")
    public String confirmViability(@PathVariable("projectId") Long projectId,
                                   @PathVariable("organisationId") Long organisationId,
                                   @ModelAttribute("form") FinanceChecksViabilityForm form) {

        financeService.saveViability(projectId, organisationId, Viability.APPROVED);

        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/viability";
    }

    private void populateViewModel(Long projectId, Long organisationId, Model model) {

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        List<ProjectFinanceResource> projectFinances = financeService.getProjectFinances(projectId);
        ProjectFinanceResource financesForOrganisation = simpleFindFirst(projectFinances,
                finance -> finance.getOrganisation().equals(organisationId)).get();

        String organisationName = organisation.getName();
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        Integer totalCosts = toZeroScaleInt(financesForOrganisation.getTotal());
        Integer percentageGrant = financesForOrganisation.getGrantClaimPercentage();
        Integer fundingSought = toZeroScaleInt(financesForOrganisation.getTotalFundingSought());
        Integer otherPublicSectorFunding = toZeroScaleInt(financesForOrganisation.getTotalOtherFunding());
        Integer contributionToProject = toZeroScaleInt(financesForOrganisation.getTotalContribution());

        String companyRegistrationNumber = organisation.getCompanyHouseNumber();
        Integer turnover = null; // for this release, these will always be null
        Integer headCount = null; // for this release, these will always be null
        OrganisationSize organisationSize = organisation.getOrganisationSize();

        model.addAttribute("model", new FinanceChecksViabilityViewModel(organisationName, leadPartnerOrganisation,
                totalCosts, percentageGrant, fundingSought, otherPublicSectorFunding, contributionToProject,
                companyRegistrationNumber, turnover, headCount, organisationSize, projectId));
    }

    private void populateForm(Long projectId, Long organisationId, Model model) {

        ViabilityResource viability = financeService.getViability(projectId, organisationId);
        boolean creditReportConfirmed = false;

        FinanceChecksViabilityForm form = new FinanceChecksViabilityForm(creditReportConfirmed, viability.getViabilityStatus());
        model.addAttribute("form", form);
    }

    private int toZeroScaleInt(BigDecimal value) {
        return value.setScale(0, HALF_EVEN).intValueExact();
    }
}
