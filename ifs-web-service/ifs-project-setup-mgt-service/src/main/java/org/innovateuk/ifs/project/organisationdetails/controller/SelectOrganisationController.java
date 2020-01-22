package org.innovateuk.ifs.project.organisationdetails.controller;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;


import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.organisationdetails.form.SelectOrganisationForm;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.SelectOrganisationViewModel;
import org.innovateuk.ifs.project.projectdetails.controller.ProjectDetailsController;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will allow the user to select an organisation to view its details.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
@SecuredBySpring(value = "Controller", description = "Internal users can select an organisation to view details",
    securedType = OrganisationDetailsWithGrowthTableController.class)
public class SelectOrganisationController {

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private ProjectService projectService;

    private static final Log LOG = LogFactory.getLog(ProjectDetailsController.class);

    @GetMapping("/select")
    public String selectOrganisation(@ModelAttribute("form") SelectOrganisationForm form,
                                BindingResult bindingResult,
                                @PathVariable long projectId,
                                @PathVariable long competitionId,
                                Model model) {

        List<PartnerOrganisationResource> sortedOrganisations = getSortedOrganisations(projectId);
        ProjectResource projectResource = projectService.getById(projectId);

        model.addAttribute("model", new SelectOrganisationViewModel(projectId, projectResource.getName(), competitionId, sortedOrganisations));
        return "project/select-organisation";
    }

    @PostMapping("/select")
    public String selectedOrganisation(@ModelAttribute("form") @Valid SelectOrganisationForm form,
                                                 BindingResult bindingResult,
                                                 ValidationHandler validationHandler,
                                                 @PathVariable long projectId,
                                                 @PathVariable long competitionId,
                                                 Model model) {
        if (bindingResult.hasErrors()) {
            return selectOrganisation(form, bindingResult, projectId, competitionId, model);
        }
        return redirectToSelectedOrganisationPage(competitionId, projectId, form.getOrganisationId());
    }

    private String redirectToSelectedOrganisationPage(long competitionId, long projectId, long organisationId) {
        return format("redirect:/competition/%d/project/%d/organisation/%d/details",
            competitionId,
            projectId,
            organisationId);
    }

    private List<PartnerOrganisationResource> getSortedOrganisations(long projectId) {
        List<PartnerOrganisationResource> beforeOrdered = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess()
            .stream()
            .filter(po -> po.getCompletedSetup() == null)
            .collect(Collectors.toList());

        return new PrioritySorting<>(beforeOrdered, simpleFindFirst(beforeOrdered,
            PartnerOrganisationResource::isLeadOrganisation).get(), po -> po.getOrganisationName()).unwrap();
    }
}