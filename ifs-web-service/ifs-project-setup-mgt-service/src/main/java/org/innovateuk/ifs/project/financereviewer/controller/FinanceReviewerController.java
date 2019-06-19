package org.innovateuk.ifs.project.financereviewer.controller;

import org.innovateuk.ifs.project.financereviewer.form.FinanceReviewerForm;
import org.innovateuk.ifs.project.financereviewer.service.FinanceReviewerRestService;
import org.innovateuk.ifs.project.financereviewer.viewmodel.FinanceReviewerViewModel;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/finance-reviewer")
public class FinanceReviewerController {

    @Autowired
    private FinanceReviewerRestService financeReviewerRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @GetMapping
    public String financeReviewer(@ModelAttribute(value = "form", binding = false) FinanceReviewerForm form,
                                  BindingResult bindingResult,
                                  long projectId,
                                  Model model) {
        model.addAttribute("model", new FinanceReviewerViewModel(projectRestService.getProjectById(projectId).getSuccess(),
                financeReviewerRestService.findFinanceUsers().getSuccess()));
        return "project/finance-reviewer";
    }

}
