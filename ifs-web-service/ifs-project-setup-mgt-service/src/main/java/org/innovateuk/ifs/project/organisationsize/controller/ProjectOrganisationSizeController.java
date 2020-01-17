package org.innovateuk.ifs.project.organisationsize.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationsize.form.ProjectOrganisationSizeWithoutGrowthTableForm;
import org.innovateuk.ifs.project.organisationsize.viewmodel.ProjectOrganisationSizeViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.yourorganisation.service.YourOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/edit")
public class ProjectOrganisationSizeController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private YourOrganisationRestService yourOrganisationRestService;

    @GetMapping
    public String editOrganisationSize(@ModelAttribute(value = "form", binding = false) ProjectOrganisationSizeWithoutGrowthTableForm form,
                                       @PathVariable long projectId,
                                       @PathVariable long organisationId,
                                       Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationFinancesWithoutGrowthTableResource finances = yourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(project.getApplication(), organisationId).getSuccess();

        model.addAttribute("model", new ProjectOrganisationSizeViewModel(project, organisation.getName(), finances.getOrganisationSize(), finances.getTurnover(), finances.getHeadCount()));

        return "project/edit-organisation-size";
    }

}
