package org.innovateuk.ifs.project.organisationdetails.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.OrganisationDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.yourorganisation.viewmodel.ProjectYourOrganisationViewModel;
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
 * This controller will allow the user to view organisation details without a growth table.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation/{organisationId}/details/without-growth-table")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
@SecuredBySpring(value = "Controller", description = "Internal users can view organisation details",
        securedType = OrganisationDetailsWithoutGrowthTableController.class)
public class OrganisationDetailsWithoutGrowthTableController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private YourOrganisationWithoutGrowthTableFormPopulator withoutGrowthTableFormPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

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
        FinanceCheckSummaryResource financeCheckSummary = financeCheckService.getFinanceCheckSummary(projectId).getSuccess();

        boolean includeYourOrganisationSection = isIncludeYourOrganisationSection(competitionId, organisation);

        model.addAttribute("orgDetails", new OrganisationDetailsViewModel(project,
                competitionId,
                organisation,
                getAddress(organisation),
                project.isCollaborativeProject()));

        model.addAttribute("showYourOrg", includeYourOrganisationSection);
        model.addAttribute("linkValid", getFinanceChecks(projectId));

        if (includeYourOrganisationSection) {
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
        }
        return "project/organisation-details-without-growth-table";
    }

    private boolean isIncludeYourOrganisationSection(long competitionId, OrganisationResource organisation) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return competition.applicantShouldUseJesFinances(OrganisationTypeEnum.getFromId(organisation.getOrganisationType()))
                && !competition.getIncludeYourOrganisationSection()
                ? false : true;
    }

    private AddressResource getAddress(OrganisationResource organisation) {
        return organisation.getAddresses().size() > 0
                ? organisation.getAddresses().get(0).getAddress()
                : createNewAddress();
    }

    private AddressResource createNewAddress() {
        return new AddressResource("", "", "", "", "", "");
    }

    private YourOrganisationWithoutGrowthTableForm getForm(long projectId, long organisationId) {
        return withoutGrowthTableFormPopulator.populate(projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId).getSuccess());
    }

    private boolean getFinanceChecks(long projectId) {
        Optional<FinanceCheckSummaryResource> financeCheckSummary = financeCheckService.getFinanceCheckSummary(projectId).getOptionalSuccessObject();

        if (financeCheckSummary.isPresent()) {
            return financeCheckSummary.get().isAllEligibilityAndViabilityInReview();
        }
        return false;
    }
}