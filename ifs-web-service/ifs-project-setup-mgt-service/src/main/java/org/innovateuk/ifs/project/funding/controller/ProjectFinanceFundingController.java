package org.innovateuk.ifs.project.funding.controller;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;


import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.validation.Valid;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimAmount;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.funding.form.ProjectFinanceFundingForm;
import org.innovateuk.ifs.project.funding.form.ProjectFinancePartnerFundingForm;
import org.innovateuk.ifs.project.funding.viewmodel.ProjectFinanceFundingViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/project/{projectId}/funding")
@SecuredBySpring(value = "PROJECT_FINANCE_FUNDING", description = "Project finance team can amend funding levels in project setup.")
@PreAuthorize("hasAuthority('project_finance')")
public class ProjectFinanceFundingController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ProjectFinanceRowRestService financeRowRestService;

    @GetMapping
    public String viewFundingLevels(@ModelAttribute(name = "form", binding = false) ProjectFinanceFundingForm form,
                                    @PathVariable long projectId,
                                    Model model) {
        List<ProjectFinanceResource> finances = projectFinanceRestService.getProjectFinances(projectId).getSuccess();
        form.setPartners(finances.stream()
                .collect(toMap(ProjectFinanceResource::getOrganisation,
                        pf -> new ProjectFinancePartnerFundingForm(pf.getTotalFundingSought()))));
        return viewFunding(projectId, finances, model);
    }

    @PostMapping
    public String saveFundingLevels(@Valid @ModelAttribute("form") ProjectFinanceFundingForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    @PathVariable long projectId,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        List<ProjectFinanceResource> finances = projectFinanceRestService.getProjectFinances(projectId).getSuccess();
        Supplier<String> failureView = () -> viewFunding(projectId, finances, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saveFundingLevels(finances, form));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                redirectAttributes.addFlashAttribute("showFundingAmountMessage", true);
                return format("redirect:/project/%d/finance-check-overview", projectId);
            });
        });
    }

    private String viewFunding(long projectId, List<ProjectFinanceResource> finances, Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        model.addAttribute("model", new ProjectFinanceFundingViewModel(project, finances));
        return "project/financecheck/funding";
    }


    private ValidationMessages saveFundingLevels(List<ProjectFinanceResource> financeList, ProjectFinanceFundingForm form) {
        Map<Long, ProjectFinanceResource> finances = financeList
                .stream()
                .collect(toMap(ProjectFinanceResource::getOrganisation, Function.identity()));

        ValidationMessages validationMessages = new ValidationMessages();
        form.getPartners().entrySet().stream().forEach(entry -> {
            ProjectFinanceResource finance = finances.get(entry.getKey());
            GrantClaimAmount grantClaim = (GrantClaimAmount) finance.getGrantClaim();
            grantClaim.setAmount(entry.getValue().getFunding());
            validationMessages.addAll(financeRowRestService.update(grantClaim).getSuccess());
        });

        return validationMessages;
    }
}
