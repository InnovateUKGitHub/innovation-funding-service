package org.innovateuk.ifs.project.viability.controller;

import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.OrganisationSizeService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.service.OrganisationDetailsRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.ViabilityResource;
import org.innovateuk.ifs.project.viability.form.FinanceChecksViabilityForm;
import org.innovateuk.ifs.project.viability.viewmodel.FinanceChecksViabilityViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.math.RoundingMode.HALF_EVEN;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller serves the Viability page where internal users can confirm the viability of a partner organisation's
 * financial position on a Project
 */
@Controller
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/viability")
public class FinanceChecksViabilityController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationDetailsRestService organisationDetailsService;

    @Autowired
    private ProjectFinanceService financeService;

    @Autowired
    private OrganisationSizeService organisationSizeService;

    @RequestMapping(method = GET)
    public String viewViability(@PathVariable("projectId") Long projectId,
                                @PathVariable("organisationId") Long organisationId, Model model) {

        return doViewViability(projectId, organisationId, model, getViabilityForm(projectId, organisationId));
    }

    @RequestMapping(method = POST, params = "save-and-continue")
    public String saveAndContinue(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute("form") FinanceChecksViabilityForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model) {

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        return doSaveViability(projectId, organisationId, Viability.REVIEW, form, validationHandler, model, successView);
    }

    @RequestMapping(method = POST, params = "confirm-viability")
    public String confirmViability(@PathVariable("projectId") Long projectId,
                                   @PathVariable("organisationId") Long organisationId,
                                   @ModelAttribute("form") FinanceChecksViabilityForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   Model model) {

        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/viability";

        return doSaveViability(projectId, organisationId, Viability.APPROVED, form, validationHandler, model, successView);
    }

    private String doSaveViability(Long projectId, Long organisationId, Viability viability, FinanceChecksViabilityForm form,
                                   ValidationHandler validationHandler, Model model, Supplier<String> successView) {

        Supplier<String> failureView = () -> doViewViability(projectId, organisationId, model, form);

        ServiceResult<Void> saveCreditReportResult = financeService.saveCreditReportConfirmed(projectId, organisationId, form.isCreditReportConfirmed());

        return validationHandler.
               addAnyErrors(saveCreditReportResult).
               failNowOrSucceedWith(failureView, () -> {

            ViabilityRagStatus statusToSend = getRagStatusDependantOnConfirmationCheckboxSelection(form);

            ServiceResult<Void> saveViabilityResult = financeService.saveViability(projectId, organisationId, viability, statusToSend);

            return validationHandler.
                   addAnyErrors(saveViabilityResult).
                   failNowOrSucceedWith(failureView, successView);
        });
    }

    private ViabilityRagStatus getRagStatusDependantOnConfirmationCheckboxSelection(FinanceChecksViabilityForm form) {
        ViabilityRagStatus statusToSend;

        if (form.isConfirmViabilityChecked()) {
            statusToSend = form.getRagStatus();
        } else {
            statusToSend = ViabilityRagStatus.UNSET;
        }
        return statusToSend;
    }

    private String doViewViability(Long projectId, Long organisationId, Model model, FinanceChecksViabilityForm form) {
        model.addAttribute("model", getViewModel(projectId, organisationId, model));
        model.addAttribute("form", form);
        return "project/financecheck/viability";
    }

    private FinanceChecksViabilityViewModel getViewModel(Long projectId, Long organisationId, Model model) {

        ViabilityResource viability = financeService.getViability(projectId, organisationId);
        boolean viabilityConfirmed = viability.getViability() == Viability.APPROVED;

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

        Long headCount = null;
        RestResult<Long> headCountResult = organisationDetailsService.getHeadCount(projectService.getById(projectId).getApplication(), organisationId);
        if (headCountResult.isSuccess()) {
            headCount = headCountResult.getSuccessObject();
        }
        Long turnover = null;
        RestResult<Long> turnOverResult = organisationDetailsService.getTurnover(projectService.getById(projectId).getApplication(), organisationId);
        if (turnOverResult.isSuccess()) {
            turnover = turnOverResult.getSuccessObject();
        }

        String approver = viability.getViabilityApprovalUserFirstName() + " " + viability.getViabilityApprovalUserLastName();
        LocalDate approvalDate = viability.getViabilityApprovalDate();

        List<OrganisationSizeResource> sizes = organisationSizeService.getOrganisationSizes();
        Optional<OrganisationSizeResource> organisationSizeResource = sizes.stream().filter(size -> size.getId().equals(financesForOrganisation.getOrganisationSize())).findAny();
        String organisationSizeDescription = organisationSizeResource.map(OrganisationSizeResource::getDescription).orElse(null);
        return new FinanceChecksViabilityViewModel(organisationName, leadPartnerOrganisation,
                totalCosts, percentageGrant, fundingSought, otherPublicSectorFunding, contributionToProject,
                companyRegistrationNumber, turnover, headCount, projectId, viabilityConfirmed,
                viabilityConfirmed, approver, approvalDate, organisationId,
                organisationSizeDescription);
    }

    private FinanceChecksViabilityForm getViabilityForm(Long projectId, Long organisationId) {

        ViabilityResource viability = financeService.getViability(projectId, organisationId);
        boolean creditReportConfirmed = financeService.isCreditReportConfirmed(projectId, organisationId);
        boolean confirmViabilityChecked = viability.getViabilityRagStatus() != ViabilityRagStatus.UNSET;

        return new FinanceChecksViabilityForm(creditReportConfirmed, viability.getViabilityRagStatus(), confirmViabilityChecked);
    }

    private int toZeroScaleInt(BigDecimal value) {
        return value.setScale(0, HALF_EVEN).intValueExact();
    }
}
