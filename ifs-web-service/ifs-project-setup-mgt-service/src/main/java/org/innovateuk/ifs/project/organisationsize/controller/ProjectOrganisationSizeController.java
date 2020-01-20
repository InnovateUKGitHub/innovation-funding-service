package org.innovateuk.ifs.project.organisationsize.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.organisationsize.form.ProjectOrganisationSizeWithoutGrowthTableForm;
import org.innovateuk.ifs.project.organisationsize.viewmodel.ProjectOrganisationSizeViewModel;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.yourorganisation.service.YourOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ProjectOrganisationSizeController.class)
public class ProjectOrganisationSizeController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private YourOrganisationRestService yourOrganisationRestService;

    @GetMapping("/edit")
    public String editOrganisationSize(@ModelAttribute(value = "form", binding = false) ProjectOrganisationSizeWithoutGrowthTableForm form,
                                       BindingResult bindingResult,
                                       @PathVariable long projectId,
                                       @PathVariable long organisationId,
                                       Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationFinancesWithoutGrowthTableResource finances = yourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(project.getApplication(), organisationId).getSuccess();

        model.addAttribute("model", new ProjectOrganisationSizeViewModel(project, organisation.getName(), finances.getOrganisationSize(), finances.getTurnover(), finances.getHeadCount()));
        model.addAttribute("form", form);

        return "project/edit-organisation-size";
    }

//    @PostMapping
//    @PreAuthorize("hasAuthority('project_finance')")
//    @SecuredBySpring(value = "VIEW_EDIT_ORGANISATION", description = "Internal users can view edit organisation size")
//    public String editOrganisationSize(ProjectOrganisationSizeWithoutGrowthTableForm form,
//                                       @PathVariable long projectId,
//                                       @PathVariable long organisationId,
//                                       UserResource loggedInUser,
//                                       Model model) {
//        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
//        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
//        OrganisationFinancesWithoutGrowthTableResource finances = yourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(project.getApplication(), organisationId).getSuccess();
//
//        model.addAttribute("model", new ProjectOrganisationSizeViewModel(project, organisation.getName(), finances.getTurnover(), finances.getHeadCount()));
//        model.addAttribute("form", form);
//
//        return "project/edit-organisation-size";
//    }

}
