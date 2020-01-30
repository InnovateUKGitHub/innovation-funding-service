package org.innovateuk.ifs.project.organisationdetails.controller;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.organisationdetails.form.SelectOrganisationForm;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.SelectOrganisationViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This controller will allow the user to select an organisation to view its details.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
@SecuredBySpring(value = "Controller", description = "Project finance, competition admin, support, innovation lead and " +
    "stakeholder users can select an organisation to view details",
    securedType = OrganisationDetailsWithGrowthTableController.class)
public class SelectOrganisationController {

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private YourOrganisationRestService yourOrganisationRestService;

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    private static final Log LOG = LogFactory.getLog(SelectOrganisationController.class);

    @GetMapping("/select")
    public String selectOrganisation(@ModelAttribute(name = "form", binding = false) SelectOrganisationForm form,
                                @PathVariable long projectId,
                                @PathVariable long competitionId,
                                Model model) {
        List<PartnerOrganisationResource> beforeOrdered = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();

        List<PartnerOrganisationResource> sortedOrganisations = getSortedCompletedSetupOrganisations(projectId, beforeOrdered);
        ProjectResource project = projectService.getById(projectId);

        model.addAttribute("model", new SelectOrganisationViewModel(projectId, project.getName(), competitionId, sortedOrganisations));
        form.setOrganisationId(sortedOrganisations.get(0).getOrganisation());

        return beforeOrdered.size() == 1
            ? redirectToSelectedOrganisationPage(competitionId, projectId, beforeOrdered.get(0).getOrganisation())
            : "project/select-organisation";
    }

    @PostMapping("/select")
    public String selectedOrganisation(@ModelAttribute("form") @Valid SelectOrganisationForm form,
                                                 ValidationHandler validationHandler,
                                                 @PathVariable long projectId,
                                                 @PathVariable long competitionId,
                                                 Model model) {

        Supplier<String> failureView = () -> selectOrganisation(form, projectId, competitionId, model);
        Supplier<String> successView = () -> redirectToSelectedOrganisationPage(competitionId, projectId, form.getOrganisationId());
        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    private String redirectToSelectedOrganisationPage(long competitionId, long projectId, long organisationId) {
        boolean includeGrowthTable = yourOrganisationRestService.isIncludingGrowthTable(competitionId).getSuccess();
        return "redirect:" +
            String.format("/competition/%d/project/%d/organisation/%d/details/%s",
                competitionId,
                projectId,
                organisationId,
                includeGrowthTable ? "with-growth-table" : "without-growth-table");
    }

    private List<PartnerOrganisationResource> getSortedCompletedSetupOrganisations(long projectId, List<PartnerOrganisationResource> partners) {
        List<PartnerOrganisationResource> unorderedCompletedSetupPartners = getCompletedSetupOrganisations(projectId, partners);
        return new PrioritySorting<>(unorderedCompletedSetupPartners, simpleFindFirst(unorderedCompletedSetupPartners,
            PartnerOrganisationResource::isLeadOrganisation).get(), po -> po.getOrganisationName()).unwrap();
    }

    private List<PartnerOrganisationResource> getCompletedSetupOrganisations(long projectId, List<PartnerOrganisationResource> partners) {
        return partners.stream()
            .filter(po -> hasCompletedSetup(projectId, po))
            .collect(Collectors.toList());
    }

    private boolean hasCompletedSetup(long projectId, PartnerOrganisationResource partner) {
        Optional<PendingPartnerProgressResource> partnerProgress = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, partner.getOrganisation()).toOptionalIfNotFound().getSuccess();
        return !partnerProgress.isPresent() || partnerProgress.get().isCompleted();
    }
}