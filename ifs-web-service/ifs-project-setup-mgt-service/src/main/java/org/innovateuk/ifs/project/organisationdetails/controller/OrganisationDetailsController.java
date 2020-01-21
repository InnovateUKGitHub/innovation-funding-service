package org.innovateuk.ifs.project.organisationdetails.controller;

import static java.lang.Boolean.TRUE;
import java.util.List;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.form.OrganisationDetailsForm;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.OrganisationDetailsFinancesViewModel;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.OrganisationDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
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
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
public class OrganisationDetailsController {

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @GetMapping("/{organisationId}/details")
    public String viewOrganisationDetails(@ModelAttribute(value = "form", binding = false)
                                              OrganisationDetailsForm form,
                                          BindingResult bindingResult,
                                          @PathVariable long projectId,
                                          @PathVariable long organisationId,
                                          @PathVariable long competitionId,
                                          Model model) {

        OrganisationDetailsFinancesViewModel financesViewModel = getFinancesViewModel(projectId, organisationId, isIncludingGrowthTable(competitionId));
        OrganisationDetailsViewModel viewModel = getDetailsViewModel(projectId, organisationId, competitionId, financesViewModel);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);

        return "project/organisation-details";
    }

    private OrganisationDetailsFinancesViewModel getFinancesViewModel(long projectId, long organisationId, boolean includeGrowthTable) {
        return includeGrowthTable ?
            new OrganisationDetailsFinancesViewModel(projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess())
            :
            new OrganisationDetailsFinancesViewModel(projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId).getSuccess());
    }

    private boolean hasPartners(Long projectId) {
        return partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess().size() > 1;
    }

    private AddressResource getOrganisationAddress(OrganisationResource organisation) {
        AddressResource emptyAddress = new AddressResource("", "", "", "", "", "");

        List<OrganisationAddressResource> organisationAddressResources = organisation.getAddresses();
        if(organisationAddressResources.isEmpty()) {
            return emptyAddress;
        }

        return organisationAddressResources.get(0).getAddress();
    }

    private OrganisationDetailsViewModel getDetailsViewModel(long projectId, long organisationId, long competitionId, OrganisationDetailsFinancesViewModel financesViewModel) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        AddressResource address = getOrganisationAddress(organisation);

        return new OrganisationDetailsViewModel(project,
            competitionId,
            organisation,
            financesViewModel,
            address,
            hasPartners(projectId)
        );
    }

    private boolean isIncludingGrowthTable(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).
            andOnSuccessReturn(competition -> TRUE.equals(competition.getIncludeProjectGrowthTable())).getSuccess();
    }
}
