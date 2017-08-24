package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.innovateuk.ifs.address.resource.AddressTypeEnum.OPERATING;
import static org.innovateuk.ifs.address.resource.AddressTypeEnum.REGISTERED;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;

/**
 * Provides methods for confirming and saving the organisation as an intermediate step in the registration flow.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL)
@PreAuthorize("permitAll")
public class OrganisationCreationSaveController extends AbstractOrganisationCreationController {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private InviteOrganisationRestService inviteOrganisationRestService;

    @GetMapping("/" + CONFIRM_ORGANISATION)
    public String confirmOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                 Model model,
                                 HttpServletRequest request) throws IOException {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        addSelectedOrganisation(organisationForm, model);
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccessObject());

        return TEMPLATE_PATH + "/" + CONFIRM_ORGANISATION;
    }

    @PostMapping("/save-organisation")
    public String saveOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                   Model model,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        OrganisationSearchResult selectedOrganisation = addSelectedOrganisation(organisationForm, model);
        AddressResource address = organisationForm.getAddressForm().getSelectedPostcode();

        OrganisationAddressResource organisationAddressResource = new OrganisationAddressResource();


        if (address != null && !organisationForm.isUseSearchResultAddress()) {
            organisationAddressResource.setAddress(address);
            organisationAddressResource.setAddressType(new AddressTypeResource(OPERATING.getOrdinal(), OPERATING.name()));
        }
        if (selectedOrganisation != null && selectedOrganisation.getOrganisationAddress() != null) {
            organisationAddressResource.setAddress(selectedOrganisation.getOrganisationAddress());
            organisationAddressResource.setAddressType(new AddressTypeResource(REGISTERED.getOrdinal(), REGISTERED.name()));
        }

        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(organisationForm.getOrganisationName());
        organisationResource.setOrganisationType(organisationForm.getOrganisationTypeId());
        organisationResource.setAddresses(Arrays.asList(organisationAddressResource));

        if (!OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationTypeId())) {
            organisationResource.setCompanyHouseNumber(organisationForm.getSearchOrganisationId());
        }

        organisationResource = saveNewOrganisation(organisationResource, request);
        registrationCookieService.saveToOrganisationIdCookie(organisationResource.getId(), response);
        
        return "redirect:" + RegistrationController.BASE_URL;
    }

    private OrganisationResource saveNewOrganisation(OrganisationResource organisationResource, HttpServletRequest request) {
        organisationResource = organisationService.saveForAnonymousUserFlow(organisationResource);
        linkOrganisationToInvite(organisationResource, request);
        return organisationResource;
    }

    /**
     * If current user is a invitee, then link the organisation that is created, to the InviteOrganisation.
     */
    private void linkOrganisationToInvite(OrganisationResource organisationResource, HttpServletRequest request) {
        Optional<String> cookieHash = registrationCookieService.getInviteHashCookieValue(request);
        if (cookieHash.isPresent()) {
            final OrganisationResource finalOrganisationResource = organisationResource;

            inviteRestService.getInviteByHash(cookieHash.get()).andOnSuccess(
                    s ->
                            inviteOrganisationRestService.getByIdForAnonymousUserFlow(s.getInviteOrganisation()).handleSuccessOrFailure(
                                    f -> restFailure(HttpStatus.NOT_FOUND),
                                    inviteOrganisation -> {
                                        if (inviteOrganisation.getOrganisation() == null) {
                                            inviteOrganisation.setOrganisation(finalOrganisationResource.getId());
                                            // Save the created organisation Id, so the next invitee does not have to..
                                            return inviteOrganisationRestService.put(inviteOrganisation);
                                        }
                                        return restFailure(HttpStatus.ALREADY_REPORTED);
                                    }
                            )
            );
        }
    }
}
