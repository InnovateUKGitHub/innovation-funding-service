package org.innovateuk.ifs.project.organisationdetails.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.populator.OrganisationDetailsViewModelPopulator;
import org.innovateuk.ifs.project.organisationsize.viewmodel.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will allow the user to view organisation details with a growth table.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation/{organisationId}/details/with-growth-table")
@SecuredBySpring(value = "Controller", description = "Internal users can view organisation details",
    securedType = OrganisationDetailsWithGrowthTableController.class)
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
public class OrganisationDetailsWithGrowthTableController extends AsyncAdaptor {

    @Autowired
    private ProjectRestService projectRestService;

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

        model.addAttribute("orgDetails", organisationDetailsViewModelPopulator.populate(competitionId, projectId, organisationId));
        model.addAttribute("orgSize", new ProjectYourOrganisationViewModel(false,
            false,
            false,
            projectId,
            project.getName(),
            organisationId,
            true,
            false));

        model.addAttribute("form", getForm(projectId, organisationId));

        return "project/organisation-details-with-growth-table";
    }

    private YourOrganisationWithGrowthTableForm getForm(long projectId, long organisationId) {
        return withGrowthTableFormPopulator.populate(projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess());
    }
}