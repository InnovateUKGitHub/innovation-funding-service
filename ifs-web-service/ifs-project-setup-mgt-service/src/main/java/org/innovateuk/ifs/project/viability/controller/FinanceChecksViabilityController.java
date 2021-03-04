package org.innovateuk.ifs.project.viability.controller;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.FinancialYearAccountsResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.ViabilityResource;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.viability.form.FinanceChecksViabilityForm;
import org.innovateuk.ifs.project.viability.viewmodel.FinanceChecksViabilityViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
@PreAuthorize("hasAnyAuthority('comp_admin', 'external_finance')")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = FinanceChecksViabilityController.class)
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/viability")
public class FinanceChecksViabilityController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private GrantOfferLetterService grantOfferLetterService;

    @GetMapping
    public String viewViability(@PathVariable("projectId") Long projectId,
                                @PathVariable("organisationId") Long organisationId, Model model, UserResource user) {

        return doViewViability(projectId, organisationId, model, getViabilityForm(projectId, organisationId), user);
    }

    @PostMapping(params = "save-and-continue")
    public String saveAndContinue(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute("form") FinanceChecksViabilityForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model,
                                  UserResource user) {

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        return doSaveViability(projectId, organisationId, ViabilityState.REVIEW, form, validationHandler, model, user, successView);
    }

    @PostMapping(params = "reset-viability")
    public String resetViability(@PathVariable("projectId") Long projectId,
                                 @PathVariable("organisationId") Long organisationId,
                                 @ModelAttribute("form") FinanceChecksViabilityForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 UserResource user) {

        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/viability";

        Supplier<String> failureView = () -> doViewViability(projectId, organisationId, model, form, user);

        if (StringUtils.isEmpty(form.getRetractionReason())) {
            bindingResult.addError(new FieldError("form", "retractionReason", "Enter a reason for the reset."));
            return failureView.get();
        }

        RestResult<Void> resetViabilityResult = financeCheckRestService.resetViability(projectId, organisationId, form.getRetractionReason());


        return validationHandler.
                addAnyErrors(resetViabilityResult).
                failNowOrSucceedWith(failureView, successView);
    }

    @PostMapping(params = "confirm-viability")
    public String confirmViability(@PathVariable("projectId") Long projectId,
                                   @PathVariable("organisationId") Long organisationId,
                                   @ModelAttribute("form") FinanceChecksViabilityForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   Model model,
                                   UserResource user) {

        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/viability";

        return doSaveViability(projectId, organisationId, ViabilityState.APPROVED, form, validationHandler, model, user, successView);
    }

    private String doSaveViability(Long projectId, Long organisationId, ViabilityState viability, FinanceChecksViabilityForm form,
                                   ValidationHandler validationHandler, Model model, UserResource user, Supplier<String> successView) {

        Supplier<String> failureView = () -> doViewViability(projectId, organisationId, model, form, user);

        RestResult<Void> saveCreditReportResult = projectFinanceRestService.saveCreditReportConfirmed(projectId, organisationId, form.isCreditReportConfirmed());

        return validationHandler.
                addAnyErrors(saveCreditReportResult).
                failNowOrSucceedWith(failureView, () -> {

                    ViabilityRagStatus statusToSend = getRagStatusDependantOnConfirmationCheckboxSelection(form);

                    RestResult<Void> saveViabilityResult = financeCheckRestService.saveViability(projectId, organisationId, viability, statusToSend);

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

    private String doViewViability(Long projectId, Long organisationId, Model model, FinanceChecksViabilityForm form, UserResource user) {
        model.addAttribute("model", getViewModel(projectId, organisationId, user));
        model.addAttribute("form", form);

        return "project/financecheck/viability";
    }

    private FinanceChecksViabilityViewModel getViewModel(Long projectId, Long organisationId, UserResource user) {

        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        ViabilityResource viability = financeCheckRestService.getViability(projectId, organisationId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        if (viability.getViability().isNotApplicable()) {
            throw new ObjectNotFoundException(VIABILITY_CHECKS_NOT_APPLICABLE.getErrorKey(), singletonList(organisation.getName()));
        }

        ViabilityState viabilityState = viability.getViability();
        boolean viabilityConfirmed = ViabilityState.APPROVED == viabilityState;

        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        List<ProjectFinanceResource> projectFinances = projectFinanceRestService.getProjectFinances(projectId).getSuccess();
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

        String approver = name(viability.getViabilityApprovalUserFirstName(), viability.getViabilityApprovalUserLastName());
        String resetter = name(viability.getViabilityResetUserFirstName(), viability.getViabilityResetUserLastName());
        LocalDate approvalDate = viability.getViabilityApprovalDate();
        LocalDate resetDate = viability.getViabilityResetDate();
        String organisationSizeDescription = Optional.ofNullable(financesForOrganisation.getOrganisationSize()).map
                (OrganisationSize::getDescription).orElse(null);

        boolean resetableGolState = false;
        if (user.isInternalUser()) {
            GrantOfferLetterStateResource golState = grantOfferLetterService.getGrantOfferLetterState(projectId).getSuccess();
            resetableGolState = golState.getState() != GrantOfferLetterState.APPROVED;
        }

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
                viabilityState,
                viabilityConfirmed,
                approver,
                approvalDate,
                resetter,
                resetDate,
                organisationId,
                organisationSizeDescription,
                projectFinances,
                resetableGolState);
    }

    private String name(String firstName, String lastName) {
        if (firstName == null) {
            return null;
        }
        return firstName + " " + lastName;
    }

    private FinanceChecksViabilityForm getViabilityForm(Long projectId, Long organisationId) {

        ViabilityResource viability = financeCheckRestService.getViability(projectId, organisationId).getSuccess();
        boolean creditReportConfirmed = projectFinanceRestService.isCreditReportConfirmed(projectId, organisationId).getSuccess();
        boolean confirmViabilityChecked = viability.getViabilityRagStatus() != ViabilityRagStatus.UNSET;

        return new FinanceChecksViabilityForm(creditReportConfirmed, viability.getViabilityRagStatus(), confirmViabilityChecked);
    }

    private int toZeroScaleInt(BigDecimal value) {
        return value.setScale(0, HALF_EVEN).intValueExact();
    }
}