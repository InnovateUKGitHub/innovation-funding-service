package org.innovateuk.ifs.project.organisationdetails.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.populator.OrganisationDetailsViewModelPopulator;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.ProjectOrganisationSizeViewModel;
import org.innovateuk.ifs.project.organisationsize.model.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation/{organisationId}/with-growth-table")
@SecuredBySpring(value = "Controller", description = "Internal users can view organisation details", securedType = ProjectOrganisationSizeWithGrowthTableController.class)
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
public class ProjectOrganisationSizeWithGrowthTableController extends AsyncAdaptor {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private OrganisationDetailsViewModelPopulator organisationDetailsViewModelPopulator;

    @Autowired
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    @GetMapping()
    public String viewOrganisationSize(@PathVariable long competitionId,
                                       @PathVariable long projectId,
                                       @PathVariable long organisationId,
                                       Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationFinancesWithGrowthTableResource financesWithGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess();

        model.addAttribute("orgDetails", organisationDetailsViewModelPopulator.populate(competitionId, projectId, organisationId));
        model.addAttribute("orgSize", new ProjectYourOrganisationViewModel(false,
            false,
            false,
            project.getId(),
            project.getName(),
            organisationId,
            true,
            false));

        model.addAttribute("form", formRequest(projectId, organisationId));

        return "project/organisation-details-with-growth-table";
    }

    @GetMapping("/edit")
    public String editOrganisationSize(
        @PathVariable long projectId,
        @PathVariable long organisationId,
        Model model) {

        model.addAttribute("model", getViewModel(projectId, organisationId));
        model.addAttribute("form", formRequest(projectId, organisationId));

        return "project/edit-organisation-size-with-growth-table";
    }

    private ProjectOrganisationSizeViewModel getViewModel(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationFinancesWithGrowthTableResource financesWithGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess();
        return new ProjectOrganisationSizeViewModel(project,
            organisation.getName(),
            financesWithGrowthTable.getOrganisationSize(),
            financesWithGrowthTable.getAnnualTurnoverAtLastFinancialYear(),
            financesWithGrowthTable.getHeadCountAtLastFinancialYear(),
            false,
            false,
            false,
            false,
            false);
    }

    private YourOrganisationWithGrowthTableForm formRequest(long projectId, long organisationId) {
        OrganisationFinancesWithGrowthTableResource financesWithGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess();
        return withGrowthTableFormPopulator.populate(financesWithGrowthTable);
    }
}