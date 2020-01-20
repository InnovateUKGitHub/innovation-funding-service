package org.innovateuk.ifs.project.organisationsize.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationsize.form.ProjectOrganisationSizeWithGrowthTableForm;
import org.innovateuk.ifs.project.organisationsize.viewmodel.ProjectOrganisationSizeViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/with-growth-table")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ProjectOrganisationSizeWithGrowthTableController.class)
public class ProjectOrganisationSizeWithGrowthTableController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @GetMapping("/edit")
    public String editOrganisationSize(@ModelAttribute(value = "form", binding = false) ProjectOrganisationSizeWithGrowthTableForm form,
                                       BindingResult bindingResult,
                                       @PathVariable long projectId,
                                       @PathVariable long organisationId,
                                       Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationFinancesWithGrowthTableResource financesWithGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess();

        model.addAttribute("model", new ProjectOrganisationSizeViewModel(project, organisation.getName(), financesWithGrowthTable.getOrganisationSize(), financesWithGrowthTable.getAnnualTurnoverAtLastFinancialYear(), financesWithGrowthTable.getHeadCountAtLastFinancialYear()));
        model.addAttribute("form", form);

        return "project/edit-organisation-size-with-growth-table";
    }
}