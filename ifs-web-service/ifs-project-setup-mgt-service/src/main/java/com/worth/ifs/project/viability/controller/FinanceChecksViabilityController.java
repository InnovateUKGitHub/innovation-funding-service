package com.worth.ifs.project.viability.controller;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.viability.viewmodel.FinanceChecksViabilityViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

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

    @RequestMapping(method = GET)
    public String viewViability(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId, Model model) {

        populateViewModel(projectId, organisationId, model);
        return "project/financecheck/viability";
    }

    private void populateViewModel(Long projectId, Long organisationId, Model model) {

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);

        String organisationName = organisation.getName();
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        Integer totalCosts = 286283;
        Integer percentageGrant = 55;
        Integer fundingSought = 180339;
        Integer otherPublicSectorFunding = 20000;
        Integer contributionToProject = 85885;

        String companyRegistrationNumber = organisation.getCompanyHouseNumber();
        Integer turnover = null;
        Integer headCount = null;
        OrganisationSize organisationSize = organisation.getOrganisationSize();
        boolean creditReportVerified = false;
        boolean viabilityApproved = false;

        model.addAttribute("model", new FinanceChecksViabilityViewModel(organisationName, leadPartnerOrganisation,
                totalCosts, percentageGrant, fundingSought, otherPublicSectorFunding, contributionToProject,
                companyRegistrationNumber, turnover, headCount, organisationSize, creditReportVerified, viabilityApproved));
    }
}
