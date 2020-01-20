package org.innovateuk.ifs.project.funding.level.controller;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.funding.level.form.ProjectFinanceFundingLevelForm;
import org.innovateuk.ifs.project.funding.level.form.ProjectFinancePartnerFundingLevelForm;
import org.innovateuk.ifs.project.funding.level.viewmodel.ProjectFinanceFundingLevelViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/project/{projectId}/funding-level")
@SecuredBySpring(value = "PROJECT_FINANCE_FUNDING", description = "Project finance team can amend funding levels in project setup.")
@PreAuthorize("hasAuthority('project_finance')")
public class ProjectFinanceFundingLevelController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ProjectFinanceRowRestService financeRowRestService;

    @GetMapping
    public String viewFundingLevels(@ModelAttribute(name = "form", binding = false) ProjectFinanceFundingLevelForm form,
                                    @PathVariable long projectId,
                                    Model model) {
        List<ProjectFinanceResource> finances = projectFinanceRestService.getProjectFinances(projectId).getSuccess();
        form.setPartners(finances.stream()
                .collect(toMap(ProjectFinanceResource::getOrganisation,
                        pf -> new ProjectFinancePartnerFundingLevelForm(pf.getGrantClaimPercentage()))));
        return viewFunding(projectId, finances, model);
    }

    @PostMapping
    public String saveFundingLevels(@Valid @ModelAttribute("form") ProjectFinanceFundingLevelForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    @PathVariable long projectId,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        List<ProjectFinanceResource> finances = projectFinanceRestService.getProjectFinances(projectId).getSuccess();
        Supplier<String> failureView = () -> viewFunding(projectId, finances, model);

        if (bindingResult.hasErrors()) {
            return failureView.get();
        }

        validateMaximumFundingLevels(bindingResult, finances, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saveFundingLevels(finances, form));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                redirectAttributes.addFlashAttribute("showFundingLevelMessage", true);
                return format("redirect:/project/%d/finance-check-overview", projectId);
            });
        });
    }

    private String viewFunding(long projectId, List<ProjectFinanceResource> finances, Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource lead = projectRestService.getLeadOrganisationByProject(projectId).getSuccess();
        model.addAttribute("model", new ProjectFinanceFundingLevelViewModel(project, finances, lead));
        return "project/financecheck/funding-level";
    }

    private ValidationMessages saveFundingLevels(List<ProjectFinanceResource> finances, ProjectFinanceFundingLevelForm form) {
        ValidationMessages messages = new ValidationMessages();
        finances.forEach(finance -> {
            BigDecimal fundingLevel = form.getPartners().get(finance.getOrganisation()).getFundingLevel();
            GrantClaimPercentage grantClaim = (GrantClaimPercentage) finance.getGrantClaim();
            grantClaim.setPercentage(fundingLevel);
            messages.addAll(financeRowRestService.update(grantClaim).getSuccess());
        });
        return messages;
    }

    private void validateMaximumFundingLevels(BindingResult bindingResult, List<ProjectFinanceResource> finances, ProjectFinanceFundingLevelForm form) {

        finances.forEach(finance -> {
            BigDecimal fundingLevel = form.getPartners().get(finance.getOrganisation()).getFundingLevel();
            if (finance.getMaximumFundingLevel() < fundingLevel.intValue()) {
                bindingResult.rejectValue(String.format("partners[%d].fundingLevel", finance.getOrganisation()),
                        "validation.finance.grant.claim.percentage.max",
                        new String[]{String.valueOf(finance.getMaximumFundingLevel())},
                        "");
            }
        });
    }
}
