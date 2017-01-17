package org.innovateuk.ifs.project.eligibility.controller;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.project.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.resource.Eligibility;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityStatus;
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
 * This controller serves the Eligibility page where internal users can confirm the eligibility of a partner organisation's
 * financial position on a Project
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility")
public class FinanceChecksEligibilityController {

    @Autowired
    private ProjectFinanceService financeService;

    @RequestMapping(method = GET)
    public String viewEligibility(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId, Model model) {

        return doViewEligibility(projectId, organisationId, model, null);
    }

    private String doViewEligibility(Long projectId, Long organisationId, Model model, FinanceChecksEligibilityForm form) {

        EligibilityResource eligibility = financeService.getEligibility(projectId, organisationId);

        if (form == null) {
            form = getEligibilityForm(eligibility);
        }

        model.addAttribute("model", getViewModel(projectId, eligibility));
        model.addAttribute("form", form);
        return "project/financecheck/eligibility";
    }

    private FinanceChecksEligibilityForm getEligibilityForm(EligibilityResource eligibility) {

        boolean confirmEligibilityChecked = eligibility.getEligibilityStatus() != EligibilityStatus.UNSET;

        return new FinanceChecksEligibilityForm(eligibility.getEligibilityStatus(), confirmEligibilityChecked);
    }

    private FinanceChecksEligibilityViewModel getViewModel(Long projectId, EligibilityResource eligibility) {

        boolean eligibilityApproved = eligibility.getEligibility() == Eligibility.APPROVED;

        return new FinanceChecksEligibilityViewModel(projectId, eligibilityApproved, eligibility.getEligibilityStatus(),
                eligibility.getEligibilityApprovalUserFirstName(), eligibility.getEligibilityApprovalUserLastName(),
                eligibility.getEligibilityApprovalDate());

    }

    @RequestMapping(method = POST, params = "confirm-eligibility")
    public String confirmEligibility(@PathVariable("projectId") Long projectId,
                                     @PathVariable("organisationId") Long organisationId,
                                     @ModelAttribute("form") FinanceChecksEligibilityForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     Model model) {

        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility";

        return doSaveEligibility(projectId, organisationId, Eligibility.APPROVED, form, validationHandler, model, successView);
    }

    @RequestMapping(method = POST, params = "save-and-continue")
    public String saveAndContinue(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute("form") FinanceChecksEligibilityForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model) {

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        return doSaveEligibility(projectId, organisationId, Eligibility.REVIEW, form, validationHandler, model, successView);
    }

    private String doSaveEligibility(Long projectId, Long organisationId, Eligibility eligibility, FinanceChecksEligibilityForm form,
                                   ValidationHandler validationHandler, Model model, Supplier<String> successView) {

        Supplier<String> failureView = () -> doViewEligibility(projectId, organisationId, model, form);

        EligibilityStatus statusToSend = getRagStatus(form);

        ServiceResult<Void> saveEligibilityResult = financeService.saveEligibility(projectId, organisationId, eligibility, statusToSend);

        return validationHandler
                .addAnyErrors(saveEligibilityResult)
                .failNowOrSucceedWith(failureView, successView);

    }

    private EligibilityStatus getRagStatus(FinanceChecksEligibilityForm form) {
        EligibilityStatus statusToSend;

        if (form.isConfirmEligibilityChecked()) {
            statusToSend = form.getEligibilityStatus();
        } else {
            statusToSend = EligibilityStatus.UNSET;
        }
        return statusToSend;
    }
}
