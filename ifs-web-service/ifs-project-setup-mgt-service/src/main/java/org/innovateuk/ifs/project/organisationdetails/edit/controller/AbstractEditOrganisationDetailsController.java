package org.innovateuk.ifs.project.organisationdetails.edit.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationDetailsReadOnlyViewModel;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.edit.viewmodel.ProjectOrganisationSizeViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationAddressRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

public abstract class AbstractEditOrganisationDetailsController<F> {
    private static final String TEMPLATE = "project/organisationdetails/edit-organisation-size";
    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    @Autowired
    private PublicContentItemRestService publicContentItemRestService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    @SecuredBySpring(value = "READ", description = "Ifs Admin and Project finance users can view edit organisation size page")
    public String view(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            Model model) {

        model.addAttribute("model", getViewModel(projectId, organisationId));
        model.addAttribute("form", form(projectId, organisationId));
        model.addAttribute("formFragment", formFragment());

        return TEMPLATE;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    @SecuredBySpring(value = "UPDATE_ORGANISATION_FUNDING_DETAILS", description = "Internal users can update organisation funding details")
    public String save(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            UserResource loggedInUser,
            @Valid @ModelAttribute("form") F form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model) {

        Supplier<String> failureHandler = () -> {
            model.addAttribute("model", getViewModel(projectId, organisationId));
            model.addAttribute("formFragment", formFragment());
            return TEMPLATE;
        };
        Supplier<String> successHandler = () -> redirectToOrganisationDetails(projectId, organisationId);

        return validationHandler.failNowOrSucceedWith(failureHandler,() -> {
             validationHandler.addAnyErrors(update(projectId, organisationId, loggedInUser.getId(), form));
             return validationHandler.failNowOrSucceedWith(failureHandler, successHandler);
        });
    }

    private ProjectOrganisationSizeViewModel getViewModel(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        PublicContentItemResource publicContentItem = publicContentItemRestService.getItemByCompetitionId(project.getCompetition()).getSuccess();

        boolean isMaximumFundingLevelConstant = competition.isMaximumFundingLevelConstant(
                organisation::getOrganisationTypeEnum,
                () -> grantClaimMaximumRestService.isMaximumFundingLevelConstant(competition.getId()).getSuccess());
        ProjectOrganisationSizeViewModel projectOrganisationSizeViewModel =  new ProjectOrganisationSizeViewModel(project,
                competition,
                organisation,
                isMaximumFundingLevelConstant,
                false,
                false,
                publicContentItem.getPublicContentResource().getHash());
        projectOrganisationSizeViewModel.setOrgDetailsViewModel(populateOrganisationDetails(organisationId));
        projectOrganisationSizeViewModel.setPartnerOrgDisplay(true);
        return projectOrganisationSizeViewModel;
    }

    protected abstract String redirectToOrganisationDetails(long projectId, long organisationId);

    protected abstract String formFragment();

    protected abstract F form(long projectId, long organisationId);

    protected abstract ServiceResult<Void> update(long projectId,
                                                  long organisationId,
                                                  long userId,
                                                  F form);

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