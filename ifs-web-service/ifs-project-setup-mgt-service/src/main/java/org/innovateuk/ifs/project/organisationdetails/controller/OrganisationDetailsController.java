package org.innovateuk.ifs.project.organisationdetails.controller;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;


import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.form.OrganisationDetailsForm;
import org.innovateuk.ifs.project.organisationdetails.form.SelectOrganisationForm;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.OrganisationDetailsViewModel;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.SelectOrganisationViewModel;
import org.innovateuk.ifs.project.projectdetails.controller.ProjectDetailsController;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to organisation details for a project.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
public class OrganisationDetailsController {

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    private static final Log LOG = LogFactory.getLog(ProjectDetailsController.class);

    @GetMapping("/select")
    public String selectOrganisation(@ModelAttribute(value = "form", binding = false)
                                    SelectOrganisationForm form,
                                BindingResult bindingResult,
                                @PathVariable long projectId,
                                @PathVariable long competitionId,
                                Model model) {
        List<PartnerOrganisationResource> sortedOrganisations = sortedOrganisations(projectId);
        ProjectResource projectResource = projectService.getById(projectId);
        model.addAttribute("model", new SelectOrganisationViewModel(sortedOrganisations, projectId, projectResource.getName()));
        model.addAttribute("form", form);
        return "project/select-organisation";
    }

    private List<PartnerOrganisationResource> sortedOrganisations(long projectId) {
        List<PartnerOrganisationResource> beforeOrdered = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();
        return new PrioritySorting<>(beforeOrdered, simpleFindFirst(beforeOrdered,
            PartnerOrganisationResource::isLeadOrganisation).get(), po -> po.getOrganisationName()).unwrap();
    }

    @GetMapping("/{organisationId}/details")
    public String viewOrganisationDetails(@ModelAttribute(value = "form", binding = false)
                                              OrganisationDetailsForm form,
                                          BindingResult bindingResult,
                                          @PathVariable long projectId,
                                          @PathVariable long organisationId,
                                          @PathVariable long competitionId,
                                          Model model) {

        OrganisationDetailsViewModel viewModel = getViewModel(projectId, organisationId, competitionId);
        YourOrganisationWithGrowthTableForm orgSizeForm = getForm(projectId, organisationId);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        model.addAttribute("orgSizeForm", orgSizeForm);

        return "project/organisation-details";
    }

    private OrganisationDetailsViewModel getViewModel(long projectId, long organisationId, long competitionId) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        String projectName = project.getName();

        YourOrganisationViewModel yourOrganisationViewModel = new YourOrganisationViewModel(true, true, true);

        return new OrganisationDetailsViewModel(projectId, projectName, organisation, yourOrganisationViewModel);
    }

    private YourOrganisationWithGrowthTableForm getForm(long projectId, long organisationId) {
        return withGrowthTableFormPopulator.populate(projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess());
    }

    private boolean isIncludingGrowthTable(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).
            andOnSuccessReturn(competition -> TRUE.equals(competition.getIncludeProjectGrowthTable())).getSuccess();
    }
}