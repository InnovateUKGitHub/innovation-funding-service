package com.worth.ifs.project.financecheck.controller;

import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.financecheck.FinanceCheckService;
import com.worth.ifs.project.financecheck.form.FinanceCheckForm;
import com.worth.ifs.project.financecheck.viewmodel.FinanceCheckViewModel;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller is for allowing internal users to view and update application finances entered by applicants
 */
@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/finance-check")
public class FinanceCheckController {
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private FinanceCheckService financeCheckService;

    @RequestMapping(method = GET)
    public String view(@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") Long organisationId, @ModelAttribute(FORM_ATTR_NAME) FinanceCheckForm form, @ModelAttribute("loggedInUser") UserResource loggedInUser, Model model){
        ProjectOrganisationCompositeId key = new ProjectOrganisationCompositeId(projectId, organisationId);
        FinanceCheckResource financeCheckResource = new FinanceCheckResource();
        populateExitingFinanceCheckDetailsInForm(financeCheckResource, form);
        return doViewFinanceCheckForm(model);
    }

    @RequestMapping(method = POST)
    public String update(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId,  Model model, @ModelAttribute(FORM_ATTR_NAME) FinanceCheckForm form){
        financeCheckService.update(form.getFinanceCheckResource());
        return "redirect:/project/" + projectId + "/organisation/" + organisationId + "/finance-check";
    }

    private void populateExitingFinanceCheckDetailsInForm(FinanceCheckResource financeCheckResource, FinanceCheckForm form){
        form.setFinanceCheckResource(financeCheckResource);
    }

    private String doViewFinanceCheckForm(Model model){
        FinanceCheckViewModel financeCheckViewModel = new FinanceCheckViewModel();
        model.addAttribute("model", financeCheckViewModel);
        return "project/finance-check";
    }
}