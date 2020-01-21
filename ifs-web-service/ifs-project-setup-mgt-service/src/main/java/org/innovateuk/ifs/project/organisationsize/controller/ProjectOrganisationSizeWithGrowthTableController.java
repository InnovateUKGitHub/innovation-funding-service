package org.innovateuk.ifs.project.organisationsize.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.populator.ApplicationYourOrganisationViewModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationsize.populator.ProjectOrganisationSizeWithGrowthTableFormPopulator;
import org.innovateuk.ifs.project.organisationsize.viewmodel.ProjectOrganisationSizeViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/with-growth-table")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ProjectOrganisationSizeWithGrowthTableController.class)
public class ProjectOrganisationSizeWithGrowthTableController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private ApplicationYourOrganisationViewModelPopulator viewModelPopulator;

    @Autowired
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    @Autowired
    private ProjectOrganisationSizeWithGrowthTableFormPopulator projectOrganisationSizeWithGrowthTableFormPopulator;

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
        return new ProjectOrganisationSizeViewModel(project, organisation.getName(), financesWithGrowthTable.getOrganisationSize(), financesWithGrowthTable.getAnnualTurnoverAtLastFinancialYear(), financesWithGrowthTable.getHeadCountAtLastFinancialYear());
    }

    private YourOrganisationWithGrowthTableForm formRequest(long projectId, long organisationId) {
        OrganisationFinancesWithGrowthTableResource financesWithGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess();
        return withGrowthTableFormPopulator.populate(financesWithGrowthTable);
    }
}