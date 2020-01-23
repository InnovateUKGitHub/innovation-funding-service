package org.innovateuk.ifs.project.organisationsize.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationsize.viewmodel.ProjectOrganisationSizeViewModel;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/without-growth-table")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ProjectOrganisationSizeWithoutGrowthTableController.class)
public class ProjectOrganisationSizeWithoutGrowthTableController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private YourOrganisationWithoutGrowthTableFormPopulator withoutGrowthTableFormPopulator;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    public String editOrganisationSize(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            Model model) {

        model.addAttribute("model", getViewModel(projectId, organisationId));
        model.addAttribute("form", formRequest(projectId, organisationId));

        return "project/edit-organisation-size-without-growth-table";
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    @SecuredBySpring(value = "UPDATE_ORGANISATION_FUNDING_DETAILS", description = "Internal users can update organisation funding details")
    public String saveWithGrowthTable(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            @ModelAttribute YourOrganisationWithoutGrowthTableForm form) {

        updateYourOrganisationWithoutGrowthTable(projectId, organisationId, form);
        return redirectToOrganisationDetails(projectId, organisationId);
    }


    private ProjectOrganisationSizeViewModel getViewModel(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationFinancesWithoutGrowthTableResource financesWithoutGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId).getSuccess();
        return new ProjectOrganisationSizeViewModel(project,
                organisation.getName(),
                organisationId,
                financesWithoutGrowthTable.getOrganisationSize(),
                financesWithoutGrowthTable.getTurnover(),
                financesWithoutGrowthTable.getHeadCount(),
                false,
                false,
                false,
                false);
    }

    private YourOrganisationWithoutGrowthTableForm formRequest(long projectId, long organisationId) {
        OrganisationFinancesWithoutGrowthTableResource financesWithoutGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId).getSuccess();
        return withoutGrowthTableFormPopulator.populate(financesWithoutGrowthTable);
    }

    private void updateYourOrganisationWithoutGrowthTable(long projectId,
                                                       long organisationId,
                                                       YourOrganisationWithoutGrowthTableForm form) {

        OrganisationFinancesWithoutGrowthTableResource finances = new OrganisationFinancesWithoutGrowthTableResource(
                form.getOrganisationSize(),
                form.getTurnover(),
                form.getHeadCount());

        projectYourOrganisationRestService.updateOrganisationFinancesWithoutGrowthTable(projectId, organisationId, finances).getSuccess();
    }

    private String redirectToOrganisationDetails(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        return "redirect:" + String.format("/competition/%d/project/%d/organisation/%d/details", project.getCompetition(), projectId, organisationId);
    }
}