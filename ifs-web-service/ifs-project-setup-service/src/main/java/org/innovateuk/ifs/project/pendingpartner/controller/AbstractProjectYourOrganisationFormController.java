package org.innovateuk.ifs.project.pendingpartner.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.ApplicationYourOrganisationViewModel;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationDetailsReadOnlyViewModel;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.pendingpartner.populator.YourOrganisationViewModelPopulator;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
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
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * The Controller for the "Your organisation" page in the project setup process
 * when a a new partner has been invited and a growth table is required.
 */
public abstract class AbstractProjectYourOrganisationFormController<F> extends AsyncAdaptor {
    private static final String TEMPLATE = "project/pending-partner-progress/your-organisation";
    @Autowired
    private YourOrganisationViewModelPopulator viewModelPopulator;
    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;
    @Autowired
    private OrganisationRestService organisationRestService;
    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    protected abstract String redirectToViewPage(long projectId, long organisationId);
    protected abstract F populateForm(long projectId, long organisationId);
    protected abstract String formFragment();
    protected abstract void update(long projectId, long organisationId, F form);


    @GetMapping
    @AsyncMethod
    @PreAuthorize("hasAnyAuthority('applicant')")
    @SecuredBySpring(value = "VIEW_YOUR_ORGANISATION", description = "Applicants and internal users can view the Your organisation page")
    public String viewPage(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            UserResource loggedInUser,
            Model model) {

        Future<ApplicationYourOrganisationViewModel> viewModelRequest = async(() ->
                getViewModel(projectId, organisationId, loggedInUser));

        Future<F> formRequest = async(() ->
                populateForm(projectId, organisationId));

        model.addAttribute("model", viewModelRequest);
        model.addAttribute("form", formRequest);
        model.addAttribute("formFragment", formFragment());

        return TEMPLATE;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE_YOUR_ORGANISATION", description = "Applicants can update their organisation details")
    public String updateWithGrowthTable(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            @ModelAttribute F form) {

        update(projectId, organisationId, form);
        return redirectToLandingPage(projectId, organisationId);
    }

    @PostMapping(params = {"mark-as-complete"})
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_YOUR_ORGANISATION_AS_COMPLETE", description = "Applicants can mark their organisation details as complete")
    public String markAsCompleteWithGrowthTable(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            UserResource loggedInUser,
            @Valid @ModelAttribute("form") F form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model) {

        Supplier<String> failureHandler = () -> {
            ApplicationYourOrganisationViewModel viewModel = getViewModel(projectId, organisationId, loggedInUser);
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            model.addAttribute("formFragment", formFragment());
            return TEMPLATE;
        };

        Supplier<String> successHandler = () -> {
            update(projectId, organisationId, form);
            validationHandler.addAnyErrors(pendingPartnerProgressRestService.markYourOrganisationComplete(projectId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureHandler, () -> redirectToLandingPage(projectId, organisationId));
        };

        return validationHandler.failNowOrSucceedWith(failureHandler, successHandler);
    }

    @PostMapping(params = {"mark-as-incomplete"})
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_YOUR_ORGANISATION_AS_INCOMPLETE", description = "Applicants can mark their organisation details as incomplete")
    public String markAsIncomplete(
            @PathVariable long projectId,
            @PathVariable long organisationId) {

        pendingPartnerProgressRestService.markYourOrganisationIncomplete(projectId, organisationId);
        return redirectToViewPage(projectId, organisationId);
    }

    private ApplicationYourOrganisationViewModel getViewModel(long projectId, long organisationId, UserResource user) {
        ApplicationYourOrganisationViewModel applicationYourOrganisationViewModel = viewModelPopulator.populate(projectId, organisationId, user);
        applicationYourOrganisationViewModel.setOrgDetailsViewModel(populateOrganisationDetails(organisationId));
        return  applicationYourOrganisationViewModel;
    }

    private String redirectToLandingPage(long projectId, long organisationId) {
        return format("redirect:/project/%d/organisation/%d/pending-partner-progress",
                projectId,
                organisationId);
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
                AddressResource addressResource = organisationAddressRestService.getOrganisationRegisterdAddressById(organisation.getId())
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
