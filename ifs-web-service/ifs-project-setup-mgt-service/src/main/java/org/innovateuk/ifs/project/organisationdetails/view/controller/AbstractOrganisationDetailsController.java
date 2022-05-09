package org.innovateuk.ifs.project.organisationdetails.view.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationDetailsReadOnlyViewModel;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.service.CompaniesHouseRestService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.organisationdetails.view.viewmodel.OrganisationDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.yourorganisation.viewmodel.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationAddressRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public abstract class AbstractOrganisationDetailsController<F> extends AsyncAdaptor {
    private static final String TEMPLATE = "project/organisationdetails/organisation-details";
    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompaniesHouseRestService companiesHouseRestService;

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    @Autowired
    private PublicContentItemRestService publicContentItemRestService;

    @GetMapping
    public String viewOrganisationDetails(@PathVariable long competitionId,
                                          @PathVariable long projectId,
                                          @PathVariable long organisationId,
                                          Model model,
                                          UserResource loggedInUser) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        PublicContentItemResource publicContentItem = publicContentItemRestService.getItemByCompetitionId(competitionId).getSuccess();

        boolean includeYourOrganisationSection = isIncludeYourOrganisationSection(competitionId, organisation);
        boolean ktpCompetition = competition.isKtp();
        OrganisationDetailsViewModel organisationDetailsViewModel = new OrganisationDetailsViewModel(project,
                competitionId,
                organisation,
                getAddress(organisation),
                project.isCollaborativeProject(),
                ktpCompetition);
        organisationDetailsViewModel.setOrgDetailsViewModel(populateOrganisationDetails((organisationId)));
        organisationDetailsViewModel.setPartnerOrgDisplay(true);
        model.addAttribute("organisationDetails", organisationDetailsViewModel);

        if (includeYourOrganisationSection) {
            boolean isMaximumFundingLevelConstant = competition.isMaximumFundingLevelConstant(
                    organisation::getOrganisationTypeEnum,
                    () -> grantClaimMaximumRestService.isMaximumFundingLevelConstant(competition.getId()).getSuccess());
            ProjectYourOrganisationViewModel projectYourOrganisationViewModel = new ProjectYourOrganisationViewModel(
                    project.getApplication(),
                    competition,
                    organisation,
                    isMaximumFundingLevelConstant,
                    false,
                    projectId,
                    project.getName(),
                    true,
                    loggedInUser,
                    isAllEligibilityAndViabilityInReview(projectId),
                    publicContentItem.getPublicContentResource().getHash());
            projectYourOrganisationViewModel.setOrgDetailsViewModel(populateOrganisationDetails((organisationId)));
            projectYourOrganisationViewModel.setPartnerOrgDisplay(true);
            model.addAttribute("yourOrganisation", projectYourOrganisationViewModel);

            model.addAttribute("form", getForm(projectId, organisationId));
            model.addAttribute("formFragment", formFragment());
        }
        return TEMPLATE;
    }

    protected abstract String formFragment();
    protected abstract F getForm(long projectId, long organisationId);

    private boolean isIncludeYourOrganisationSection(long competitionId, OrganisationResource organisation) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return competition.applicantShouldSeeYourOrganisationSection(organisation.getOrganisationTypeEnum());
    }

    private AddressResource getAddress(OrganisationResource organisation) {
        if (organisation.getCompaniesHouseNumber() != null) {
            Optional<OrganisationSearchResult> maybeResult = companiesHouseRestService.getOrganisationById(organisation.getCompaniesHouseNumber()).getOptionalSuccessObject();
            if (maybeResult.isPresent()) {
                return maybeResult.get().getOrganisationAddress();
            }
        }
        return createNewAddress();
    }

    private AddressResource createNewAddress() {
        return new AddressResource("", "", "", "", "", "");
    }

    private boolean isAllEligibilityAndViabilityInReview(long projectId) {
        Optional<FinanceCheckSummaryResource> financeCheckSummary = financeCheckService.getFinanceCheckSummary(projectId).getOptionalSuccessObject();

        if (financeCheckSummary.isPresent()) {
            return financeCheckSummary.get().isAllEligibilityAndViabilityInReview();
        }
        return false;
    }

    private YourOrganisationDetailsReadOnlyViewModel populateOrganisationDetails(long organisationId) {
        YourOrganisationDetailsReadOnlyViewModel yourOrganisationDetailsReadOnlyViewModel = new YourOrganisationDetailsReadOnlyViewModel();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        yourOrganisationDetailsReadOnlyViewModel.setOrganisationName(organisation.getName());
        yourOrganisationDetailsReadOnlyViewModel.setOrganisationType(organisation.getOrganisationTypeName());
        if (organisation.getCompanyRegistrationNumber() == null || organisation.getCompanyRegistrationNumber().isEmpty()) {
        yourOrganisationDetailsReadOnlyViewModel.setOrgDetailedDisplayRequired(false);
        yourOrganisationDetailsReadOnlyViewModel.setRegistrationNumber("");
        yourOrganisationDetailsReadOnlyViewModel.setAddressResource(null);
        yourOrganisationDetailsReadOnlyViewModel.setSicCodes(null);
    } else {
        yourOrganisationDetailsReadOnlyViewModel.setOrgDetailedDisplayRequired(true);
        yourOrganisationDetailsReadOnlyViewModel.setRegistrationNumber(organisation.getCompanyRegistrationNumber());
        AddressResource addressResource =  organisationAddressRestService.getOrganisationRegisterdAddressById(organisation.getId())
                .andOnSuccessReturn(addresses -> addresses.stream()
                        .findFirst()
                        .map(OrganisationAddressResource::getAddress)
                        .orElse(new AddressResource()))
                .getSuccess();

        yourOrganisationDetailsReadOnlyViewModel.setAddressResource(addressResource);
        if (organisation.getSicCodes() != null && !organisation.getSicCodes().isEmpty()) {
            yourOrganisationDetailsReadOnlyViewModel.setSicCodes(organisation.getSicCodes());
        }
    }
        return yourOrganisationDetailsReadOnlyViewModel;
}

}