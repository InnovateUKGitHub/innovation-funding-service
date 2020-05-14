package org.innovateuk.ifs.project.viability.controller;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.FinancialYearAccountsResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.ViabilityResource;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.viability.form.FinanceChecksViabilityForm;
import org.innovateuk.ifs.project.viability.viewmodel.FinanceChecksViabilityViewModel;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.math.RoundingMode.HALF_EVEN;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.VIABILITY_CHECKS_NOT_APPLICABLE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This controller serves the Viability page where internal users can confirm the viability of a partner organisation's
 * financial position on a Project
 */
@Controller
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'external_finance')")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = FinanceChecksViabilityController.class)
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/viability")
public class FinanceChecksViabilityController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectFinanceService financeService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @GetMapping
    public String viewViability(@PathVariable("projectId") Long projectId,
                                @PathVariable("organisationId") Long organisationId, Model model) {

        return doViewViability(projectId, organisationId, model, getViabilityForm(projectId, organisationId));
    }

    @PostMapping(params = "save-and-continue")
    public String saveAndContinue(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute("form") FinanceChecksViabilityForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model) {

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        return doSaveViability(projectId, organisationId, ViabilityState.REVIEW, form, validationHandler, model, successView);
    }

    @PostMapping(params = "confirm-viability")
    public String confirmViability(@PathVariable("projectId") Long projectId,
                                   @PathVariable("organisationId") Long organisationId,
                                   @ModelAttribute("form") FinanceChecksViabilityForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   Model model) {

        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/viability";

        return doSaveViability(projectId, organisationId, ViabilityState.APPROVED, form, validationHandler, model, successView);
    }

    private String doSaveViability(Long projectId, Long organisationId, ViabilityState viability, FinanceChecksViabilityForm form,
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
        model.addAttribute("model", getViewModel(projectId, organisationId));
        model.addAttribute("form", form);

        return "project/financecheck/viability";
    }

    private FinanceChecksViabilityViewModel getViewModel(Long projectId, Long organisationId) {

        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        ViabilityResource viability = financeService.getViability(projectId, organisationId);
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        if (viability.getViability().isNotApplicable()) {
            throw new ObjectNotFoundException(VIABILITY_CHECKS_NOT_APPLICABLE.getErrorKey(), singletonList(organisation.getName()));
        }

        boolean viabilityConfirmed = viability.getViability() == ViabilityState.APPROVED;

        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        List<ProjectFinanceResource> projectFinances = financeService.getProjectFinances(projectId);
        ProjectFinanceResource financesForOrganisation = simpleFindFirst(projectFinances,
                finance -> finance.getOrganisation().equals(organisationId)).get();

        String organisationName = organisation.getName();
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        Integer totalCosts = toZeroScaleInt(financesForOrganisation.getTotal());
        BigDecimal percentageGrant = financesForOrganisation.getGrantClaimPercentage();
        Integer fundingSought = toZeroScaleInt(financesForOrganisation.getTotalFundingSought());
        Integer otherPublicSectorFunding = toZeroScaleInt(financesForOrganisation.getTotalOtherFunding());
        Integer contributionToProject = toZeroScaleInt(financesForOrganisation.getTotalContribution());

        String companyRegistrationNumber = organisation.getCompaniesHouseNumber();

        String approver = viability.getViabilityApprovalUserFirstName() + " " + viability.getViabilityApprovalUserLastName();
        LocalDate approvalDate = viability.getViabilityApprovalDate();
        String organisationSizeDescription = Optional.ofNullable(financesForOrganisation.getOrganisationSize()).map
                (OrganisationSize::getDescription).orElse(null);

        return new FinanceChecksViabilityViewModel(project,
                competition,
                organisationName,
                leadPartnerOrganisation,
                totalCosts,
                percentageGrant,
                fundingSought,
                otherPublicSectorFunding,
                contributionToProject,
                companyRegistrationNumber,
                ofNullable(financesForOrganisation.getFinancialYearAccounts())
                        .map(FinancialYearAccountsResource::getTurnover)
                        .map(BigDecimal::longValue)
                        .orElse(null),
                ofNullable(financesForOrganisation.getFinancialYearAccounts())
                        .map(FinancialYearAccountsResource::getEmployees)
                        .orElse(null),
                projectId,
                viabilityConfirmed,
                viabilityConfirmed,
                approver,
                approvalDate,
                organisationId,
                organisationSizeDescription,
                projectFinances);
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