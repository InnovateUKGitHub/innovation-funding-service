package org.innovateuk.ifs.project.financereviewer.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.financereviewer.form.FinanceReviewerForm;
import org.innovateuk.ifs.project.financereviewer.service.FinanceReviewerRestService;
import org.innovateuk.ifs.project.financereviewer.viewmodel.FinanceReviewerViewModel;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.function.Supplier;


@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/finance-reviewer")
@SecuredBySpring(value = "FINANCE_REVIEWER",
        description = "Only project finance view and change projects finance reviewer")
@PreAuthorize("hasAuthority('project_finance')")
public class FinanceReviewerController {

    @Autowired
    private FinanceReviewerRestService financeReviewerRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @GetMapping
    public String financeReviewer(@ModelAttribute(value = "form", binding = false) FinanceReviewerForm form,
                                  BindingResult bindingResult,
                                  @PathVariable long projectId,
                                  Model model) {
        model.addAttribute("model", new FinanceReviewerViewModel(projectRestService.getProjectById(projectId).getSuccess(),
                financeReviewerRestService.findFinanceUsers().getSuccess()));
        return "project/finance-reviewer";
    }

    @PostMapping
    public String assignFinanceReviewer(@Valid @ModelAttribute(value = "form") FinanceReviewerForm form,
                                        BindingResult bindingResult,
                                        ValidationHandler validationHandler,
                                        @PathVariable long projectId,
                                        @PathVariable long competitionId,
                                        Model model) {
        Supplier<String> failureView = () -> financeReviewer(form, bindingResult, projectId, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(financeReviewerRestService.assignFinanceReviewerToProject(form.getUserId(), projectId));
            return validationHandler.failNowOrSucceedWith(failureView, () ->
                String.format("redirect:/competition/%d/project/%d/details?displayFinanceReviewerSuccess=true", competitionId, projectId)
            );
        });
    }

}
