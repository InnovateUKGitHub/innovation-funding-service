package org.innovateuk.ifs.project.organisationdetails.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.OrganisationDetailsViewModel;
import org.innovateuk.ifs.project.yourorganisation.viewmodel.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

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
    private OrganisationRestService organisationRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    @Autowired
    private FinanceCheckService financeCheckService;

    @GetMapping
    public String viewOrganisationDetails(@PathVariable long competitionId,
                                          @PathVariable long projectId,
                                          @PathVariable long organisationId,
                                          Model model,
                                          UserResource loggedInUser) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        model.addAttribute("orgDetails", new OrganisationDetailsViewModel(project,
                competitionId,
                organisation,
                getAddress(organisation),
                project.isCollaborativeProject()));


        model.addAttribute("yourOrg", new ProjectYourOrganisationViewModel(false,
                false,
                false,
                projectId,
                project.getName(),
                organisationId,
                true,
                false,
                loggedInUser));

        model.addAttribute("form", getForm(projectId, organisationId));
        model.addAttribute("linkValid", getFinanceChecks(projectId));

        return "project/organisation-details-with-growth-table";
    }

    private AddressResource getAddress(OrganisationResource organisation) {
        return organisation.getAddresses().size() > 0
                ? organisation.getAddresses().get(0).getAddress()
                : new AddressResource("", "", "", "", "", "");
    }

    private YourOrganisationWithGrowthTableForm getForm(long projectId, long organisationId) {
        return withGrowthTableFormPopulator.populate(projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess());
    }

    private boolean getFinanceChecks(long projectId) {
        Optional<FinanceCheckSummaryResource> financeCheckSummary = financeCheckService.getFinanceCheckSummary(projectId).getOptionalSuccessObject();

        if (financeCheckSummary.isPresent()) {
            return financeCheckSummary.get().isAllEligibilityAndViabilityInReview();
        }
      return false;
    }
}