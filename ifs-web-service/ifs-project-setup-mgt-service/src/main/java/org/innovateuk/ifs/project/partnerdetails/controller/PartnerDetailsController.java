package org.innovateuk.ifs.project.partnerdetails.controller;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;


import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.partnerdetails.form.PartnerDetailsForm;
import org.innovateuk.ifs.project.partnerdetails.form.SelectPartnerForm;
import org.innovateuk.ifs.project.partnerdetails.viewmodel.PartnerDetailsViewModel;
import org.innovateuk.ifs.project.partnerdetails.viewmodel.SelectPartnerViewModel;
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
 * This controller will handle all requests that are related to partner details for a project.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/partner")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
public class PartnerDetailsController {

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

    private static final Log LOG = LogFactory.getLog(ProjectDetailsController.class);

    //Put in alphabetical order except lead first
    @GetMapping("/select")
    public String selectPartner(@ModelAttribute(value = "form", binding = false)
                                    SelectPartnerForm form,
                                BindingResult bindingResult,
                                @PathVariable long projectId,
                                @PathVariable long competitionId,
                                Model model) {
        List<PartnerOrganisationResource> sortedPartners = sortedPartners(projectId);
        ProjectResource projectResource = projectService.getById(projectId);
        model.addAttribute("model", new SelectPartnerViewModel(sortedPartners, projectId, projectResource.getName()));
        model.addAttribute("form", form);
        return "project/select-partner";
    }

    private List<PartnerOrganisationResource> sortedPartners(long projectId) {
        List<PartnerOrganisationResource> beforeOrdered = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();
        return new PrioritySorting<>(beforeOrdered, simpleFindFirst(beforeOrdered,
            PartnerOrganisationResource::isLeadOrganisation).get(), po -> po.getOrganisationName()).unwrap();
    }

    @GetMapping("/{partnerId}/details")
    public String viewPartnerDetails(@ModelAttribute(value = "form", binding = false)
                                             PartnerDetailsForm form,
                                   BindingResult bindingResult,
                                    @PathVariable long projectId,
                                    @PathVariable long partnerId,
                                     @PathVariable long competitionId,
                                     Model model) {

        PartnerDetailsViewModel viewModel = getViewModel(projectId, partnerId, competitionId);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);

        return "project/partner-details";
    }

    private PartnerDetailsViewModel getViewModel(long projectId, long partnerId, long competitionId) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource partner = organisationRestService.getOrganisationById(partnerId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        String projectName = project.getName();

        // Look up competition isIncludeGrowthTable if included then populate with growth table
        OrganisationFinancesWithoutGrowthTableResource finances = projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable()
        OrganisationFinancesWithoutGrowthTableResource finances1 = projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable()

    }
}

