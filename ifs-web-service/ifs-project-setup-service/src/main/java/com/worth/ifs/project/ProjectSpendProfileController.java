package com.worth.ifs.project;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.form.SpendProfileForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
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
 * This controller will handle all requests that are related to spend profile.
 */
@Controller
@RequestMapping("/project/{projectId}/partner-organisation/{organisationId}/spend-profile")
public class ProjectSpendProfileController {

    public static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @RequestMapping(method = GET)
    public String viewSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectSpendProfileViewModel viewModel = populateSpendProfileViewModel(projectId, organisationId);
        model.addAttribute("model", viewModel);
        return "project/spend-profile";
    }

    // TODO - Optimise the common functionality in viewSpendProfile and editSpendProfile
    @RequestMapping(value = "/edit", method = GET)
    public String editSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {




        ProjectSpendProfileViewModel viewModel = populateSpendProfileViewModel(projectId, organisationId);

        model.addAttribute("model", viewModel);

        SpendProfileForm form = new SpendProfileForm();
        form.setTable(viewModel.getTable());
        model.addAttribute(FORM_ATTR_NAME, form);

        return "project/spend-profile/edit";
    }

    // TODO - Optimise the common functionality in saveSpendProfile and confirmSpendProfile
    @RequestMapping(value = "/edit", method = POST)
    public String saveSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute(FORM_ATTR_NAME) SpendProfileForm form,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ServiceResult<Void> result = projectFinanceService.saveSpendProfile(projectId, organisationId, form.getTable());
        if (result.isFailure()) {

            // If this model attribute is set, it means there are some categories where the totals don't match
            model.addAttribute("errorCategories", result.getFailure().getErrors());
        }

        // We go the the same view, irrespective of whether there were some categories in error or not, to enable
        // the user to mark the Spend Profile as complete

        return "project/spend-profile/edit";
    }

    @RequestMapping(value = "/confirm", method = POST)
    public String confirmSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute(FORM_ATTR_NAME) SpendProfileForm form,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ServiceResult<Void> result = projectFinanceService.saveSpendProfile(projectId, organisationId, form.getTable());

        if (result.isFailure()) {
            model.addAttribute("errorCategories", result.getFailure().getErrors());
            return "project/spend-profile/edit";
        }

        return "project/spend-profile/confirm";
    }



    private ProjectSpendProfileViewModel populateSpendProfileViewModel(final Long projectId, final Long organisationId) {

        ProjectResource projectResource = projectService.getById(projectId);
        SpendProfileTableResource table = projectFinanceService.getSpendProfileTable(projectId, organisationId);
        return new ProjectSpendProfileViewModel(projectResource, table);
    }
}
