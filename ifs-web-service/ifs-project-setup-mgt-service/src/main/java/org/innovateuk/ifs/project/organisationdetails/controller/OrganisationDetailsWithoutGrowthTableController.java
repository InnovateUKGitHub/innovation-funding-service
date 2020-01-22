package org.innovateuk.ifs.project.organisationdetails.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.populator.OrganisationDetailsViewModelPopulator;
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
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation/{organisationId}/without-growth-table")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = OrganisationDetailsWithoutGrowthTableController.class)
public class OrganisationDetailsWithoutGrowthTableController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private OrganisationDetailsViewModelPopulator organisationDetailsViewModelPopulator;

    @Autowired
    private YourOrganisationWithoutGrowthTableFormPopulator withoutGrowthTableFormPopulator;

    @GetMapping()
    public String viewOrganisationSize(@PathVariable long competitionId,
                                       @PathVariable long projectId,
                                       @PathVariable long organisationId,
                                       Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        model.addAttribute("orgDetails", organisationDetailsViewModelPopulator.populate(competitionId, projectId, organisationId));
        model.addAttribute("orgSize", new ProjectYourOrganisationViewModel(false,
            false,
            false,
            project.getId(),
            project.getName(),
            organisationId,
            true,
            false));

        model.addAttribute("form", getForm(projectId, organisationId));

        return "project/organisation-details-without-growth-table";
    }

    private YourOrganisationWithoutGrowthTableForm getForm(long projectId, long organisationId) {
        return withoutGrowthTableFormPopulator.populate(projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId).getSuccess());
    }
}