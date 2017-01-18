package org.innovateuk.ifs.project.eligibility.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller serves the Eligibility page where internal users can confirm the viability of a partner organisation's
 * financial position on a Project
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility")
public class FinanceChecksEligibilityController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private OrganisationService organisationService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(method = GET)
    public String viewEligibility(@PathVariable("projectId") Long projectId,
                                @PathVariable("organisationId") Long organisationId, Model model) {

        return doViewEligibility(projectId, organisationId, model);
    }

    private String doViewEligibility(Long projectId, Long organisationId, Model model) {
        model.addAttribute("model", getViewModel(projectId, organisationId, model));
        return "project/financecheck/eligibility";
    }

    private FinanceChecksEligibilityViewModel getViewModel(Long projectId, Long organisationId, Model model) {


        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        FinanceCheckEligibilityResource eligibility = financeCheckService.getFinanceCheckEligibilityDetails(projectId, organisationId);

        return new FinanceChecksEligibilityViewModel(eligibility, organisation.getName(), project.getName(), application.getFormattedId(), leadPartnerOrganisation, project.getId());
    }
}
